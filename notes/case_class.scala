sealed abstract class Base
case class First(name: String) extends Base
case class Second(name: String) extends Base
case class Third(name: String) extends Base

val value: Base = Second("hello")

value match {
  case First(name) => println("It was first! "   + name)
  case Second(name) => println("It was second! " + name)
  // case Third(name) => println("It was third! "   + name)
}
