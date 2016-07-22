package com.sg.crawler.actor

import akka.actor.{Actor, ActorRef, Props}
import com.sg.crawler._

import scala.collection.mutable

class SupervisorActor(domain: String, workers: List[ActorRef]) extends Actor {
  private val queue = mutable.Queue[String]()
  private var visitedUrls = mutable.Set[String]()

  override def receive: Receive = {
    case Initialize =>
      queue.enqueue(domain)
      workers.foreach(_ ! LinksAvailable)
    case CrawlResponse(urls) =>
      urls.foreach { url =>
        if (!visitedUrls.contains(url)) {
          if (validUrl(url))
            queue.enqueue(url)
          else
            visitedUrls = visitedUrls + url
        }
      }
      if (queue.nonEmpty)
        workers.foreach(_ ! LinksAvailable)
    case LinkRequest =>
      if (queue.nonEmpty) {
        val url = queue.dequeue
        visitedUrls = visitedUrls + url
        sender() ! CrawlUrl(url)
      }
  }

  def validUrl(url: String): Boolean = ???

}

object SupervisorActor {
  val props = Props[SupervisorActor]
}
