package models

/**
  * Created by lfinke200 on 6/1/17.
  */

case class TestResults(
                      browser: String,
                      site: String,
                      os: String,
                      resultId: String,
                      ping: String,
                      download: String,
                      upload: String,
                      location: String,
                      ipAddress: String,
                      duration: Long,
                      protocol: Option[String],
                      message: Option[String]
                      )
