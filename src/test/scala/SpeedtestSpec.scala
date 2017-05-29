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
        PatienceConfig(timeout = scaled(Span(60, Seconds)), interval = scaled(Span(1, Seconds)))
      // Cancel test when cannot access
      try goTo("http://beta.speedtest.net/") catch { case e: Throwable => cancel(e) }
      eventually {cssSelector("div.start-button").element.size.width should be > 0}
      clickOn(cssSelector("#container > div.main-content > div > div > div > div.pure-u-custom-speedtest > div.speedtest-container.main-row > div.start-button > a"))

      eventually { cssSelector("div.result-item.result-item-download.updated > div.result-label").element.text should be ("DOWNLOAD") }
      eventually { cssSelector("div.result-item.result-item-upload.updated > div.result-label").element.text should be ("UPLOAD") }

      println(s"Download: ${cssSelector(".download-speed").element.text}")
      println(s"Upload: ${cssSelector(".upload-speed").element.text}")

      close()
    }
  }

}

class SpeedtestSpecWithChrome extends SpeedtestSpec with Chrome
//class SpeedtestSpecWithSafari extends SpeedtestSpec with Safari
class SpeedtestSpecWithInternetExplorer extends SpeedtestSpec with InternetExplorer
class SpeedtestSpecWithFirefox extends SpeedtestSpec with Firefox
