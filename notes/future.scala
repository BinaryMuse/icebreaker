import scala.util.{Success,Failure}
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global // default thread pool

var oneDay = future {
  Thread.sleep(3000) // twiddling thumbs
  10
}

oneDay = oneDay map { _ + 5 }  // 15
oneDay = oneDay map { _ * 10 } // 150
// oneDay = oneDay map { _ / 0 }

oneDay onComplete {
  case Success(x)  => println(s"The number is $x")
  case Failure(ex) => println(s"Oh no! $ex")
}

println("Waiting...")
Await.ready(oneDay, 10.seconds) // block this thread until we have a result
