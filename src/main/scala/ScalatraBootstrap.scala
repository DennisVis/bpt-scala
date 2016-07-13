import com.mchange.v2.c3p0.ComboPooledDataSource
import com.xebia.bpt._
import com.xebia.bpt.questions.{QuestionsService, QuestionsServlet}
import org.scalatra._
import javax.servlet.ServletContext
import slick.driver.PostgresDriver.api._

import org.slf4j.LoggerFactory

class ScalatraBootstrap extends LifeCycle {

  val logger = LoggerFactory.getLogger(getClass)

  val cpds = new ComboPooledDataSource
  logger.info("Created c3p0 connection pool")

  override def init(context: ServletContext) {
    val db = Database.forDataSource(cpds)
    val questionsService = new QuestionsService(db)
    context.mount(new BPTServlet, "/*")
    context.mount(new QuestionsServlet(questionsService), "/questions/*")
  }

  private def closeDbConnection() {
    logger.info("Closing c3po connection pool")
    cpds.close()
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection()
  }
}
