import com.learnist.icebreaker._
import scala.concurrent.ExecutionContext

import scala.concurrent.Future

class MyFirstProcessor(implicit context: ExecutionContext) extends Processor {
  override def process(request: Request, response: Future[Response]) = {
    response map { _.copy(title = "TITLE!") }
  }
}

class MySecondProcessor(implicit context: ExecutionContext) extends Processor {
  override def process(request: Request, response: Future[Response]) = {
    response map { resp =>
      val newTitle = resp.title map { _.toUpperCase }
      val newImages = resp.image_urls ++ List("images/test.png")
      resp.copy(title = newTitle, image_urls = newImages, content_type = Some(HuluVideo))
    }
  }
}

object YourApp extends App {
  // Icebreaker requires an ExecutionContext;
  // create one or import the default
  import ExecutionContext.Implicits.global

  // Create a stack of Processors to chain
  val stack = List(
    new MyFirstProcessor,
    new MySecondProcessor
  )
  val icebreaker = new Icebreaker(stack)

  // Scrape a URL; returns Future[Response]
  val futureResponse = icebreaker.scrape("http://some/url")

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
