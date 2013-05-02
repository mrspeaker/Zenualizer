package services

import scala.concurrent._

import play.api._
import play.api.libs.iteratee._
import play.api.libs.ws._
import play.api.libs.oauth._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.libs.concurrent.Execution.Implicits._

import org.joda.time._


object Github {

  val ACCESS_TOKEN = Play.current.configuration.getString("github.access_token").get

  val BASE_URL = "https://api.github.com"
  val USER = "zenualizer"

  val logger = Logger("Github")

  def logGithubLimit(result: Response) {
    for {
      remaining <- result.header("x-ratelimit-remaining")
    } {
        logger.debug("remaining: "+remaining)
    }
  }
  
  def followings: Future[JsValue] = {
    WS.url(s"$BASE_URL/users/$USER/following")
      .withQueryString("access_token" -> ACCESS_TOKEN)
      .get()
      .map { resp =>
        logGithubLimit(resp)
        resp.json
      }
  }

  def events (login: String): Future[JsValue] = {
    WS.url(s"$BASE_URL/users/$login/events")
      .withQueryString("access_token" -> ACCESS_TOKEN).get()
      .map { resp =>
        logGithubLimit(resp)
        resp.json
      }
  }
}
