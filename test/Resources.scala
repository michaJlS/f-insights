package test

import java.net.URL

import play.api.libs.json.Json

import scala.io.Source

trait Resources
{

  def resource(path:String):String = Source.fromURL(getClass.getResource(path)).mkString

  def jsonResource(path:String) = Json.parse(resource(path))

}
