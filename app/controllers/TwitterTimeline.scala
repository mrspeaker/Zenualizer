package controllers

import scala.concurrent._
import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import views._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits._

object TwitterTimeline extends Controller {

  implicit val timelineRead = 
    __.read[Seq[(Long, Boolean, Long, Long, String, String, String, String)]](
      Reads.seq(
        // TODO: ignore if "retweeted_status" field is present
        (
          (__ \ "id").read[Long] and
          (__ \ "retweeted").read[Boolean] and // TODO: remove this useless field
          (__ \ "retweet_count").read[Long] and
          (__ \ "favorite_count").read[Long] and
          (__ \ "text").read[String] and
          (__ \ "user" \ "name").read[String] and
          (__ \ "user" \ "screen_name").read[String] and
          (__ \ "user" \ "profile_image_url").read[String]
        ).tupled
      )
    )

  implicit val timeLineWrite =
    Writes.seq(
       (
        (__ \ "id").write[Long] and
        (__ \ "retweeted").write[Boolean] and // TODO: remove
        (__ \ "retweet_count").write[Long] and
        (__ \ "favorite_count").write[Long] and
        (__ \ "text").write[String] and
        (__ \ "username").write[String] and
        (__ \ "userscreen_name").write[String] and
        (__ \ "userprofile_image_url").write[String]
      ).tupled
    )
    
  def timeline = Action { implicit request =>
      Async {
        services.Twitter.timeline map { js => 
          Json.fromJson[Seq[(Long, Boolean, Long, Long, String, String, String, String)]](js) map { l =>
           val sortedL = l.sortWith((e1,e2) => (e1._3 + e1._4) > (e2._3 + e2._4))
           val filteredL = sortedL.filter(e => !e._2) // TODO: this doesn't work, we have to check the "retweeted_status" non-presence
           Ok(Json.toJson(filteredL))
          } recoverTotal{ e => BadRequest(JsError.toFlatJson(e)) }
          
        }
      }
  }

  def timelineAll = Action { implicit r =>
    Async {
      services.Twitter.timeline map { js =>
        Ok(Json.toJson(js))
      }
    }
  }

}
