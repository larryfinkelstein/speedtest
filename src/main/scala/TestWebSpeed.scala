
import java.util

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import drivers.{ChromeFullScreen, WebDrivers}
import models.TestResults
import org.scalatest.selenium._
import org.scalatest.time.{Minutes, Seconds, Span}
import org.scalatest.{Matchers, concurrent}

/**
  * Created by lfinke200 on 5/31/17.
  */
trait TestWebSpeed extends LazyLogging with Matchers with concurrent.Eventually with WebDrivers {
  this: WebBrowser with Driver =>

  override implicit val patienceConfig = PatienceConfig(timeout = scaled(Span(3, Minutes)), interval = scaled(Span(1, Seconds)))

  def runSpeedTest(implicit config: Config): TestResults = {
    val startTime = System.nanoTime()
    try {

      startSpeedTest

      waitForTestToStart

      waitForTestToComplete

      getTestResults(startTime)

    } catch {
      case e: Throwable =>
        TestResults(webDriver.getClass.getSimpleName,
          config.getString("name"),
          getOsVersion, "failure result", "ping", "dl", "ul", "location", "ipAddress",
          System.nanoTime() - startTime, None, Some(e.getMessage)
        )
    } finally {
      quit()
    }
  }

  def quit: Unit = {
    webDriver.quit()
  }

  def startSpeedTest(implicit config: Config): Unit = {
    logger info s"${webDriver.getClass.getSimpleName} ${config.getString("name")} Started - ${config.getString("url")}"
    goTo(config.getString("url"))
  }

  def waitForTestToStart(implicit config: Config): Unit = {
    logger info s"   Waiting for ${config.getString("name")} ${config.getString("startButton")}"
    eventually {cssSelector(config.getString("startButton")).element.size.width should be > 0}
    if (config.hasPath("startButtonText")) {
      eventually { cssSelector(config.getString("startButtonTextSelector")).element.text.toUpperCase should be
        config.getString("startButtonText").toUpperCase }
    }
    logger info s"      clicking on ${config.getString("startButtonClick")} button"
    clickOn(cssSelector(config.getString("startButtonClick")))
  }

  private def getTestResults(startTime: Long)(implicit config: Config): TestResults = {
    //FIXME: pingSpeedUnit for Verizon needs to be trimmed
    val ping = s"${getSpeedText(config, "pingSpeed")} ${getSpeedText(config, "pingSpeedUnit")}"
    val dl = s"${getSpeedText(config, "downloadSpeed")} ${getSpeedText(config, "downloadSpeedUnit")}"
    val ul = s"${getSpeedText(config, "uploadSpeed")} ${getSpeedText(config, "uploadSpeedUnit")}"
    val location = getSpeedText(config, "location")
    val ipAddress = if (config.hasPath("ipAddress")) {getSpeedText(config, "ipAddress")} else { "" }
    val protocol = if (config.hasPath("protocol")) {getSpeedText(config, "protocol")} else { "" }
    val resultId = if (config.hasPath("resultId")) {cssSelector(config.getString("resultId")).element.text} else "none"
    TestResults(webDriver.getClass.getSimpleName, config.getString("name"), getOsVersion,
      resultId, ping, dl, ul, location, ipAddress, System.nanoTime()-startTime,
      Some(protocol), None)
  }

  private def waitForTestToComplete(implicit config: Config): Unit = {
    logger debug s"      waiting for test to complete"
    eventually {upDownLength should be > 0}

    def upDownLength: Int = {
      val r = "[0-9\\.]+".r
      val d = r.findFirstIn(getSpeedText(config, "downloadSpeed")).getOrElse("")
      val u = r.findFirstIn(getSpeedText(config, "uploadSpeed")).getOrElse("")
      d.length.min(u.length)
    }
  }

  private def getSpeedText(c: Config, t: String): String = {
    val s = cssSelector(c.getString(t)).element.text
    logger trace s"$t(${c.getString(t)}) == $s"
    if (s.trim.isEmpty /* || s.trim.equals("--") */) {
      val v6Path = s"${t}V6"
      if (c.hasPath(v6Path)) {
        val sV6 = cssSelector(c.getString(v6Path)).element.text
        logger trace s"$t(${c.getString(v6Path)}) == $sV6"
        sV6
      } else {
        s
      }
    } else {
      s
    }
  }

}

trait ChromeBrowser extends TestWebSpeed with ChromeFullScreen {
  override def startSpeedTest(implicit config: Config): Unit = {
    super.startSpeedTest(config)
    val tabs = new util.ArrayList[String](webDriver.getWindowHandles)
    webDriver.switchTo().window(tabs.get(1))
    webDriver.close()
    webDriver.switchTo().window(tabs.get(0))
  }
  override def quit: Unit = webDriver.quit()
}

//class TestWebSpeedWithChrome extends TestWebSpeed with Chrome
class TestWebSpeedWithChrome extends TestWebSpeed with ChromeBrowser
class TestWebSpeedWithSafari extends TestWebSpeed with Safari
class TestWebSpeedWithSInternetExplorer extends TestWebSpeed with InternetExplorer
class TestWebSpeedWithFirefox extends TestWebSpeed with Firefox
