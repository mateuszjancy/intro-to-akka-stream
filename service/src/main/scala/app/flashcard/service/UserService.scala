package app.flashcard.service

import java.util.UUID

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source, ZipWith}
import app.flashcard.repository.UserRepository
import app.flashcard.repository.UserRepository.User
import app.flashcard.service.MailService.{Mail, MailTemplate}

import scala.concurrent.duration._

class UserService(userRepository: UserRepository, mailService: MailService) {

  private def charge: Flow[UUID, BigDecimal, NotUsed] = Flow[UUID].map { _ => BigDecimal(4) }

  //@formatter:off
  /**

                    ---> only language ---> mailService.template --->
    User as a input ---> only id ---------> charge -----------------> Mail as a output
                    ------------------------------------------------>

    Use Broadcast to split user data to three streams. map on Broadcast is allowed.
    Use ZipWith[MailTemplate, BigDecimal, User, Mail] to zip MailTemplate, BigDecimal, User to Mail, mailService.fillBody can be used.
    */
  //@formatter:on
  private def calculate: Flow[User, Mail, NotUsed] = Flow.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

    val user = ???
    val zip = ???

    //@formatter:off
    //Fancy graph....
    //@formatter:on

    FlowShape(user.in, zip.out)
  })


  /**
    * Check Source.tick.
    *
    * @return
    */
  def sendSummary = Source.single(()) // <-- replace
    .mapConcat(_ => userRepository.find)
    .via(calculate)
    .via(mailService.send)
    .to(Sink.ignore)

}

object UserService {
  def apply(userRepository: UserRepository, mailService: MailService): UserService =
    new UserService(userRepository, mailService)
}
