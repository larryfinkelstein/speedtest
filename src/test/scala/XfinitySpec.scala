import org.scalatest.selenium._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{FunSpecLike, _}

/**
  * Created by larryf on 5/27/2017.
  */
trait XfinitySpec extends FunSpecLike with Matchers with concurrent.Eventually {
  this: WebBrowser with Driver =>

  describe("speedtest.xfinity.com") {
    it("should compute download and upload speeds") {
      implicit val patienceConfig =
        PatienceConfig(timeout = scaled(Span(60, Seconds)), interval = scaled(Span(1, Seconds)))
      // Cancel test when cannot access
      try goTo("http://speedtest.xfinity.com") catch { case e: Throwable => cancel(e) }

      eventually { cssSelector("#action-start-test").element.text should be ("Start Test") }
      clickOn(cssSelector("#action-start-test"))

      eventually { cssSelector("div.text.text--title").element.text should be ("Speed Results") }

      println(s"Download: ${cssSelector("#finalResultsIPv4-download-value").element.text}")
      println(s"Upload: ${cssSelector("#finalResultsIPv4-upload-value").element.text}")

      close()
    }
  }

}

class XfinitySpecWithChrome extends XfinitySpec with Chrome
//class XfinitySpecWithSafari extends XfinitySpec with Safari
class XfinitySpecWithInternetExplorer extends XfinitySpec with InternetExplorer
class XfinitySpecWithFirefox extends XfinitySpec with Firefox
