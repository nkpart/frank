
package belt {
  class Application extends belt.Belt {
    def service(r: Request): Response = {
      val beers: frank.RestBase = new frank.examples.Beers(r)
      beers.apply(r)
    }
  }
}

package frank {
package examples {

import scala.xml._
import belt._
import rest._

class Beers(request: Request) extends RestBase {
  rest("breweries", Show).view(someView) { id =>
    (OK, id.toInt)
  }
  
  layout { body =>
    <html>
      { body }
    </html>
  }
  
  val someView: Int => NodeSeq = (id: Int) => {
    <p>got id: {id}</p>
  }
}

}}