package frank

import scala.xml._
import belt._
import rest._
import scalaz.http.response.OK

class Beers(request: Request) extends RestBase {  
  rest("breweries", Show).view(someView, { id =>
    Respond(Response(OK))
    Redirect("/")
    Render(OK, id.toInt)
  })
  
  layout { body =>
    <html>
      { body }
    </html>
  }
  
  val someView: Int => NodeSeq = (id: Int) => {
    <p>got id: {id}</p>
  }
}
