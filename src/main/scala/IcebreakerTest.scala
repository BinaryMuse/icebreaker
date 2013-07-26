import com.learnist.icebreaker._
import com.learnist.icebreaker.processors._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext//.Implicits.global
import java.util.concurrent.Executors
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import scala.concurrent._
import com.ning.http.client

import scala.io.Source

object IcebreakerTest extends App {
  // val urls = if (args.length > 0) args else Array("http://google.com/")

  val urls = Source.fromFile("/Users/grockit/workspace/icebreaker/learnist_web_urls").getLines.take(1000)

  import ExecutionContext.Implicits.global

  // This cache is shared for EVERY scraped URL, not ideal API.
  val cache = new ConcurrentHashMap[String, Future[client.Response]]().asScala
  val stack = List(
    new DefaultHtmlProcessor(cache)
  )
  val icebreaker = new Icebreaker(stack)

  val responses = urls.toList.map { url =>
    val resp = icebreaker.scrape(url)

    resp onSuccess {
      case Response(url, title, image_urls, content_type, content_metadata) =>
        println(s"""|             URL: $url
                    |           Title: $title
                    |          Images: $image_urls
                    |    Content Type: $content_type
                    |Content Metadata: $content_metadata\n""".stripMargin)
    }

    resp onFailure {
      case x => println(s"Scraper failed on a URL: $x")
    }

    resp
  }

  val responsesFuture = Future.sequence(responses)

  Await.ready(responsesFuture, 30.seconds)
}
