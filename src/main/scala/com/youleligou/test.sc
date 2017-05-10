import java.time.LocalDate

import scala.io.Source

Source.fromURL("http://api.ip.data5u.com/dynamic/get.html?order=50734e2ec46b2b80855cb60cc821cf4c&random=true").mkString


import java.text.SimpleDateFormat

val tt = new SimpleDateFormat("yyyy-MM-dd")
tt.format(java.sql.Date.valueOf(LocalDate.now()))