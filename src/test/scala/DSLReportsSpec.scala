import org.scalatest.selenium._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{FunSpecLike, _}

/**
  * Created by larryf on 5/27/2017.
  */
trait DSLReportsSpec extends FunSpecLike with Matchers with concurrent.Eventually {
  this: WebBrowser with Driver =>

  describe("www.dslreports.com/speedtest") {
    it("should compute download and upload speeds") {
      implicit val patienceConfig =
        PatienceConfig(timeout = scaled(Span(120, Seconds)), interval = scaled(Span(1, Seconds)))
      // Cancel test when cannot access
      try goTo("http://www.dslreports.com/speedtest") catch { case e: Throwable => cancel(e) }

      eventually { cssSelector("#start_panel > div > p:nth-child(2) > a:nth-child(2)").element.size.width should be > 0}
      eventually { cssSelector("#start_panel > div > p:nth-child(2) > a:nth-child(2)").element.text should be ("Cable") }

      println("Clicking Cable button") // TODO: Add support for Gig/Fiber
      clickOn(cssSelector("#start_panel > div > p:nth-child(2) > a:nth-child(2)"))


      try {
//        println("Waiting for test to complete")
        eventually {
          cssSelector("#latency1 > div").element.text should be ("pinging")
        }
        println("ping test started")
        eventually {
          cssSelector("#latency1 > div").element.text should be ("downloading")
        }
        println("download test started")
        eventually {
          cssSelector("#latency1 > div").element.text should be ("uploading")
        }
        println("upload test started")
        eventually {
          cssSelector("#latency1 > div").element.text should be ("cleaning up")
        }
        println("cleaning up")
        eventually {
          uploadCompleted should be > 0
        }
        println("Test completed")

//        Thread.sleep(1000)
//        val protocol = cssSelector("#finalResults-protocol-version").element.text

//        println("Ping:")
//        println(cssSelector("#latency_server").element.toString())
//        println(s"Ping: ${cssSelector("#latency_server > table > tbody > tr").element.toString}")
//        println(s"Ping: ${cssSelector("#latency_server > table > tbody > tr").element.text.toString}")
        println(s"Download: ${cssSelector("#kbps_down > div.kbps_down").element.text} " +
          s"${cssSelector("#kbps_down > div.kbps_label").element.text}")
//        println("Upload:")
//        val element = cssSelector("#kbps_up > div.kbps_up").element
//        println(s"Upload: ${element.text} ")
        println(s"Upload: ${cssSelector("#kbps_up > div.kbps_up").element.text} " +
          s"${cssSelector("#kbps_up > div.kbps_label").element.text}")

//        println(s"Location: ${cssSelector("#latency_server > table > tbody > tr:nth-child(1) > td:nth-child(1)").element.text}")
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
    try {
      val d = cssSelector("#kbps_down > div.kbps_down").element.text
      val u = cssSelector("#kbps_up > div.kbps_up").element.text
//      println(s"$d $u ${d.length + u.length}")
      d.length.min(u.length)
    } catch {
      case e: Throwable =>
//        println(e.getMessage)
        0
    }
  }

}

//class DSLReportsSpecWithChrome extends DSLReportsSpec with ChromeFullScreen
class DSLReportsSpecWithChrome extends DSLReportsSpec with Chrome
class DSLReportsSpecWithSafari extends DSLReportsSpec with Safari
class DSLReportsSpecWithInternetExplorer extends DSLReportsSpec with InternetExplorer
class DSLReportsSpecWithFirefox extends DSLReportsSpec with Firefox
