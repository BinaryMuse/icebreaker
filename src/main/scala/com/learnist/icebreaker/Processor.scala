package com.learnist.icebreaker

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.collection.immutable

case class Request(url: String)
case class Response(url: String, title: Option[String], image_urls: Seq[String], content_type: Option[ContentType], content_metadata: Map[String,String])

abstract class Processor {
  def process(request: Request, response: Future[Response]): Future[Response]
}
