package app.flashcard.service

import akka.NotUsed
import akka.stream.scaladsl.Flow
import app.flashcard.repository.UserRepository.User
import app.flashcard.service.MailService.{Mail, MailTemplate}

class MailService(templates: Map[String, MailTemplate]) {
  def send: Flow[Mail, Unit, NotUsed] = Flow[Mail].map { mail =>
    //@formatter:off
    println(s"""
      |
      | To: ${mail.to}
      | Subject: ${mail.subject}
      | >${mail.body}
    """.stripMargin)
    //@formatter:om
  }

  def template =  Flow[String].map(lang => templates(lang))

  def fillBody(mailTemplate: MailTemplate, price: BigDecimal, user: User): Mail = Mail(
      mailTemplate.subject,
      mailTemplate.body.replace("[price]", price.toString()),
      user.mail
    )

}

object MailService {
  case class Mail(subject: String, body: String, to: String)

  case class MailTemplate(subject: String, body: String)

  private val templates = Map(
    "pl" -> MailTemplate("Twoja dzienna portcja słówek", "Hej, pamiętaj że płacisz za to [price] zł."),
    "en" -> MailTemplate("Your dallt set ot flashcards", "Hi, keep in mind that it cost you [price] euro.")
  )

  def apply(): MailService = new MailService(templates)

}
