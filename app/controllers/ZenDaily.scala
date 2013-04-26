package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.concurrent.Promise
import play.api.libs.ws._
import play.api.libs.oauth._
import views._
import play.api.libs.json._

import play.api.libs.concurrent.Execution.Implicits._

object ZenDaily {
  val BASE_URL = "http://10.0.24.139:9000"

  val KEY = "4dc7e739-3b3b-4c68-a094-68320f675853"

  val EVENTS = "api/events"
  val SUBJECTS = "api/subjects"

  def stream = {
    (for{
      events <- WS.url(s"$BASE_URL/$EVENTS").withQueryString("key" -> KEY).get()
      subjects <- WS.url(s"$BASE_URL/$EVENTS").withQueryString("key" -> KEY).get() 
    } yield (events, subjects)).map{ case (events, subjects) =>
      val all = events.json.as[JsArray].value.map{ event => event.as[JsObject] ++ Json.obj("type" -> "event") } ++
      subjects.json.as[JsArray].value.map{ sub => sub.as[JsObject] ++ Json.obj("type" -> "subject") }

      Json.toJson(all.sortWith( (a, b) => (a \ "created").as[Long] < (b \ "created").as[Long] ))
    }
  }

}
