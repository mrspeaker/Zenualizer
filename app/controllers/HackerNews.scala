package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import play.api.cache._
import play.api.Play.current

object HackerNews extends Controller {
  def stream = Cached("hackernews_twitter", 55) {
    Action {
      Async {
        services.HackerNews.news map { Ok(_) }
      }
    }
  }

}
