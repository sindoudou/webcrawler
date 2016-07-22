package com.sg.crawler

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.example.PingActor
import com.sg.crawler.actor.{SupervisorActor, WorkerActor}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class WorkerActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
 
  def this() = this(ActorSystem("WorkerActorSpec"))
 
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
 
  "A Worker actor" must {
    "send LinkRequest to supervisor on LinksAvailable" in {
      val domain = "http://domain.do"
      val workerActor = TestActorRef(new WorkerActor)
      val supervisorActor = system.actorOf(Props(new SupervisorActor(domain, List(workerActor))))

      workerActor ! LinksAvailable
      expectMsg(LinkRequest)
    }

    "send CrawlResponse to supervisor if urls found" in {
      val domain = "http://domain.do"
      val links = List(s"$domain/news", s"$domain/weather")
      val workerActor = TestActorRef(new WorkerActor {
        override def crawl(url: String): List[String] = links
      })
      val supervisorActor = system.actorOf(Props(new SupervisorActor(domain, List(workerActor))))

      workerActor ! CrawlUrl(domain)
      expectMsg(CrawlResponse(links))
    }

    "send only LinkRequest if no links found when crawling" in {
      val domain = "http://domain.do"
      val workerActor = TestActorRef(new WorkerActor {
        override def crawl(url: String): List[String] = List.empty
      })
      val supervisorActor = system.actorOf(Props(new SupervisorActor(domain, List(workerActor))))

      workerActor ! CrawlUrl(domain)
      expectMsgAllOf(LinkRequest)
    }

    "send LinkRequest to supervisor when worker is done crawling" in {
      val domain = "http://domain.do"
      val links = List(s"$domain/news", s"$domain/weather")
      val workerActor = TestActorRef(new WorkerActor {
        override def crawl(url: String): List[String] = links
      })
      val supervisorActor = system.actorOf(Props(new SupervisorActor(domain, List(workerActor))))

      workerActor ! CrawlUrl(domain)

      expectMsgAllOf(CrawlResponse(links), LinkRequest)
    }
  }
}
