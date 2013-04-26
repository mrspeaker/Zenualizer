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
    Twitter.sessionTokenPair match {
      case Some(credentials) => Async {
        retrieveTimeline(credentials) map { js => Ok(js) }
      }
      case _ => Redirect(routes.Twitter.authenticate)
    }
  }

  def retrieveTimeline(credentials: RequestToken): Future[JsValue] = {
    WS.url("https://api.twitter.com/1/statuses/home_timeline.json")
      .sign(OAuthCalculator(Twitter.KEY, credentials))
      .get
      .map{ result => 
        result.json
      }
  }
}