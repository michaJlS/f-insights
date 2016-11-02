import scala.concurrent.Future

package object kittens {

  object Conversions {

    implicit def futureToFutureOption[T](x:Future[Option[T]]) = new FutureOption(x)

  }

}
