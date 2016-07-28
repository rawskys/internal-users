package com.rawskys.model

import play.api.data._
import play.api.libs.json._

import play.api.data.Forms.{text, longNumber, mapping, nonEmptyText, optional}

case class User(id: Option[String], name: String, pass: String)

object User {

  implicit object UserWrites extends OWrites[User] {

    def writes(user: User): JsObject = Json.obj(
      "_id" -> user.id,
      "user" -> user.name,
      "pass" -> user.pass
    )
  }

  implicit object UserReads extends Reads[User] {

    override def reads(json: JsValue): JsResult[User] = json match {
      case obj: JsObject => try {
        val id = (obj \ "_id").asOpt[String]
        val name = (obj \ "user").as[String]
        val pass = (obj \ "pass").as[String]

        JsSuccess(User(id, name, pass))
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }

      case _ => JsError("expected.jsobject")
    }
  }

  val form = Form(
    mapping(
      "_id" -> optional(text),
      "user" -> nonEmptyText,
      "pass" -> nonEmptyText
    ) {
      (id, name, pass) => User(id, name, pass)
    } { user =>
      Some(user.id, user.name, user.pass)
    }
  )
}
