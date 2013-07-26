package com.learnist.icebreaker.processors

import com.learnist.icebreaker._

import scala.concurrent.Future
import scala.collection.concurrent
import com.ning.http.client

abstract class CachingProcessor extends Processor

object CachingProcessor {
  type Cache = concurrent.Map[String, Future[client.Response]]
}
