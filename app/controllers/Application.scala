package controllers

import play.api._
import play.api.mvc._

import scala.concurrent._
import play.api.libs.ws._
import play.api.libs.oauth._
import play.api.libs.json._

import play.api.libs.concurrent.Execution.Implicits._


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  def timeline = Action { implicit request =>
      Async {
        retrieveTimeline() map { js => Ok(js) }
      }
  }

  def retrieveTimeline(): Future[JsValue] = {
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
}