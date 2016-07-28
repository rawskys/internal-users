package com.rawskys.controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import scala.concurrent.ExecutionContext.Implicits.global

class InternalUsers @Inject()(val reactiveMongoApi: ReactiveMongoApi) extends Controller with MongoController with ReactiveMongoComponents {

  val status = Action.async {
    val db = reactiveMongoApi.db
    val collection = db.collection[JSONCollection]("user")
    collection.count().map { users =>
      Ok(Json.obj("users" -> users))
    }.recover {
      case e => BadRequest(Json.obj("error" -> e.getLocalizedMessage))
    }
  }
}
