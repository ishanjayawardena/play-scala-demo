package iuj.sc.demo.actor.image

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorSystem}
import iuj.sc.demo.actor.image.TransformerSupervisor.{Factory, TransformAll, TransformCompleted}
import iuj.sc.demo.config.Keys
import play.api.Logger
import play.api.libs.concurrent.InjectedActorSupport

import scala.util.Random

class ImageTransformer @Inject() (
                                   supervisorFactor: Factory,
                                   actorSystem: ActorSystem,
                                 ) extends Actor with InjectedActorSupport {

  private val logger = Logger(this.getClass)

  private var controller: ActorRef = ActorRef.noSender

  override def receive: Receive = {

    case request: TransformAll =>
      controller = sender()
      val supervisor: ActorRef = injectedChild(
        supervisorFactor(self),
        s"supervisor-${System.currentTimeMillis}-${Random.nextInt(1001)}",
        props => props.withDispatcher(Keys.LongRunningTasksDispatcher))
      supervisor ! request
      logger.info("Submitted Transform request to a supervisor")

    case result: TransformCompleted => controller ! result

    case _ =>
      logger.warn("Unknown message")
      unhandled(_)
  }
}
