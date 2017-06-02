import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import drivers.WebDrivers
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.scalatest.selenium.{WebBrowser, _}
import org.scalatest.{Matchers, concurrent}

/**
  * Created by larryf on 5/28/2017.
  */

object Speedtest extends App with WebBrowser with WebDrivers with HtmlUnit with LazyLogging with concurrent.Eventually with Matchers {

  override def main(args: Array[String]): Unit = {
    Thread.currentThread.setName("ScalaTest-main")

    getAllDrivers.forEach( d => {
      if (isOkToRun(d)) {
        getAllSites.forEach( s => {
          val browser = d.getString("name")
          logger debug s"$browser ${s.getString("name")}"
          browser match {
            case "Chrome" =>
              val t = new TestWebSpeedWithChrome
              t.runSpeedTest(s)
              t.quit()
            case "Firefox" =>
              val t = new TestWebSpeedWithFirefox
              t.runSpeedTest(s)
              t.quit()
            case "Safari" =>
              val t = new TestWebSpeedWithSafari
              t.runSpeedTest(s)
              t.quit()
            case "InternetExplorer" =>
              val t = new TestWebSpeedWithSInternetExplorer
              t.runSpeedTest(s)
              t.quit()
            case other =>
              logger warn  s"Could not find class for $other"
          }
        })
      }
    })
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


