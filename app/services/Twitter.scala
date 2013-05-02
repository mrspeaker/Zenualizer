package services

import scala.concurrent._
import play.api._
import play.api.libs.json._
import play.api.libs.oauth._
import play.api.libs.ws._
import java.io.File
import play.api.libs.concurrent.Execution.Implicits._

object Twitter {

  val conf = Play.current.configuration

  val consumerKey = ConsumerKey(conf.getString("twitter.consumer_key").get, conf.getString("twitter.consumer_secret").get)
  val tokenPair = RequestToken(conf.getString("twitter.access_token").get, conf.getString("twitter.access_token_secret").get)
  val authCalculator = OAuthCalculator(consumerKey, tokenPair)

  val logger = Logger("Twitter")

  def logTwitterLimit(result: Response, context: String) {
        for {
          remaining <- result.header("x-ratelimit-remaining")
          resetTime <- result.header("x-ratelimit-reset")
        } {
          logger.debug(context+" - remaining: "+remaining+
            " - reset: "+new java.util.Date(1000L*resetTime.toLong))
        }
  }

  def timeline: Future[JsValue] = {
    WS.url("https://api.twitter.com/1/statuses/home_timeline.json?count=100")
      .sign(authCalculator)
      .get
      .map { result => 
        logTwitterLimit(result, "timeline")
        result.json
      }
  }

  def userTimeline (user: String): Future[JsValue] = {
    WS.url("http://api.twitter.com/1/statuses/user_timeline.json")
      .withQueryString("screen_name" -> user)
      .sign(Twitter.authCalculator)
      .get
      .map { result =>
        logTwitterLimit(result, "userTimeline("+user+")")
        result.json
      }
  }

}
