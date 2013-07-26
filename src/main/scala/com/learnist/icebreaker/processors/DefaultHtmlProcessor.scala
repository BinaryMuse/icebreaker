package com.learnist.icebreaker.processors

import dispatch._
import com.learnist.icebreaker._
import CachingProcessor.Cache
import scala.concurrent.ExecutionContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class DefaultHtmlProcessor(cache: Cache)(implicit context: ExecutionContext) extends CachingProcessor {
  override def process(request: Request, response: Future[Response]): Future[Response] = {
    val req = cache.getOrElseUpdate(request.url, getPage(request.url))

    val newResponse = for {
      response <- response
      page <- req
    } yield {
      val titleOpt = extractTitle(request.url, page.getResponseBody)
      val imageUrls = response.image_urls ++ extractImages(request.url, page.getResponseBody)
      response.copy(title = titleOpt, content_type = Some(Html), image_urls = imageUrls)
    }

    // If we fail to contac the host, abandon all hope and return the original response.
    newResponse recoverWith {
      case _ => response
    }
  }

  def getPage(address: String) = {
    val req = url(address)
    Http.configure(_.setFollowRedirects(true).setConnectionTimeoutInMs(2500))(req)
  }

  def extractTitle(address: String, body: String): Option[String] = {
    val doc = Jsoup.parse(body, address)
    val titleElems = doc.select("title")
    if (titleElems.size > 0) Some(titleElems.first.text)
    else None
  }

  def extractImages(address: String, body: String): Seq[String] = {
    val doc = Jsoup.parse(body, address)
    val imageElems = doc.select("img").toArray(Array[Element]())
    val srcs = imageElems map { elem =>
      elem.attr("src")
    }
    srcs toList
  }
}