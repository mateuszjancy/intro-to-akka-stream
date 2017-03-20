package app.flashcard.route

import akka.NotUsed
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, _}
import akka.stream.scaladsl.Source
import app.flashcard.repository.FlashcardRepository
import app.flashcard.route.QuizRoute._
import spray.json.DefaultJsonProtocol

class QuizRoute(flashcardRepository: FlashcardRepository) extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val questionJson = jsonFormat1(Question)
  implicit val answerJson = jsonFormat1(Answer)
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  /**
    * Find all flashcards and map them to Question DTO.
    *
    * @return
    */
  def question = path("question") {
    get {
      val response: Source[Question, NotUsed] = flashcardRepository.find.map(flashcard => Question(flashcard.word))
      complete(response)
    }
  }

  /**
    * Find flashcard for given word and check if translation is "equalsIgnoreCase" to user translation. Send back Answer DTO.
    *
    * @return
    */
  def answer = path("answer") {
    get {
      parameter('word) { word =>
        parameter('translation) { translation =>
          val response: Source[Answer, NotUsed] = flashcardRepository
            .get(word)
            .map(flashcard => Answer(flashcard.translation.equalsIgnoreCase(translation)))

          complete(response)
        }
      }
    }
  }

  def routes = pathPrefix("quiz")(question ~ answer)
}

object QuizRoute {

  case class Question(word: String)

  case class Answer(correct: Boolean)

  def apply(flashcardRepository: FlashcardRepository): QuizRoute = new QuizRoute(flashcardRepository)
}