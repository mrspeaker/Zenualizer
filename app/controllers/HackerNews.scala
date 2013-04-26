package controllers

import scala.concurrent._

import play.api.libs.ws._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.oauth._

object HackerNews {
  // sucks @hnycombinator
  def stream = {
    WS.url("http://api.twitter.com/1/statuses/user_timeline.json")
      .withQueryString("screen_name" -> "hnycombinator")
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