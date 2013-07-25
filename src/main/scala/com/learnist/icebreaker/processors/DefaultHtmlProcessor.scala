package com.learnist.icebreaker.processors

import dispatch._
import com.learnist.icebreaker._
import CachingProcessor.Cache
import scala.concurrent.ExecutionContext
import org.jsoup.Jsoup

class DefaultHtmlProcessor(cache: Cache)(implicit context: ExecutionContext) extends CachingProcessor {
  override def process(request: Request, response: Future[Response]): Future[Response] = {
    for {
      response <- response
      page <- cache.getOrElseUpdate(request.url, getPage(request.url))
    } yield {
      val titleOpt = extractTitle(request.url, page.getResponseBody)
      val imageUrls = response.image_urls ++ extractImages(page.getResponseBody)
      response.copy(title = titleOpt, content_type = Some(Html), image_urls = imageUrls)
    }
  }

  def getPage(address: String) = {
    cache getOrElseUpdate(address, {
      val req = url(address)
      Http(req)
    })
  }

  def extractTitle(address: String, body: String): Option[String] = {
    val doc = Jsoup.parse(body, address)
    val titleElems = doc.select("title")
    if (titleElems.size > 0) Some(titleElems.first.text)
    else None
  }

  def extractImages(body: String): Seq[String] = {
    List()
  }
}