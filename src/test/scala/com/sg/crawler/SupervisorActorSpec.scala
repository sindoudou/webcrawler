package com.sg.crawler

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.sg.crawler.actor.SupervisorActor
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class SupervisorActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
 
  def this() = this(ActorSystem("SupervisorActorSpec"))
 
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
 
  "A Supervisor actor" must {
    "send LinksAvailable to workers on Initialize" in {
      val domain = "http://domain.do"
      val worker =  TestProbe()
      val supervisorActor = TestActorRef(new SupervisorActor(domain, List(worker.ref)))

      supervisorActor ! Initialize
      worker.expectMsg(LinksAvailable)
    }

    "send LinksAvailable to workers on CrawlResponse if valid url found" in {
      val domain = "http://domain.do"
      val worker =  TestProbe()
      val supervisorActor = TestActorRef(new SupervisorActor(domain, List(worker.ref)){
        override def validUrl(url: String): Boolean = true
      })

      supervisorActor ! CrawlResponse(List("http://someurl"))
      worker.expectMsg(LinksAvailable)
    }

    "not send LinksAvailable to workers on CrawlResponse if no valid urls found" in {
      val domain = "http://domain.do"
      val worker =  TestProbe()
      val supervisorActor = TestActorRef(new SupervisorActor(domain, List(worker.ref)){
        override def validUrl(url: String): Boolean = false
      })

      supervisorActor ! CrawlResponse(List("http://someurl"))
      worker.expectNoMsg()
    }

    "send CrawlUrl to workers on LinkRequest" in {
      val domain = "http://domain.do"
      val worker =  TestProbe()
      val supervisorActor = TestActorRef(new SupervisorActor(domain, List(worker.ref)))

      supervisorActor ! Initialize

      worker.send(supervisorActor, LinkRequest)

      worker.expectMsgAllOf(LinksAvailable, CrawlUrl(domain))
    }

    // TODO: Test visitedUrl, queue

  }


}
