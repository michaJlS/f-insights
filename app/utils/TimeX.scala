package utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.util.Try

object TimeX {

  def unixToJoda(t: String): Option[DateTime] = Try{t.toLong}.toOption.map(t => new DateTime(t * 1000L))

  def unixToYM(t: String, default: String = "1970-01"): String = unixToJoda(t).map(dt => DateTimeFormat.forPattern("yyyy-MM").print(dt)).getOrElse(default)

}
