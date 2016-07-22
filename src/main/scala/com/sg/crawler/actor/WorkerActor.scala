package com.sg.crawler.actor

import akka.actor.Actor
import com.sg.crawler.{CrawlResponse, CrawlUrl, LinkRequest, LinksAvailable}

class WorkerActor extends Actor {
  override def receive: Receive = {
    case LinksAvailable =>
      sender() ! LinkRequest
    case CrawlUrl(url) =>
      val urls = crawl(url)
      if (urls.nonEmpty) sender() ! CrawlResponse(urls)
      sender() ! LinkRequest
  }

  def crawl(url: String): List[String] = {
    ???
  }
}
