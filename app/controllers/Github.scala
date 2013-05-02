package controllers

import scala.concurrent._

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.ws._
import play.api.libs.oauth._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.libs.concurrent.Execution.Implicits._

import org.joda.time._

import play.api.cache._
import play.api.Play.current

object Github extends Controller {

  val readLogins = __.read[JsArray] flatMap { arr => Reads.seq((__ \ 'login).read[String]) }

  def followings = services.Github.followings

  def allEvents = {
    services.Github.followings.flatMap { js =>
      Json.fromJson(js)(readLogins) map { logins =>
        Future.successful(logins)
      } recoverTotal(e => Future.failed(new RuntimeException(JsError.toFlatForm(e).toString)))
    } flatMap { logins =>
      Future.sequence(logins map { login =>
        services.Github.events(login)
      }) map (_ map (_.as[Seq[JsValue]]) flatten) // blurp to flatten seq(seq)
    } map { seq =>
      seq.sortWith { (a, b) =>
        val ad = (a \ "created_at").as[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZ"))
        val bd = (b \ "created_at").as[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZ"))
        bd.isBefore(ad)
      }
    }
  }

  def followingsStream = Cached("github_following", 55) {
    Action {
      Async {
        followings map { Ok(_) }
      }
    }
  }

  def eventStream = Cached("github_event", 55) {
    Action {
      Async {
        allEvents map { js => Ok(Json.toJson(js)) }
      }
    }
  }
}
