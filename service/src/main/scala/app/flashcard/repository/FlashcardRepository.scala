package app.flashcard.repository

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import app.flashcard.repository.FlashcardRepository._

/**
  * How to mix Future based API into Akka.streams.
  *
  * @param db
  */
class FlashcardRepository(db: DB[Flashcard]) {

  private val parallelism: Int = 2

  /**
    * Source can be build by multiple builder methods. Check check Source.from***.
    * mapConcat with identity function is like flatten function.
    *
    * @return
    */
  def find: Source[Flashcard, NotUsed] = Source.fromFuture(db.find).mapConcat(identity)

  /**
    * Flow need to be parametrized with input type. Flow represents set of collection like operations.
    * Check mapAsync.
    *
    * @return
    */
  def add: Flow[Flashcard, Flashcard, NotUsed] = Flow[Flashcard].mapAsync(parallelism)(db.insert)

  def get(word: String): Source[Flashcard, NotUsed] = Source.fromFuture(db.get(el => el.word == word))
}

object FlashcardRepository {

  case class Flashcard(word: String, translation: String)

  def apply(db: DB[Flashcard]): FlashcardRepository = new FlashcardRepository(db)
}
