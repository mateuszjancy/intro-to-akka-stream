import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.Materializer
import app.flashcard.repository.FlashcardRepository.Flashcard
import app.flashcard.repository.UserRepository.User
import app.flashcard.repository.{DB, FlashcardRepository, UserRepository}
import app.flashcard.route.{FlashcardRoute, QuizRoute}
import app.flashcard.service.{MailService, UserService}

trait Context {
  implicit val actorSystem: ActorSystem
  implicit val materializer: Materializer

  //Repositories
  val flashcardRepository: FlashcardRepository = FlashcardRepository(DB[Flashcard]())
  val userRepository: UserRepository = UserRepository(List(
    User(UUID.fromString("bfc17252-145f-40ab-9f4f-d4464f38b384"), "marta", "marta@mail.com", "en"),
    User(UUID.fromString("c90d3ecd-c85b-465a-a19e-cdb89794290c"), "mateusz", "mateusz@mail.com", "pl")
  ))

  //Services
  val mailService: MailService = MailService()
  val userService: UserService = UserService(userRepository, mailService)

  //Routes
  val flashcardRoute: FlashcardRoute = FlashcardRoute(flashcardRepository)
  val quizRoute: QuizRoute = QuizRoute(flashcardRepository)
}
