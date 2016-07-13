package com.xebia.bpt.questions

import com.xebia.bpt.{NotFoundException, FailureException, BptScalaStack}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, AsyncResult, FutureSupport, NoContent, InternalServerError, NotFound,
ActionResult, Ok}
import org.scalatra.json.JacksonJsonSupport
import org.slf4j.LoggerFactory
import slick.driver.PostgresDriver.api._
import scala.util.{Success => Good}
import scala.util.{Failure => Bad}

import com.xebia.bpt.persistence.Tables._

import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

case class Question(id: Option[Int], name: String, labels: Map[String, String])
object Question {
  def apply(id: Option[Int], name: String, labels: (String, String)*): Question =
    Question(id, name, Map(labels: _*))
}

class QuestionsService(val db: Database) {

  private def fetchQuestions(questionsQuery: Query[Questions, QuestionsRow, Seq]): DBIO[Iterable[Question]] = {
    val query = for {
      ((question, _), label) <- questionsQuery join LabelsPerQuestion on (_.id === _.questionId) join Labels on (_._2.labelId === _.id)
    } yield (question, label)

    query.result.map(_.groupBy(_._1).map {
      case (questionsRow, items) =>
        Question(Some(questionsRow.id), questionsRow.name, items.map(i => i._2.language -> i._2.value): _*)
    })
  }

  private def insertLabels(questionId: Int, labels: (String, String)*): DBIO[Seq[Int]] = {
    DBIO.sequence(labels.map {
      case (language, value) =>
        val labelQuery =
          Labels.map(l => l.language -> l.value) returning Labels.map(_.id) forceInsert(language -> value)
        labelQuery.flatMap { labelId =>
          LabelsPerQuestion.map(lpq => lpq.questionId -> lpq.labelId).forceInsert(questionId, labelId)
        }
    })
  }

  def all: Future[Iterable[Question]] = db.run(fetchQuestions(Questions))

  def create(question: Question): Future[Question] = {
    val questionInsertAction = Questions.map(_.name) returning Questions.map(_.id) forceInsert question.name
    val labelsInsertAction = questionInsertAction.flatMap { questionId =>
      insertLabels(questionId, question.labels.toSeq: _*).map { statuses =>
        question.copy(id = Some(questionId))
      }
    }

    db.run(labelsInsertAction.transactionally)
  }

  def read(id: Int): Future[Option[Question]] = {
    val query = Questions.filter(_.id === id)
    db.run(fetchQuestions(query)).map(_.headOption)
  }

  def update(questionId: Int, question: Question): Future[Question] = {
    val deleteLabelsAction = LabelsPerQuestion.filter(_.questionId === questionId).delete
    val updateQuestionAction = Questions.filter(_.id === questionId).map(_.name).update(question.name)
    val insertLabelsAction = insertLabels(questionId, question.labels.toSeq: _*)

    db.run((deleteLabelsAction >> updateQuestionAction >> insertLabelsAction).transactionally).flatMap { sts =>
      if (sts.forall(s => s > 0)) Future.successful(question)
      else Future.failed(FailureException(s"Could not update question with id ${question.id}"))
    }
  }

  def delete(id: Int): Future[Int] = {
    val deleteQuestionAction = Questions.filter(_.id === id).delete

    db.run(deleteQuestionAction).flatMap { status =>
      if (status > 0) Future.successful(status)
      else Future.failed(FailureException(s"Could not delete question with id $id"))
    }
  }
}

class QuestionsServlet(service: QuestionsService) extends BptScalaStack
  with FutureSupport
  with JacksonJsonSupport
  with CorsSupport {
  override protected implicit val executor: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  val logger = LoggerFactory.getLogger(getClass)

  private def handleError(e: Throwable): ActionResult = e match {
    case NotFoundException(message) => NotFound(reason = message)
    case e @ _ =>
      logger.error("Error occurred during request processing", e)
      InternalServerError()
  }

  private def handleResult(result: Future[Question]): Future[ActionResult] = {
    val p = Promise[ActionResult]()
    result.onComplete {
      case Good(q) => p.success(Ok(q))
      case Bad(e) => p.success(handleError(e))
    }
    p.future
  }

  before() {
    contentType = formats("json")
  }

  get("/") {
    new AsyncResult {
      val is = service.all
    }
  }

  post("/") {
    val question = parsedBody.extract[Question]
    new AsyncResult {
      val is = handleResult(service.create(question))
    }
  }

  get("/:id") {
    val p = Promise[ActionResult]()
    val id = params("id").toInt
    service.read(id).onComplete {
      case Good(Some(q)) => p.success(Ok(q))
      case Good(None) => p.success(NotFound())
      case Bad(e) => p.success(handleError(e))
    }

    new AsyncResult {
      val is = p.future
    }
  }

  put("/:id") {
    val id = params("id").toInt
    val question = parsedBody.extract[Question].copy(id = Some(id))
    new AsyncResult {
      val is = handleResult(service.update(id, question))
    }
  }

  delete("/:id") {
    val p = Promise[ActionResult]()
    val id = params("id").toInt
    service.delete(id).onComplete {
      case Good(s) if s > 0 => p.success(NoContent())
      case Good(s) => p.success(InternalServerError())
      case Bad(e) => p.success(handleError(e))
    }
    new AsyncResult {
      val is = p.future
    }
  }
}
