package models

import java.util.Date

/**
  * Created by lfinke200 on 6/1/17.
  */

case class TestResults(
                      ping: String,
                      download: String,
                      upload: String,
                      location: String,
                      ipAddress: String,
                      protocol: Option[String]
                      )
