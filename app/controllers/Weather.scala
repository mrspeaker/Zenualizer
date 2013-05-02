package controllers

import scala.concurrent._

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.ws._

import play.api.libs.concurrent.Execution.Implicits._

import play.api.cache._
import play.api.Play.current

object Weather extends Controller {

    def weather = Cached("weather", 55) {
      Action {
        Async {
          services.Weather.now().map(Ok(_))
        }
      }
    }

    def proxyWeatherMap(req: String) = Action {
        val url = "http://undefined.tile.openweathermap.org/map/"+req;
        Async {
          WS.url(url).get().map { response =>
            Ok(response.ahcResponse.getResponseBodyAsBytes())
              .withHeaders(
                CONTENT_TYPE -> "image/png"
              );
          }
        }
    }
}
