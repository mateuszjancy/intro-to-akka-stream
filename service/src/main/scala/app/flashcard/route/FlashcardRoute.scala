package app.flashcard.route

import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import app.flashcard.repository.FlashcardRepository
import app.flashcard.repository.FlashcardRepository.Flashcard
import app.flashcard.route.FlashcardRoute.NewFlashcard
import spray.json.DefaultJsonProtocol

class FlashcardRoute(flashcardRepository: FlashcardRepository) extends DefaultJsonProtocol with SprayJsonSupport {

  private implicit val flashcardJson = jsonFormat2(Flashcard)
  private implicit val newFlashcardJson = jsonFormat2(NewFlashcard)
  private implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  def create = post {
    entity(asSourceOf[NewFlashcard]) { newFlashcard =>
      val response = newFlashcard.map(_.toFlashcard).via(flashcardRepository.add)
      complete(response)
    }
  }

  def find = get {
    complete(flashcardRepository.find)
  }

  def routes = path("flashcard")(find ~ create)
}

object FlashcardRoute {

  case class NewFlashcard(word: String, tranlation: String) {
    def toFlashcard = Flashcard(word, tranlation)
  }

  def apply(flashcardRepository: FlashcardRepository): FlashcardRoute =
    new FlashcardRoute(flashcardRepository)
}
