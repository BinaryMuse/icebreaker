import com.learnist.icebreaker._
import com.learnist.icebreaker.CachingProcessor.Cache
import scala.concurrent.ExecutionContext
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._

import scala.concurrent.Future

// class MyFirstProcessor(cache: concurrent.Map[String,Future[Response]])(implicit context: ExecutionContext) extends Processor {
//   override def process(request: Request, response: Future[Response]) = {
//     response map { _.copy(title = Some("test")) }
//     // response
//   }
// }
//
// class MySecondProcessor(implicit context: ExecutionContext) extends Processor {
//   override def process(request: Request, response: Future[Response]) = {
//     response map { resp =>
//       val newTitle = resp.title map { _.toUpperCase }
//       val newImages = resp.image_urls ++ List("images/test.png")
//       resp.copy(title = newTitle, image_urls = newImages, content_type = Some(HuluVideo))
//     }
//   }
// }

class DefaultHtmlProcessor(cache: Cache)(implicit context: ExecutionContext) extends CachingProcessor {
  override def process(request: Request, response: Future[Response]) = {
    response
  }
}

object YourApp extends App {
  // Icebreaker requires an ExecutionContext;
  // create one or import the default
  import ExecutionContext.Implicits.global

  // Create a stack of Processors to chain
  val cache = new ConcurrentHashMap[String, Future[Response]]().asScala
  val stack = List(
    new DefaultHtmlProcessor(cache)
  )
  val icebreaker = new Icebreaker(stack)

  // Scrape a URL; returns Future[Response]
  val futureResponse = icebreaker.scrape("http://www.google.com/")

  futureResponse onSuccess {
    case Response(title, image_urls, None, content_metadata) =>
      println("OMG NO CONTENT TYPE WHAT DO I DO")
    case Response(title, image_urls, Some(content_type), content_metadata) =>
      println(title)
      println(image_urls)
      println(content_type)
      println(content_metadata)
  }
}
