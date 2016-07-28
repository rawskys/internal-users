package com.rawskys.controllers

import java.util.UUID
import javax.inject.Inject

import com.rawskys.model.User
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class InternalUsers @Inject()(val reactiveMongoApi: ReactiveMongoApi) extends Controller with MongoController with ReactiveMongoComponents {

  val collection = db.collection[JSONCollection]("user")

  val status = Action.async {
    collection.count().map { users =>
      Ok(Json.obj("users" -> users))
    }.recover {
      case e => BadRequest(Json.obj("error" -> e.getLocalizedMessage))
    }
  }

  def register = Action.async { implicit request =>
//    implicit val messages = messagesApi.

    User.form.bindFromRequest.fold(
      errors => Future.successful(Ok(Json.obj("error" -> errors.toString))),
      article => collection.insert(article.copy(
        id = article.id.orElse(Some(UUID.randomUUID().toString))
      )).map(result => Ok(Json.obj("status" -> result.message)))
    )
  }
}
