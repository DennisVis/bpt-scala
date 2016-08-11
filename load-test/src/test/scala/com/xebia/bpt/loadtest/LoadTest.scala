package com.xebia.bpt.loadtest

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.xebia.bpt.persistence.Tables
import com.xebia.bpt.questions.{Question, QuestionsService}
import io.gatling.core.Predef._
import io.gatling.core.session.StaticStringExpression
import io.gatling.http.Predef._
import org.json4s.ShortTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

class LoadTest extends Simulation {

  implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[Question])))

  var cpds = new ComboPooledDataSource
  var db = Database.forDataSource(cpds)
  var questionsService = new QuestionsService(db)

  val nrUsers = 10
  val nrMinutes = 5
  val iterations = 10

  val feeder = Iterator.continually(Map("questionNames" -> Random.alphanumeric.take(20).mkString))

  object Data {
    def createQuestion(questionName: String): String = {
      write(Question(None, s"$questionName", "EN" -> s"Question $questionName", "NL" -> s"Vraag $questionName"))
    }

    def randomQuestionId: Int = Random.nextInt(3) + 1

    def cleanup(): String = {
      val future = for {
        _ <- db.run(Tables.Questions.filterNot(_.id === 1).filterNot(_.id === 2).filterNot(_.id === 3).delete)
        _ <- db.run(sql"SELECT setval('questions_id_seq', 3);".as[Int])
        _ <- db.run(sql"SELECT setval('labels_id_seq', 6);".as[Int])
      } yield ""

      Await.result(future, 10 minutes)
    }
  }

  object Overview {
    def create(index: Int) = exec(http("Create Question").post("/questions").body(StringBody(
      """{"name":"${questionNames""" + index + """}","labels":{"EN": "Question ${questionNames""" + index + """}","NL":"Vraag ${questionNames""" + index + """}"}}"""
    )))
    def multipleCreate = (1 to Math.round(iterations / 4)).map(i => create(i))

    def read = exec(http("Questions Overview").get("/questions"))
    def multipleReads = (1 to iterations).map(_ => read)

    def clean = exec(http(StaticStringExpression(Data.cleanup())).get(""))
  }

  object Detail {
    def read = exec(http("Read Question").get(s"/questions/${Data.randomQuestionId}"))
    def multipleReads = (1 to iterations).map(_ => read)
  }

  val httpConf = http
    .baseURL("http://bpt.io:3000")
    .acceptHeader("text/html,application/xhtml+xml,application/xml,application/json;q=0.9,*/*;q=0.8")
    .contentTypeHeader("application/json")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("nl-NL,nl;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val users = scenario("BPT REST Scenario")
    .feed(feeder, iterations)
    .exec(Overview.multipleReads)
    .exec(Overview.multipleCreate)
    .exec(Detail.multipleReads)

  setUp {
    users.inject(constantUsersPerSec(nrUsers).during(nrMinutes minutes))
  }.protocols(httpConf)

  after {
    Data.cleanup()
    cpds.close()
  }
}
