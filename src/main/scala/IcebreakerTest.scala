import com.learnist.icebreaker._
import com.learnist.icebreaker.processors._
import scala.concurrent.ExecutionContext
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import scala.concurrent.{Future,Await}
import com.ning.http.client

object YourApp extends App {
  // Icebreaker requires an ExecutionContext;
  // create one or import the default
  import ExecutionContext.Implicits.global

  // Create a stack of Processors to chain
  val cache = new ConcurrentHashMap[String, Future[client.Response]]().asScala
  val stack = List(
    new DefaultHtmlProcessor(cache)
  )
  val icebreaker = new Icebreaker(stack)

  // Scrape a URL; returns Future[Response]
  val futureResponse = icebreaker.scrape("http://www.google.com/")

  futureResponse onSuccess {
    case Response(title, image_urls, content_type, content_metadata) =>
      println(s"           Title: $title")
      println(s"          Images: $image_urls")
      println(s"    Content Type: $content_type")
      println(s"Content Metadata: $content_metadata")
  }

  futureResponse onFailure {
    case e => println(e)
  }

  Await.ready(futureResponse, scala.concurrent.duration.Duration("10 seconds"))
}
