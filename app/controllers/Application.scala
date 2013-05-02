package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Application extends Controller {

  val startTime = new java.util.Date().getTime

  def version = Action {
    Ok(JsNumber(startTime))
  }

  def index = Action {
    Ok(views.html.index())
  }

}
