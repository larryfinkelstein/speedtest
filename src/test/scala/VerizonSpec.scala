import org.scalatest.selenium._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{FunSpecLike, _}

/**
  * Created by larryf on 5/27/2017.
  */
trait VerizonSpec extends FunSpecLike with Matchers with concurrent.Eventually {
  this: WebBrowser with Driver =>

  describe("www.verizon.com/speedtest/") {
    it("should compute download and upload speeds") {
      implicit val patienceConfig =
        PatienceConfig(timeout = scaled(Span(60, Seconds)), interval = scaled(Span(1, Seconds)))
      // Cancel test when cannot access
      try goTo("https://www.verizon.com/speedtest/") catch { case e: Throwable => cancel(e) }
      eventually {cssSelector("#startButton").element.size.width should be > 0}
      clickOn(cssSelector("#startButton"))

      eventually { cssSelector("#results-download > span.results-type").element.text should be ("DOWNLOAD") }
      eventually { cssSelector("#results-upload > span.results-type").element.text should be ("UPLOAD") }

      println(s"Download: ${cssSelector("#results-download-int").element.text}")
      println(s"Upload: ${cssSelector("#results-upload-int").element.text}")

      close()
    }
  }

}

class VerizonSpecWithChrome extends VerizonSpec with Chrome
//class VerizonSpecWithSafari extends VerizonSpec with Safari
class VerizonSpecWithInternetExplorer extends VerizonSpec with InternetExplorer
class VerizonSpecWithFirefox extends VerizonSpec with Firefox
