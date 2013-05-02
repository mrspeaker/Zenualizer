package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.concurrent.Promise
import play.api.libs.ws._
import play.api.libs.oauth._
import views._
import play.api.libs.json._

import play.api.libs.concurrent.Execution.Implicits._

object ZenDaily extends Controller {

  def stream = Action {
    Async {
      services.ZenDaily.topics map { Ok(_) }
    }
  }

}
