Icebreaker
==========

![Icebreaker](media/icebreaker.png)

Scraper on steroids

Requirements
------------

[sbt](http://www.scala-sbt.org/)

On OS X, you can install sbt with `brew update && brew install sbt`. The first time you run sbt, it will download the version of Scala that it uses internally. Icebreaker itself uses a specific version of Scala, so sbt may download more than one version of Scala if you run sbt for the first time from within Icebreaker's directory.

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
  // Icebreaker requires an ExecutionContext;
  // create one or import the default
  import ExecutionContext.Implicits.global

  // Create a stack of Processors to chain
  val stack = List(
    new MyFirstProcessor
    new MySecondProcessor
  )
  val icebreaker = new Icebreaker(stack)

  // Scrape a URL; returns Future[Response]
  val futureResponse = icebreaker.scrape("http://some/url")
}
```
