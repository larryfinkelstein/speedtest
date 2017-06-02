import java.util

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
        PatienceConfig(timeout = scaled(Span(120, Seconds)), interval = scaled(Span(1, Seconds)))
      // Cancel test when cannot access
      try goTo("https://www.verizon.com/speedtest/") catch { case e: Throwable => cancel(e) }

      if (webDriver.getClass.getSimpleName.equals("ChromeDriver")) {
        val tabs2 = new util.ArrayList[String](webDriver.getWindowHandles)
        webDriver.switchTo().window(tabs2.get(1))
        webDriver.close()
        webDriver.switchTo().window(tabs2.get(0))
      }

      eventually {cssSelector("#startButton").element.size.width should be > 0}
      clickOn(cssSelector("#startButton"))

//      eventually { cssSelector("#results-download > span.results-type").element.text should be ("DOWNLOAD") }
//      eventually { cssSelector("#results-upload > span.results-type").element.text should be ("UPLOAD") }

      println("Waiting for test to complete")

      eventually {upDownLength should be > 0}

      println(s"Ping: ${cssSelector("#latency > span").element.text} " +
        s"${cssSelector("#latency").element.text.split(" ").lastOption.getOrElse("").toLowerCase}")
      println(s"Download: ${cssSelector("#results-download-int").element.text} " +
        s"${cssSelector("#results-download > span.value-type").element.text}")
      println(s"Upload: ${cssSelector("#results-upload-int").element.text} " +
        s"${cssSelector("#results-upload > span.value-type").element.text}")

      println(s"Location: ${cssSelector("#summary_server_name").element.text}")
      println(s"IP Address: ${cssSelector("#summary_client_IP").element.text}")

      close()
    }
  }

  def upDownLength: Int = {
    val r = "[0-9\\.]+".r
    val up = cssSelector("#results-download-int").element.text
    val dn = cssSelector("#results-upload-int").element.text
    val l = r.findFirstIn(dn).getOrElse("").length.min(r.findFirstIn(up).getOrElse("").length)
//    println(s"up=$up dn=$dn l=$l")
    l
  }
}

class VerizonSpecWithChrome extends VerizonSpec with ChromeBrowser
class VerizonSpecWithSafari extends VerizonSpec with Safari
class VerizonSpecWithInternetExplorer extends VerizonSpec with InternetExplorer
class VerizonSpecWithFirefox extends VerizonSpec with Firefox
