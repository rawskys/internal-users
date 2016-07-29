package com.rawskys.model

import org.mindrot.jbcrypt.BCrypt
import play.api.data.Forms.{mapping, nonEmptyText, optional, text}
import play.api.data._
import play.api.libs.json._
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONObjectID}

case class NewUser(id: Option[String], name: String, pass: Array[Char])

object NewUser {

	implicit object UserReads extends Reads[NewUser] {

		override def reads(json: JsValue): JsResult[NewUser] = json match {
			case obj: JsObject => try {
				val id = (obj \ "_id").asOpt[String]
				val name = (obj \ "user").as[String]
				val pass = (obj \ "pass").as[String]

				JsSuccess(NewUser(id, name, pass.toCharArray))
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
			"pass" -> nonEmptyText(12)
		) {
			(id, name, pass) => NewUser(id, name, pass.toCharArray)
		} { user =>
			Some(user.id, user.name, user.pass.toString)
		}
	)

	implicit object NewUserWriter extends BSONDocumentWriter[NewUser] {

		override def write(newUser: NewUser): BSONDocument = BSONDocument(
			"_id" -> BSONObjectID.generate(),
			"user" -> newUser.name,
			"pass" -> BCrypt.hashpw(newUser.toString, BCrypt.gensalt())
		)
	}

}
