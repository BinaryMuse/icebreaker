package com.learnist.icebreaker

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class Icebreaker(stack: Seq[Processor])(implicit val context: ExecutionContext) {
  def scrape(address: String): Future[Response] = {
    val request = Request(address)
    val response = Future(Response("", Nil))
    next(request, response, stack.toList)
  }

  @tailrec
  private def next(request: Request, response: Future[Response], remaining: List[Processor]): Future[Response] = {
    remaining match {
      case processor :: rest =>
        val resp = processor.process(request, response)
        next(request, resp, rest)
      case Nil => response
    }
  }
}
