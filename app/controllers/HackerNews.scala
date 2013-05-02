package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

object HackerNews extends Controller {
  def stream = Action {
    Async {
      services.HackerNews.news map { Ok(_) }
    }
  }

}
