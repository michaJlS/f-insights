package controllers.actors

import akka.util.Timeout
import java.util.concurrent.TimeUnit

import play.api.Logger

trait BaseActor {

  val logger = Logger("fa-actors")

  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  implicit val ec = play.api.libs.concurrent.Execution.Implicits.defaultContext

}
