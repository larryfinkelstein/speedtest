
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.{Interval, Period}
import org.scalatest.selenium._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, concurrent}

/**
  * Created by lfinke200 on 5/31/17.
  */
trait TestWebSpeed extends LazyLogging with Matchers with concurrent.Eventually {
  this: WebBrowser with Driver =>

    def runSpeedTest(config: Config): Unit = {
      val t0 = System.nanoTime()
      implicit val patienceConfig = PatienceConfig(timeout = scaled(Span(60, Seconds)), interval = scaled(Span(1, Seconds)))
      // Cancel test when cannot access
      try {
        logger info s"   ${config.getString("name")} Started - ${config.getString("url")}"
        goTo(config.getString("url"))

//        webDriver.manage().window().fullscreen()
        eventually {cssSelector(config.getString("startButton")).element.size.width should be > 0}
        if (config.hasPath("startButtonText")) {
          eventually { cssSelector(config.getString("startButtonTextSelector")).element.text.toUpperCase should be
            (config.getString("startButtonText").toUpperCase) }
          logger info s"      clicking on ${config.getString("startButtonText")} button"
        }
        clickOn(cssSelector(config.getString("startButtonClick")))

        //".start-text"
        eventually { cssSelector(".start-text").element.text.toUpperCase should be ("AGAIN")}
        logger debug s"${config.getString("startButtonTextSelector")} == ${config.getString("startButtonComplete")}"
        eventually { cssSelector(config.getString("startButtonTextSelector")).element.text.toUpperCase should be
          (config.getString("startButtonComplete").toUpperCase)}
//        eventually { cssSelector(config.getString("downloadCompleteDiv")).element.text should be (config.getString("downloadCompleteText")) }
//        eventually { cssSelector(config.getString("uploadCompleteDiv")).element.text should be (config.getString("uploadCompleteText")) }

        val ping = s"${getSpeedText(config, "pingSpeed")} ${getSpeedText(config, "pingSpeedUnit")}"
        val dl = s"${getSpeedText(config, "downloadSpeed")} ${getSpeedText(config, "downloadSpeedUnit")}"
        val ul = s"${getSpeedText(config, "uploadSpeed")} ${getSpeedText(config, "uploadSpeedUnit")}"
        val location = getSpeedText(config, "location")
        val ipAddress = getSpeedText(config, "ipAddress")

        val t1 = System.nanoTime()

//        logger info (s"Ping: ${cssSelector("#ping-value").element.text} " +
//          s"${cssSelector("div.result-data > span.result-data-unit").element.text}")
        logger info s"   ${config.getString("name")} Completed ${formatDuration(t0, t1)} s: $ping $dl/$ul $location $ipAddress"
      } catch {
        case e: Throwable =>
          logger error e.getMessage
      } finally {
        close()
      }
    }

    private def getSpeedText(c: Config, t: String): String = {
      val s = cssSelector(c.getString(t)).element.text
      logger debug s"$t(${c.getString(t)}) == $s"
      if (s.trim.isEmpty) {
        val v6Path = s"${t}V6"
        if (c.hasPath(v6Path)) {
          val sV6 = cssSelector(c.getString(v6Path)).element.text
          logger debug s"$t(${c.getString(v6Path)}) == $sV6"
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

class TestWebSpeedWithChrome extends TestWebSpeed with Chrome
class TestWebSpeedWithSafari extends TestWebSpeed with Safari
class TestWebSpeedWithSInternetExplorer extends TestWebSpeed with InternetExplorer
class TestWebSpeedWithFirefox extends TestWebSpeed with Firefox
