package controllers

import scala.concurrent._

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.concurrent.Promise
import play.api.libs.ws._
import play.api.libs.oauth._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.libs.concurrent.Execution.Implicits._

import org.joda.time._

object Github {

  val ACCESS_TOKEN = "64a6989c3f6cd0a3aec0ec3f9b20d15b38076819"

  val BASE_URL = "https://api.github.com"
  val USER = "zenualizer"
  
  def followings: Future[JsValue] = {
    WS.url(s"$BASE_URL/users/$USER/following").withQueryString("access_token" -> ACCESS_TOKEN).get() map { resp =>
      resp.json
    }
  }

  val readLogins = __.read[JsArray] flatMap { arr => Reads.seq((__ \ 'login).read[String]) }

  def eventStream = {
    followings.flatMap { js =>
      Json.fromJson(js)(readLogins) map { logins =>
        Future.successful(logins)
      } recoverTotal(e => Future.failed(new RuntimeException(JsError.toFlatForm(e).toString)))
    } flatMap { logins =>
      Future.sequence(logins map { login =>
        events(login)
      }) map (_ map (_.as[Seq[JsValue]]) flatten) // blurp to flatten seq(seq)
    } map { seq =>
      seq.sortWith{ (a, b) =>
        val ad = (a \ "created_at").as[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZ"))
        val bd = (b \ "created_at").as[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZ"))

        bd.isBefore(ad)
      }
    }
  }

  def events(login: String): Future[JsValue] = {
    WS.url(s"$BASE_URL/users/$login/events")
      .withQueryString("access_token" -> ACCESS_TOKEN).get()
      .map { resp =>
        resp.json
      }
  }
}