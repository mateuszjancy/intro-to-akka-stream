package app.flashcard.repository

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import app.flashcard.repository.FlashcardRepository._

class FlashcardRepository(db: DB[Flashcard]) {

  private val parallelism: Int = 2

  def find: Source[Flashcard, NotUsed] = Source.fromFuture(db.find).mapConcat(identity)

  def add: Flow[Flashcard, Flashcard, NotUsed] = Flow[Flashcard].mapAsync(parallelism)(db.insert)

  def get(word: String): Source[Flashcard, NotUsed] = Source.fromFuture(db.get(el => el.word == word))
}

object FlashcardRepository {

  case class Flashcard(word: String, translation: String)

  def apply(db: DB[Flashcard]): FlashcardRepository = new FlashcardRepository(db)
}
