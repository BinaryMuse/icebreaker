package com.learnist.icebreaker

import scala.concurrent.Future
import scala.collection.concurrent

abstract class CachingProcessor extends Processor

object CachingProcessor {
  type Cache = concurrent.Map[String, Future[Response]]
}
