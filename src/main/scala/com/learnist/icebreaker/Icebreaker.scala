package com.learnist.icebreaker

import scala.annotation.tailrec
import scala.concurrent._

class Icebreaker(stack: Seq[Processor])(implicit context: ExecutionContext = ExecutionContext.Implicits.global) {
  def scrape(address: String): Future[Response] = {
    val request = Request(address)
    val response = future { Response(address, None, Nil, None, Map()) }
    stack.foldLeft(response) { (resp, processor) => processor.process(request, resp) }
  }
}
