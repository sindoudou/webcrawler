package com.sg.crawler

import akka.actor.{ActorRef, ActorSystem, Props}
import com.sg.crawler.actor.{SupervisorActor, WorkerActor}

object WebCrawlerMain extends App {
  val system = ActorSystem("web-crawler-system")

  val domain = args(0)
  val workerCount = args(1)
  val workers = (1 to 3).map(i => system.actorOf(Props[WorkerActor], s"WorkerActor-$i")).toList
  val supervisor = system.actorOf(Props(new SupervisorActor(domain, workers)), "SupervisorActor")

  supervisor ! Initialize

  //TODO: ???
  system.awaitTermination()
}
