package frank

object Foo

object Test {
  implicit def richFoo(foo: Foo.type) = new {
    def hi = println("hi")
  }
  
  def run {
    richFoo(Foo).hi
  }
}