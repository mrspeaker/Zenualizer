package services

import scala.concurrent._

import play.api._
import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.json._

import play.api.libs.concurrent.Execution.Implicits._

object Weather {

  val lat = current.configuration.getString("weather.location.lat").get
  val lng = current.configuration.getString("weather.location.lng").get
  val units = current.configuration.getString("weather.units").getOrElse("metric")

  def now(): Future[JsObject] = 
    WS.url("http://api.openweathermap.org/data/2.5/weather")
      .withQueryString(
        ("lat", lat),
        ("lon", lng),
        ("units", units)
      )
      .get()
      .map(_.json)
      .map { json =>
        Json.obj(
          ("temp", (json \ "main" \ "temp").as[JsNumber]),
          ("description", (json \\ "description")(0).as[JsString])
        )
      }
}
