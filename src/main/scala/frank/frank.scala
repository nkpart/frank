package frank

import scalaz.{Show => _, Index => _, _}
import Scalaz._
import rest._
import belt._
import scalaz.http.response.{Response => _, _}

import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.ArrayBuffer

object run extends Belt {
  def symbolFor[T](a: Action[T]) = a match {
    case New => 'new
    case Create => 'create
    case Edit(_) => 'edit
    case Index => 'index
    case Show(_) => 'show
    case Destroy(_) => 'destroy
    case Update(_) => 'update
  }
  
  def service(r: Request): Response = {
    MyApp(r)
  }
}

trait RenderArgs {
  val renderArgs: MMap[String, Any] = MMap()

  implicit def pimpedString(s: String) = new {
    def <+(a: Any) = renderArgs += (s -> a)
  }
}

trait RestBase {
  val actions: ArrayBuffer[Request => Option[belt.Response]] = new ArrayBuffer
  
  def rest(resource: String, s: Show.type)(f: String => Response) = {
    actions += (r => r.action match { case Some((r, Show(id))) if r == resource => some(f(id)); case _ => none })
  }
  
  def apply(r: Request) = {
    actions.iterator.map(_.apply(r)).find(_.isDefined).join getOrElse {
      Response(NotFound)
    }
  }
//  def rest(a: New.type)(r: => Response) = action('new){case New => r}
}

object MyApp extends RenderArgs with RestBase {
  rest("breweries", Show) { id =>
    Response(OK)
  }
  
  // action('show) { case Show(id) =>
  //   "foo" <+ id
  //   belt.Response(OK)
  // }
}
