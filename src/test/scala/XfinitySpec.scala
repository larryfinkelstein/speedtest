import drivers.ChromeFullScreen
import org.openqa.selenium.By
import org.scalatest.selenium._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{FunSpecLike, _}
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated

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

      eventually { cssSelector("#action-start-test").element.size.width should be > 0}
      eventually { cssSelector("#action-start-test").element.text should be ("Start Test") }

      println("Clicking GO! button")
      clickOn(cssSelector("#action-start-test"))

//      eventually { cssSelector("div.text.text--title").element.text should be ("Speed Results") }
//
//      println(s"Download: ${cssSelector("#finalResultsIPv4-download-value").element.text}")
//      println(s"Upload: ${cssSelector("#finalResultsIPv4-upload-value").element.text}")

      try {
//        eventually { cssSelector("#action-restart-test").element.text.toUpperCase should be ("Test Again".toUpperCase)}
//        eventually { cssSelector("#test-results > div > h1").element.text should be ("Speed Results")}
//        eventually { visibilityOfElementLocated(By.xpath("//button[text()='Test Again']"))}
//        eventually { cssSelector("#action-advanced-test-settings").element.text should be ("Advanced Test Settings") }
        eventually {
          uploadCompleted should be > 0
//            cssSelector("#action-advanced-test-settings").element.text should be ("Advanced Test Settings")
        }

        val protocol = cssSelector("#finalResults-protocol-version").element.text
        println(s"Protocol: ${protocol}")
        protocol match {
          case "IPv4" =>
            println(s"Ping: ${cssSelector("#finalResultsIPv4-latency-value").element.text} " +
              s"${cssSelector("#finalResultsIPv4-latency-unit").element.text}")
            println(s"Download: ${cssSelector("#finalResultsIPv4-download-value").element.text} " +
              s"${cssSelector("#final-Results-download-unit").element.text}")
            println(s"Upload: ${cssSelector("#finalResultsIPv4-upload-value").element.text} " +
              s"${cssSelector("#finalResults-upload-unit").element.text}")
          case "IPv6" =>
            println(s"Ping: ${cssSelector(".finalResultsIPv6-latency-descr #finalResultsIPv6-latency-value").element.text} " +
              s"${cssSelector(".finalResultsIPv6-latency-descr #finalResultsIPv6-latency-unit").element.text}")
            println(s"Download: ${cssSelector("#finalResultsIPv6-download-value").element.text} " +
              s"${cssSelector("#final-Results-download-unit").element.text}")
            println(s"Upload: ${cssSelector("#finalResultsIPv6-upload-value").element.text} " +
              s"${cssSelector("#finalResults-upload-unit").element.text}")
          case _ => println(s"Unknown protocol $protocol")
        }

        println(s"Location: ${cssSelector("#finalResults-host-location").element.text}")
//        println(s"IP Address: ${cssSelector(".ip-address").element.text}")
      } catch {
        case e: Exception =>
          println(e.getMessage)
      }
      finally
      {
        // your scala code here, such as to close a database connection
        close()
      }
    }
  }

  def uploadCompleted: Int = {
    val x = cssSelector("#finalResultsIPv4-upload-value").element.text
    val y = cssSelector("#finalResultsIPv6-upload-value").element.text
//    println(s"$x $y ${x.length + y.length}")
    x.length + y.length
  }

}

class XfinitySpecWithChrome extends XfinitySpec with ChromeFullScreen
class XfinitySpecWithSafari extends XfinitySpec with Safari
class XfinitySpecWithInternetExplorer extends XfinitySpec with InternetExplorer
class XfinitySpecWithFirefox extends XfinitySpec with Firefox
