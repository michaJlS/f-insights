package controllers.actors.messages

import java.util.UUID

import akka.actor.ActorRef
import domain.entities.{PhotoFavourite, UserToken}

case class PreloadPhotosFavs(token: UserToken, dashboardId: UUID, owner: String, photos: Seq[String])

case class PreloadPhotoFavs(replyTo: ActorRef, token: UserToken, dashboardId: UUID, owner: String, photo: String)

case class LoadedPhotoFavs(dashboardId: UUID, photoFavs: Seq[PhotoFavourite])
