import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import drivers.WebDrivers
import models.TestResults
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.scalatest.selenium.{WebBrowser, _}
import org.scalatest.{Matchers, concurrent}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by larryf on 5/28/2017.
  */

object Speedtest extends App with WebBrowser with WebDrivers with HtmlUnit with LazyLogging with concurrent.Eventually with Matchers {

  override def main(args: Array[String]): Unit = {
    Thread.currentThread.setName("ScalaTest-main")

    var results = ArrayBuffer[TestResults]()
    getAllDrivers.forEach( d => {
      if (isOkToRun(d)) {
        getAllSites.forEach( s => {
          val browser = d.getString("name")
          logger debug s"$browser ${s.getString("name")}"
          val t:TestWebSpeed = browser match {
            case "Chrome" =>
              new TestWebSpeedWithChrome
            case "Firefox" =>
              new TestWebSpeedWithFirefox
            case "Safari" =>
              new TestWebSpeedWithSafari
            case "InternetExplorer" =>
              new TestWebSpeedWithSInternetExplorer
            case other =>
              logger warn  s"Could not find class for $other"
              new TestWebSpeedWithChrome
          }
          results += t.runSpeedTest(s)
          t.quit
        })
      }
    })
    results.foreach(r =>
      logger info s"${r.browser} ${r.site} Completed ${formatDuration(0,r.duration)} - " +
        s"id=${r.resultId}-${r.protocol.getOrElse("")} ${r.ping} ${r.download}/${r.upload} " +
        s"${r.location} ${r.ipAddress} ${r.message.getOrElse("")}"
    )
  }

  def getAllSites = {
    val config = ConfigFactory.parseURL(this.getClass.getResource("application.conf"))
    assert(config != null)
    config.getConfigList("sites")
  }

  def isOkToRun(config: Config): Boolean = {
    if (config.hasPath("os")) {
      logger debug s"${config.getString("os")} === $os"
      if (os.startsWith(config.getString("os"))) true else false
    } else {
      true
    }
  }

  def getDriver(name: String): RemoteWebDriver = {
    name match {
      case "Chrome" => new ChromeDriver()
      case "Firefox" => new FirefoxDriver()
    }
  }

  def getAllDrivers = {
    val config = ConfigFactory.parseURL(this.getClass.getResource("application.conf"))
    assert(config != null)
    config.getConfigList("drivers")
  }

}


