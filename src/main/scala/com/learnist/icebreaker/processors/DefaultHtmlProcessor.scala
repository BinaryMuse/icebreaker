package com.learnist.icebreaker.processors

import com.learnist.icebreaker._
import CachingProcessor.Cache

import scala.concurrent.ExecutionContext

import dispatch._
import java.net.URI
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document,Element}

class DefaultHtmlProcessor(cache: Cache)(implicit context: ExecutionContext) extends CachingProcessor {
  override def process(request: Request, response: Future[Response]): Future[Response] = {
    val req = cache.getOrElseUpdate(request.url, getPage(request.url))

    // Once response and req succeed, transform them into a new Future
    // that includes the extracted title, images, and content_type 
    val newResponse = for {
      response <- response
      page <- req
    } yield {
      implicit val doc = parseHtml(request.url, page.getResponseBody)
      val titleOpt = extractTitle
      val imageUrls = response.image_urls ++ extractImages(request.url)
      response.copy(title = titleOpt, content_type = Some(Html), image_urls = imageUrls)
    }

    // If the for comprehension resulved in a FAILED Future, just fall
    // back to the original response we were given originally.
    newResponse recoverWith {
      case _ => response
    }
  }

  private def getPage(address: String) = {
    val req = url(address)
    Http.configure(_.setFollowRedirects(true).setConnectionTimeoutInMs(2500))(req)
  }

  private def parseHtml(address: String, body: String): Document = Jsoup.parse(body, address)

  private def extractTitle(implicit doc: Document): Option[String] = {
    val titleElems = doc.select("title")
    if (titleElems.size > 0) Some(titleElems.first.text)
    else None
  }

  private def extractImages(address: String)(implicit doc: Document): Seq[String] = {
    val base = new URI(address)
    val imageElems = doc.select("img").toArray(Array[Element]())
    val imageUris = imageElems.map(_.attr("src")).map { (img: String) => base.resolve(new URI(img)) }
    imageUris.map(_.toString).toList
  }
}
