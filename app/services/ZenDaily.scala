package services

import play.api._
import play.api.libs.iteratee._
import play.api.libs.concurrent.Promise
import play.api.libs.ws._
import play.api.libs.oauth._
import views._
import play.api.libs.json._

import play.api.libs.concurrent.Execution.Implicits._

object ZenDaily {

  val BASE_URL = Play.current.configuration.getString("zendaily.url").get
  val KEY = Play.current.configuration.getString("zendaily.key").get

  val EVENTS = "api/events"
  val SUBJECTS = "api/subjects"

  def topics = {
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
