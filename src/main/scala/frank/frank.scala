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

trait RestBase {
  implicit val charset = UTF8
  
  var layoutVar: Option[(NodeSeq => NodeSeq)] = none
  
  def layout(f: NodeSeq => NodeSeq) {
    layoutVar = some(f)
  }
  
  val actions: ArrayBuffer[Request => Option[belt.Response]] = new ArrayBuffer
  
  def rewrite(pf: PartialFunction[Request, Action[String]]) = pf
  
  def rest[T](resource: String, s: Show.type) = new {
    def view(v: T => NodeSeq)(action: String => (Status, T)) = {
      actions += (r => r.action match { 
        case Some((r, Show(id))) if r == resource => some {
          val actionResult: (Status, T) = action(id)
          val viewResult: NodeSeq = v(actionResult._2)
          val layedOutValue: NodeSeq = layoutVar map (f => f(viewResult)) getOrElse viewResult
          Response(actionResult._1)(r => r << layedOutValue)
        }
        case _ => none
      })
    }
  }
  
  def rest(resource: String, i: Index.type)(f: => Response) = {
    actions += (r => r.action match { case Some((r, Index)) if r == resource => some(f); case _ => none })
  }
  
  def apply(r: Request) = {
    actions.iterator.map(_.apply(r)).find(_.isDefined).join getOrElse {
      Response(NotFound)
    }
  }
}

