package controllers.actors

import akka.util.Timeout
import java.util.concurrent.TimeUnit

trait BaseActor {

  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  implicit val ec = play.api.libs.concurrent.Execution.Implicits.defaultContext

}
