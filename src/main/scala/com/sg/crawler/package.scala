package com.sg.crawler


case class Initialize()
case class LinksAvailable() // Supervisor send workers message to tell there are more links
case class CrawlUrl(url: String) // send url to crawl to worker

case class LinkRequest() // if worker request work
case class CrawlResponse(urls: List[String]) // List of urls found by the workers



