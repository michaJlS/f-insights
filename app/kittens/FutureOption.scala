package kittens

import scala.concurrent.{ExecutionContext, Future}

class FutureOption[T](x:Future[Option[T]]) {

  def mapOpt[U](f:(T => U))(implicit ec: ExecutionContext): Future[Option[U]] = x.map{_.map(f(_))}

}
