Icebreaker
==========

![Icebreaker](media/icebreaker.png)

Warning: this project developed during a hackathon to explore the Scala language and its asynchronous APIs. Not responsible for leaky abstractions, poor idioms, or plain ol' bad ideas. I probably wouldn't use this in production.

Requirements
------------

* [sbt](http://www.scala-sbt.org/) - On OS X, you can install sbt with `brew update && brew install sbt`. The first time you run sbt, it will download the version of Scala that it uses internally. Icebreaker itself uses a specific version of Scala, so sbt may download more than one version of Scala if you run sbt for the first time from within Icebreaker's directory.

Installation
------------

`sbt update`

Usage
-----

Scraping a URL:

```scala
import com.learnist.icebreaker._
import scala.concurrent.ExecutionContext

object YourApp extends App {
  // Icebreaker uses a default implicit ExecutionContext if you don't supply one,
  // but we'll need one anyway to evaluate the Future[Response] Icebreaker returns.
  // Most (all?) processors will also need an ExecutionContext.
  import ExecutionContext.Implicits.global

  // Create a stack of Processors to chain
  val stack = List(
    new MyFirstProcessor
    new MySecondProcessor
  )
  val icebreaker = new Icebreaker(stack)

  // Scrape a URL; returns Future[Response]
  val futureResponse = icebreaker.scrape("http://some/url")

  futureResponse onSuccess {
    // ...
  }
}
```

The work your processors do is very free-form, and can be created however you please. For example, if you plan on doing a lot of HTTP requests, you could create a cache object:

```scala
val cache = new ConcurrentHashMap[String,String]()
val stack = List(
  new FirstHttpProcessor(cache),
  new SecondHttpProcessor(cache),
  new NonHttpProcessor
)
val icebreaker = new Icebreaker(stack)
```

Processors must define a `process` method that takes a request and the current (future) response and returns a new (future) response.

```scala
class TitleCapitalizer(implicit context: ExecutionContext) extends Processor {
  override def process(request: Request, response: Future[Response]) = {
    response map { resp =>
      val newTitle = resp.title map { _.toUpperCase }
      resp.copy(title = newTitle)
    }
  }
}
```

License
-------

This software is licensed under the MIT license. See the `LICENSE` file for more information.
