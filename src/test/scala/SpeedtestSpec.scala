import java.util

import drivers.ChromeFullScreen
import org.scalatest.selenium._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{FunSpecLike, _}

/**
  * Created by larryf on 5/27/2017.
  */
trait SpeedtestSpec extends FunSpecLike with Matchers with concurrent.Eventually {
  this: WebBrowser with Driver =>

  describe("beta.speedtest.net") {
    it("should compute download and upload speeds") {
      implicit val patienceConfig =
        PatienceConfig(timeout = scaled(Span(120, Seconds)), interval = scaled(Span(1, Seconds)))

      // Cancel test when cannot access
      try goTo("http://beta.speedtest.net") catch { case e: Throwable => cancel(e) }

      if (webDriver.getClass.getSimpleName.equals("ChromeDriver")) {
        val tabs2 = new util.ArrayList[String](webDriver.getWindowHandles)
        println(tabs2.size)
        tabs2.forEach(t => println(t))
        println(webDriver.getCurrentUrl)

        webDriver.switchTo().window(tabs2.get(1))
        webDriver.close()
        webDriver.switchTo().window(tabs2.get(0))
      }

      println("Waiting for start button")
      eventually {cssSelector(".start-button").element.size.width should be > 0}
      eventually {cssSelector(".start-text").element.text.toUpperCase should be ("GO!")}

      println("Clicking GO! button")
//      clickOn(cssSelector(".start-button > a"))
      clickOn(cssSelector(".start-text"))

      try {
        eventually { cssSelector(".start-text").element.text.toUpperCase should be ("AGAIN")}

        println(s"Ping: ${cssSelector("#ping-value").element.text} " +
          s"${cssSelector("div.result-data > span.result-data-unit").element.text}")
        println(s"Download: ${cssSelector(".result-data-value.download-speed").element.text} " +
          s"${cssSelector(".result-item-download .result-data-unit").element.text}")
        println(s"Upload: ${cssSelector(".result-data-value.upload-speed").element.text} " +
          s"${cssSelector(".result-item-upload .result-data-unit").element.text}")

        println(s"Location: ${cssSelector(".server-display .location .name").element.text}")
        println(s"IP Address: ${cssSelector(".ip-address").element.text}")
      } catch {
        case e: Exception =>
          println(e.getMessage)
          cancel(e)
      }
      finally
      {
        // your scala code here, such as to close a database connection
        quit()
      }
    }
  }

}

class SpeedtestSpecWithChrome extends SpeedtestSpec with ChromeFullScreen
class SpeedtestSpecWithSafari extends SpeedtestSpec with Safari
class SpeedtestSpecWithInternetExplorer extends SpeedtestSpec with InternetExplorer
class SpeedtestSpecWithFirefox extends SpeedtestSpec with Firefox
