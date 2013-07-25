import com.learnist.icebreaker._
import com.learnist.icebreaker.processors._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import scala.concurrent.{Future,Await}
import com.ning.http.client

object IcebreakerTest extends App {
  val urls = if (args.length > 0) args else Array("http://google.com/")

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

  Await.ready(responsesFuture, 10.seconds)
}
