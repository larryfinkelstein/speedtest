package drivers

import org.joda.time.{Interval, Period}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.selenium.{Driver, WebBrowser}

/**
  * Created by lfinke200 on 6/2/17.
  */
trait WebDrivers {
  def getCurrentDirectory: String = new java.io.File(".").getCanonicalPath

  lazy val os:String = System.getProperty("os.name")

  //Get OS Version.
  def getOsVersion: String = {
    val os = System.getProperty("os.name")
    var osVersion = System.getProperty("os.version")
    val OsVer = s"$os $osVersion"
    //    setOsType(osVersion)
    OsVer
  }

//  def isWindows: Boolean = os.indexOf("win") >= 0
//  def isMac: Boolean = os.indexOf("mac") >= 0
//  def isUnix: Boolean = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0

  //implicit val webDriver = new HtmlUnitDriver()
  def formatDuration(t0: Long, t1: Long): String = {
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

trait ChromeFullScreen extends WebBrowser with Driver with WebDrivers /*with ScreenshotCapturer */ {

  /**
    * <code>WebBrowser</code> subtrait that defines an implicit <code>WebDriver</code> for Chrome (an <code>org.openqa.selenium.chrome.ChromeDriver</code>).
    */
  val options = new ChromeOptions()
  options.addArguments("start-maximized")
  options.addArguments(s"load-extension=$getCurrentDirectory/src/main/resources/drivers/Chrome/AdBlock/3.10.0_0")
  private val capabilities = DesiredCapabilities.chrome
  capabilities.setCapability(ChromeOptions.CAPABILITY, options)
  private val suffix = getOsVersion.takeWhile(_ != ' ') match {
    case "Windows" => "Windows/chromedriver.exe"
    case "Mac" => "Mac/chromedriver"
    case "Linux" => "Linux/chromedriver"
    case _ =>
      println(s"Unable to find driver for $getOsVersion/$os")
      "nodriver"
  }

  System.setProperty("webdriver.chrome.driver", s"$getCurrentDirectory/src/main/resources/drivers/$suffix")

  implicit val webDriver = new ChromeDriver(capabilities)

  def quit: Unit = webDriver.quit()
  def getWebDriver: ChromeDriver = webDriver

  /**
    * Captures a screenshot and saves it as a file in the specified directory.
    */
  def captureScreenshot(directory: String): Unit = {
    capture to directory
  }
}

/**
  * Companion object that facilitates the importing of <code>Chrome</code> members as
  * an alternative to mixing it in. One use case is to import <code>Chrome</code> members so you can use
  * them in the Scala interpreter.
  */
object ChromeFullScreen extends ChromeFullScreen
