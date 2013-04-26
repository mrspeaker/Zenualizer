package widgets

import play.api.Play.current
import scala.collection.JavaConversions._

object Widgets {
  val all: List[String] = current.configuration.getStringList("application.widgets").map(_.toList).getOrElse(Nil)
}
