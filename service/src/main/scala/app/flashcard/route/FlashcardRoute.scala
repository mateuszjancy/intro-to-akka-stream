package app.flashcard.route

import akka.NotUsed
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Source
import app.flashcard.repository.FlashcardRepository
import app.flashcard.repository.FlashcardRepository.Flashcard
import app.flashcard.route.FlashcardRoute.NewFlashcard
import spray.json.DefaultJsonProtocol

/**
  * Implement trivial CRud.
  *
  * @param flashcardRepository
  */
class FlashcardRoute(flashcardRepository: FlashcardRepository) extends DefaultJsonProtocol with SprayJsonSupport {

  private implicit val flashcardJson = jsonFormat2(Flashcard)
  private implicit val newFlashcardJson = jsonFormat2(NewFlashcard)

  //enable JSON Streaming, ir needs to be in implicit scope
  private implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()


  /**
    * Use asSourceOf in order to get entity as a stream. consume directive is can "consume" Source.
    * Consume NewFlashcard entity, map it to Flashcard and use flashcardRepository in order to store it.
    * Sent newly created entity to client.
    */
  def create = post {
    entity(asSourceOf[NewFlashcard]) { newFlashcard =>
      val response: Source[Flashcard, NotUsed] = newFlashcard.map(_.toFlashcard).via(flashcardRepository.add)
      complete(response)
    }
  }


  /**
    * consume directive is can "consume" Source.
    * Sent all items to client.
    */
  def find = get {
    val response: Source[Flashcard, NotUsed] = flashcardRepository.find
    complete(response)
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
