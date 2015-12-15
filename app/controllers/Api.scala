package controllers

import javax.inject.Inject


import play.api.Play._
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._

import scala.concurrent.Future


class Api extends Controller
{

  def statsFavsTags = Action.async( implicit request => {
    // /stats/favs/tags/ ,  number of most popular ,
    // return tag , count
    Future {InternalServerError("Not yet implemented") }
  } )

  def statsFavsAuthors = Action.async( implicit request => {
    // /stats/favs/authors/ ,  number of most popular , exclude followed
    // return number of
    Future {InternalServerError("Not yet implemented") }
  } )

  def statsUserTags = Action.async( implicit request => {
    Future {InternalServerError("Not yet implemented") }
  } )

}
