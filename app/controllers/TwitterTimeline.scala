package controllers

import scala.concurrent._
import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import views._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits._

import org.joda.time._

import play.api.cache._
import play.api.Play.current

import java.util.Locale

object TwitterTimeline extends Controller {

  import play.api.data.validation.ValidationError  
  def jodaDateReads(pattern: String, locale: Locale, corrector: String => String = identity): Reads[org.joda.time.DateTime] = new Reads[org.joda.time.DateTime] {
    import org.joda.time.DateTime

    val df = org.joda.time.format.DateTimeFormat.forPattern(pattern).withLocale(locale)
    
    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsNumber(d) => JsSuccess(new DateTime(d.toLong))
      case JsString(s) => parseDate(corrector(s)) match {
        case Some(d) => JsSuccess(d)
        case None => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.jodadate.format", pattern))))
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date"))))
    }

    private def parseDate(input: String): Option[DateTime] =
      scala.util.control.Exception.allCatch[DateTime] opt (DateTime.parse(input, df))

  }

  val tweetRead =
    (
      (__ \ "id_str").read[String] and
      (__ \ "retweet_count").read[Long] and
      (__ \ "favorite_count").read[Long] and
      (__ \ "text").read[String] and
      (__ \ "created_at").read[DateTime](jodaDateReads("EEE MMM dd HH:mm:ss Z yyyy", java.util.Locale.ENGLISH)) and
      (__ \ "user" \ "name").read[String] and
      (__ \ "user" \ "screen_name").read[String] and
      (__ \ "user" \ "profile_image_url").read[String]
    ).tupled


  implicit val timelineRead = 
    __.read[JsArray] map { array =>
      array.value
        .filter { item => (item \ "retweeted_status").isInstanceOf[JsUndefined] }
        .flatMap { item => tweetRead.reads(item).asOpt }
    }

  implicit val timeLineWrite =
    Writes.seq(
       (
        (__ \ "id").write[String] and
        (__ \ "retweet_count").write[Long] and
        (__ \ "favorite_count").write[Long] and
        (__ \ "text").write[String] and
        (__ \ "created_at").write[DateTime] and
        (__ \ "username").write[String] and
        (__ \ "userscreen_name").write[String] and
        (__ \ "userprofile_image_url").write[String]
      ).tupled
    )
    
  def timeline = Cached("timeline", 55) {
    Action { implicit request =>
      Async {
        val _24hoursAgo = DateTime.now().minusHours(24)
        services.Twitter.timeline map { js => 
          Json.fromJson[Seq[(String, Long, Long, String, DateTime, String, String, String)]](js) map { list =>
            val results = 
              list
              .filter { twitt =>
                twitt._5 isAfter _24hoursAgo
              }
              .sortWith { (e1, e2) =>
                (e1._2 + e1._3) > (e2._2 + e2._3)
              }
            Ok(Json.toJson(results))
          } recoverTotal{ e => BadRequest(JsError.toFlatJson(e)) }
          
        }
      }
    }
  }

  /*
  def timelineAll = Action { implicit r =>
    Async {
      services.Twitter.timeline map { js =>
        Ok(Json.toJson(js))
      }
    }
  }
  */
}
