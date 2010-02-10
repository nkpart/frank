package belt

import belt._

class Application extends belt.Belt {
  def service(r: Request): Response = {
    val beers: frank.RestBase = new frank.Beers(r)
    beers.apply(r)
  }
}
