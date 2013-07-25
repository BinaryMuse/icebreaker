import com.learnist.icebreaker._
import com.learnist.icebreaker.processors._
import scala.concurrent.ExecutionContext
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import scala.concurrent.{Future,Await}
import com.ning.http.client

object YourApp extends App {
  val urls = if (args.length > 0) args else Array("http://google.com/")

  // Icebreaker requires an ExecutionContext;
  // create one or import the default
  import ExecutionContext.Implicits.global

  // Create a stack of Processors to chain
  // val cache = new ConcurrentHashMap[String, Future[client.Response]]().asScala
  // val stack = List(
  //   new DefaultHtmlProcessor(cache)
  // )
  // val icebreaker = new Icebreaker(stack)

  // val futureResponse = icebreaker.scrape(url)

  // Scrape a URL; returns Future[Response]
  val responses = urls.toList.map { url =>
    val cache = new ConcurrentHashMap[String, Future[client.Response]]().asScala
    val stack = List(
      new DefaultHtmlProcessor(cache)
    )
    val icebreaker = new Icebreaker(stack)
    icebreaker.scrape(url)
  }

  val responsesFuture = Future.sequence(responses)

  responsesFuture onSuccess {
    case list =>
      for {
        response <- list
      } response match {
        case Response(url, title, image_urls, content_type, content_metadata) =>
          println(s"             URL: $url")
          println(s"           Title: $title")
          println(s"          Images: $image_urls")
          println(s"    Content Type: $content_type")
          println(s"Content Metadata: $content_metadata\n")
      }
  }

  Await.ready(responsesFuture, scala.concurrent.duration.Duration("10 seconds"))
}
