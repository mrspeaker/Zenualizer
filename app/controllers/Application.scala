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
import play.api.Play



object Application extends Controller {

  val startTime = new java.util.Date().getTime

  def version = Action {
    Ok(JsNumber(startTime))
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  implicit val timelineRead = 
    __.read[Seq[(Long, Boolean, Long, Long, String, String, String)]](
      Reads.seq(
        (
          (__ \ "id").read[Long] and
          (__ \ "retweeted").read[Boolean] and
          (__ \ "retweet_count").read[Long] and
          (__ \ "favorite_count").read[Long] and
          (__ \ "text").read[String] and
          (__ \ "user" \ "name").read[String] and
          (__ \ "user" \ "screen_name").read[String]
        ).tupled
      )
    )

  implicit val timeLineWrite =
    Writes.seq(
       (
        (__ \ "id").write[Long] and
        (__ \ "retweeted").write[Boolean] and
        (__ \ "retweet_count").write[Long] and
        (__ \ "favorite_count").write[Long] and
        (__ \ "text").write[String] and
        (__ \ "username").write[String] and
        (__ \ "userscreen_name").write[String]
      ).tupled
    )
    
  def timeline = Action { implicit request =>
      Async {
        retrieveTimeline map { js => 
          Json.fromJson[Seq[(Long, Boolean, Long, Long, String, String, String)]](js) map { l =>
           val sortedL = l.sortWith((e1,e2) => (e1._3 + e1._4) > (e2._3 + e2._4))
           val filteredL = sortedL.filter(e => !e._2)
           Ok(Json.toJson(filteredL))
          } recoverTotal{ e => BadRequest(JsError.toFlatJson(e)) }
          
        }
      }
  }

  def timelineAll = Action { implicit request =>
    Async {
      retrieveTimeline map { js => 
        Ok(js)
      } 
    }
  }

  def retrieveTimeline: Future[JsValue] = {
    WS.url("https://api.twitter.com/1/statuses/home_timeline.json?count=100")
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

  def githubFollowings = Action {
    Async {
      Github.followings map { js => Ok(js) }
    }
  }

  def githubEventStream = Action {
    Async {
      Github.eventStream map { logins => Ok(Json.toJson(logins)) }
    }
  }

  def hackerNewsStream = Action {
    Async {
      HackerNews.stream map { Ok(_) }
    }
  }

  def zendailyStream = Action {
    Async {
      ZenDaily.stream map { Ok(_) }
    }
  }
}
