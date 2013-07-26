import scala.language.implicitConversions

object IntegerImprovements {
  implicit def IntToLong(x: Int) = x.toLong

  implicit class TimeMethods[T <% Long](val x: T) {
    def seconds: Long = x * 1000
    def second = seconds

    def minutes: Long = seconds * 60
    def minute = minutes

    def hours: Long   = minutes * 60
    def hour = hours

    def days: Long    = hours * 24
    def day = days
  }
}

import IntegerImprovements._

val x = 10

println(s"$x seconds: ${x.seconds} ms")
println(s"$x minutes: ${x.minutes} ms")
println(s"  $x hours: ${x.hours} ms")
println(s"   $x days: ${x.days} ms")
