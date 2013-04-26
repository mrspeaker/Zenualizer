package controllers

import play.api._
import play.api.mvc._

import scala.concurrent._
import play.api.libs.ws._
import play.api.libs.oauth._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.libs.concurrent.Execution.Implicits._

import services._

import concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  val startTime = new java.util.Date().getTime

  def version = Action {
    Ok(JsNumber(startTime))
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val timelineRead = 
    __.read[Seq[(Long, Boolean, Long, Long)]](
      Reads.seq(
        (
          (__ \ "id").read[Long] and
          (__ \ "retweeted").read[Boolean] and
          (__ \ "retweet_count").read[Long] and
          (__ \ "favorite_count").read[Long]
        ).tupled
      )
    )

  def timeline = Action { implicit request =>
      Async {
        retrieveTimeline map { js => 
          timelineRead.reads(js) map { l =>
            Ok(l.toString)
          } recoverTotal{ e => BadRequest(JsError.toFlatJson(e)) }
          
        }
      }
  }

  def retrieveTimeline: Future[JsValue] = {
    WS.url("https://api.twitter.com/1/statuses/home_timeline.json")
      .sign(OAuthCalculator(
        Twitter.KEY, 
        Twitter.tokenPair
      ))
      .get
      .map{ result => 
        result.json
      }
  }

  def weather = Action {
    Async {
      Weather.now().map(Ok(_))
    }
  }

}
