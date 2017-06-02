package drivers

import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.selenium.{Driver, WebBrowser}

/**
  * Created by lfinke200 on 6/2/17.
  */
trait WebDrivers {
  def getCurrentDirectory: String = new java.io.File(".").getCanonicalPath

  lazy val os = System.getProperty("os.name")

  //Get OS Version.
  def getOsVersion: String = {
    val os = System.getProperty("os.name")
    var osVersion = System.getProperty("os.version")
    val OsVer = s"$os $osVersion"
    //    setOsType(osVersion)
    OsVer
  }

}

trait ChromeFullScreen extends WebBrowser with Driver with WebDrivers /*with ScreenshotCapturer */ {

  /**
    * <code>WebBrowser</code> subtrait that defines an implicit <code>WebDriver</code> for Chrome (an <code>org.openqa.selenium.chrome.ChromeDriver</code>).
    */
  val options = new ChromeOptions()
  options.addArguments("start-maximized")
//  println(s"Loading $getCurrentDirectory/src/main/resources/drivers/AdBlock/3.10.0_0")
  options.addArguments(s"load-extension=$getCurrentDirectory/src/main/resources/drivers/AdBlock/3.10.0_0")
  val capabilities = DesiredCapabilities.chrome
  capabilities.setCapability(ChromeOptions.CAPABILITY, options)
  System.setProperty("webdriver.chrome.driver", s"$getCurrentDirectory/src/main/resources/drivers/chromedriver")

  implicit val webDriver = new ChromeDriver(capabilities)

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
