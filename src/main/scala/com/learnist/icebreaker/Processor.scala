package com.learnist.icebreaker

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

case class Request(url: String)
case class Response(title: String, image_urls: Seq[String])

abstract class Processor {
  def process(request: Request, response: Future[Response]): Future[Response]
}
