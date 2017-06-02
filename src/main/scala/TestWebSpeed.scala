
import java.util

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import drivers.ChromeFullScreen
import models.TestResults
import org.joda.time.{Interval, Period}
import org.scalatest.selenium._
import org.scalatest.time.{Minutes, Seconds, Span}
import org.scalatest.{Matchers, concurrent}

/**
  * Created by lfinke200 on 5/31/17.
  */
trait TestWebSpeed extends LazyLogging with Matchers with concurrent.Eventually {
  this: WebBrowser with Driver =>

  override implicit val patienceConfig = PatienceConfig(timeout = scaled(Span(2, Minutes)), interval = scaled(Span(1, Seconds)))

  def runSpeedTest(config: Config): Unit = {
      val t0 = System.nanoTime()
      try {

        startSpeedTest(config)

        waitForTestToStart(config)

        waitForTestToComplete(config)

        val r = getTestResults(config)

        logger info s"${webDriver.getClass.getSimpleName} ${config.getString("name")} Completed ${formatDuration(t0, System.nanoTime())} s: " +
          s"${r.protocol.getOrElse("")} ${r.ping} ${r.download}/${r.upload} ${r.location} ${r.ipAddress}"
      } catch {
        case e: Throwable =>
          logger error s"${webDriver.getClass.getSimpleName} ${config.getString("name")} Completed failed ${formatDuration(t0, System.nanoTime())} s: ${e.getMessage}"
      } finally {
        quit()
      }
    }

  def startSpeedTest(config: Config): Unit = {
    logger info s"${webDriver.getClass.getSimpleName} ${config.getString("name")} Started - ${config.getString("url")}"
    goTo(config.getString("url"))
  }

  def waitForTestToStart(config: Config): Unit = {
    logger info s"   Waiting for ${config.getString("name")} ${config.getString("startButton")}"
    eventually {cssSelector(config.getString("startButton")).element.size.width should be > 0}
    if (config.hasPath("startButtonText")) {
      eventually { cssSelector(config.getString("startButtonTextSelector")).element.text.toUpperCase should be
        (config.getString("startButtonText").toUpperCase) }
    }
    logger info s"      clicking on ${config.getString("startButtonClick")} button"
    clickOn(cssSelector(config.getString("startButtonClick")))
  }

  private def getTestResults(config: Config): TestResults = {
    val ping = s"${getSpeedText(config, "pingSpeed")} ${getSpeedText(config, "pingSpeedUnit")}"
    val dl = s"${getSpeedText(config, "downloadSpeed")} ${getSpeedText(config, "downloadSpeedUnit")}"
    val ul = s"${getSpeedText(config, "uploadSpeed")} ${getSpeedText(config, "uploadSpeedUnit")}"
    val location = getSpeedText(config, "location")
    val ipAddress = if (config.hasPath("ipAddress")) {getSpeedText(config, "ipAddress")} else { "" }
    val protocol = if (config.hasPath("protocol")) {getSpeedText(config, "protocol")} else { "" }
    TestResults(ping, dl, ul, location, ipAddress, Some(protocol))
  }

  private def waitForTestToComplete(config: Config): Unit = {
    logger debug s"      waiting for test to complete"
    eventually {upDownLength should be > 0}

    def upDownLength: Int = {
      val r = "[0-9\\.]+".r
      r.findFirstIn(getSpeedText(config, "downloadSpeed")).getOrElse("").length.min(r.findFirstIn(getSpeedText(config, "uploadSpeed)"))
        .getOrElse("").length)
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

  private def formatDuration(t0: Long, t1: Long): String = {
    val interval: Interval = new Interval(t0/1000000, t1/1000000)
    val period: Period = interval.toPeriod
    if (period.getHours > 0) {
      f"${period.getHours}%02d:${period.getMinutes}%02d:${period.getSeconds}%02d.${period.getMillis}"
    } else {
      if (period.getMinutes > 0) {
        f"${period.getMinutes}%02d:${period.getSeconds}%02d.${period.getMillis}"
      } else {
        f"${period.getSeconds}%d.${period.getMillis}"
      }
    }
  }
}

trait ChromeBrowser extends TestWebSpeed with ChromeFullScreen {
  override def startSpeedTest(config: Config): Unit = {
    super.startSpeedTest(config)
    val tabs = new util.ArrayList[String](webDriver.getWindowHandles)
    webDriver.switchTo().window(tabs.get(1))
    webDriver.close()
    webDriver.switchTo().window(tabs.get(0))
  }
}

//class TestWebSpeedWithChrome extends TestWebSpeed with Chrome
class TestWebSpeedWithChrome extends TestWebSpeed with ChromeBrowser
class TestWebSpeedWithSafari extends TestWebSpeed with Safari
class TestWebSpeedWithSInternetExplorer extends TestWebSpeed with InternetExplorer
class TestWebSpeedWithFirefox extends TestWebSpeed with Firefox
