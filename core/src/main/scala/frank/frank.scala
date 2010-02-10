package frank

import scala.xml._
import scalaz.{Show => _, Index => _, _}
import Scalaz._
import rest._
import belt._
import scalaz.http.response.{Response => _, _}
import scalaz.http.Slinky._

import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.ArrayBuffer

sealed trait ReturnValue[+T]
case class Render[T](s: Status, value: T) extends ReturnValue[T]
case class Redirect(path: String) extends ReturnValue[Nothing]
case class Respond(response: Response) extends ReturnValue[Nothing]

trait RestActions {
  self: RestBase =>
  
  implicit val charset: CharSet

  type View[T] = T => NodeSeq
  def handleReturn[T](rt: ReturnValue[T])(f: View[T]): Response = {
    rt match {
      case Respond(r) => r
      case Redirect(path) => belt.redirect(path)
      case Render(status, value) => {
        Response(status)(r => r << f(value))
      }
    }
  }
  
  def rest(resource: String, s: Show.type) = new {
    def view[T](v: => View[T], action: String => ReturnValue[T]) = {
      actions += (r => r.action match { 
        case Some((r, Show(id))) if r == resource => some {
          val rv = action(id)
          handleReturn(rv)(v)
        }
        case _ => none
      })
    }
  }
  
  // def rest(resource: String, i: Index.type) = new {
  //   def view[T](v: T => NodeSeq)(action: => (Status, T)) = {
  //     actions += (r => r.action match { 
  //       case Some((r, Index)) if r == resource => some {
  //         val actionResult: (Status, T) = action
  //         val viewResult: NodeSeq = v(actionResult._2)
  //         val layedOutValue: NodeSeq = layoutVar map (f => f(viewResult)) getOrElse viewResult
  //         Response(actionResult._1)(r => r << layedOutValue)
  //       }
  //       case _ => none
  //     })
  //   }
  // }
}

trait RestBase extends RestActions {
  implicit val charset = UTF8
  
  var layoutVar: Option[(NodeSeq => NodeSeq)] = none
  
  def layout(f: NodeSeq => NodeSeq) {
    layoutVar = some(f)
  }
  
  val actions: ArrayBuffer[Request => Option[belt.Response]] = new ArrayBuffer
  
  def rewrite(pf: PartialFunction[Request, Action[String]]) = pf
  
  def apply(r: Request) = {
    actions.iterator.map(_.apply(r)).find(_.isDefined).join getOrElse {
      Response(NotFound)
    }
  }
}

