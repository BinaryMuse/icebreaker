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

  val urls = Source.fromFile("/Users/grockit/workspace/icebreaker/topsites.txt").getLines.toList.take(500).map { "http://" + _ + "/" }

  implicit val ec = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(1000);
    def execute(runnable: Runnable) {
      threadPool.submit(runnable)
    }
    def reportFailure(t: Throwable) {}
  }

  println("Starting...")
  val responses = urls.toList.map { url =>
    val cache = new ConcurrentHashMap[String, Future[client.Response]]().asScala
    val stack = List(
      new DefaultHtmlProcessor(cache)
    )
    val icebreaker = new Icebreaker(stack)
    val resp = icebreaker.scrape(url)

    resp onSuccess {
      case Response(url, title, image_urls, content_type, content_metadata) =>
        println(s"""|             URL: $url
                    |           Title: $title
                    |          Images: $image_urls
                    |    Content Type: $content_type
                    |Content Metadata: $content_metadata\n""".stripMargin)
    }

    resp
  }

  val responsesFuture = Future.sequence(responses)

  // responsesFuture onSuccess {
  //   case list =>
  //     for {
  //       response <- list
  //     } response match {
  //       case Response(url, title, image_urls, content_type, content_metadata) =>
  //         println(s"             URL: $url")
  //         println(s"           Title: $title")
  //         println(s"          Images: $image_urls")
  //         println(s"    Content Type: $content_type")
  //         println(s"Content Metadata: $content_metadata\n")
  //     }
  // }

  Await.ready(responsesFuture, 30.seconds)
}
