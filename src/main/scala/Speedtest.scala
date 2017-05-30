import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest._
import org.scalatest.selenium.{WebBrowser, _}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by larryf on 5/28/2017.
  */

class Speedtest extends FlatSpec with WebBrowser with concurrent.Eventually {
}

object Speedtest extends App with Chrome with WebBrowser {

  val log:Logger = LoggerFactory.getLogger(getClass.getCanonicalName)

  override def main(args: Array[String]): Unit = {
    Thread.currentThread.setName("ScalaTest-main")
    println(System.getProperty("os.name"))

    log info s"Hello from $getCurrentDirectory"

    getAllSites.forEach( s =>
      log info s.getString("name")
//      runSpeedTests(s)
    )
  }

  def runSpeedTests(config: Config) = {
    //    implicit val patienceConfig =
    //      PatienceConfig(timeout = scaled(Span(60, Seconds)), interval = scaled(Span(1, Seconds)))
    // Cancel test when cannot access
//    try goTo("http://speedtest.xfinity.com") catch { case e: Throwable => cancel(e) }
    goTo("http://speedtest.xfinity.com")
    close()
  }

  def getAllSites = {
    val config = ConfigFactory.parseURL(this.getClass.getResource("application.conf"))
    assert(config != null)

    config.getConfigList("sites") // getList("sites")
  }

  def getCurrentDirectory = new java.io.File(".").getCanonicalPath

}
