import com.learnist.icebreaker._
import com.learnist.icebreaker.processors._

import scala.io.Source
import scala.concurrent._
import scala.concurrent.ExecutionContext
import scala.collection.JavaConverters._
import scala.util.{Success,Failure}

import java.util.concurrent.Executors
import java.util.concurrent.ConcurrentHashMap

import com.ning.http.client

object IcebreakerTest extends App {
  val urls = if (args.length > 0) args else Array("http://google.com/")

  // val urls = Source.fromFile("/Users/grockit/workspace/icebreaker/learnist_web_urls").getLines.take(1000)

  import ExecutionContext.Implicits.global
  // implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(50))

  // This cache is shared for EVERY scraped URL, not ideal API.
  val cache = new ConcurrentHashMap[String, Future[client.Response]]().asScala
  val stack = List(
    new DefaultHtmlProcessor(cache)
  )
  val icebreaker = new Icebreaker(stack)

  val responses = urls.toList.map { icebreaker.scrape(_) }

  for (response <- responses) {
    response onSuccess {
        case Response(url, title, image_urls, content_type, content_metadata) =>
          println(s"""|             URL: $url
                      |           Title: $title
                      |          Images: $image_urls
                      |    Content Type: $content_type
                      |Content Metadata: $content_metadata\n""".stripMargin)
    }

    response onFailure {
      case ex => println("Failure: " + ex)
    }
  }

  // val responsesFuture = Future.sequence(responses)

  // Await.ready(responsesFuture, 30.seconds)
  // System.exit(0)
}
