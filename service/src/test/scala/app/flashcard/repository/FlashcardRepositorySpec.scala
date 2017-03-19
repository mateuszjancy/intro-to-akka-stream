package app.flashcard.repository

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import app.flashcard.repository.FlashcardRepository.Flashcard
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.immutable.Seq
import scala.concurrent.Future


class FlashcardRepositorySpec extends FlatSpec with Matchers with ScalaFutures with MockitoSugar {
  implicit val actorSystem = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()

  val expected = Flashcard("czesc", "helo")
  val db = mock[DB[Flashcard]]
  when(db.find).thenReturn(Future.successful(Set(expected)))

  it should "work" in {
    val tested: Seq[Flashcard] = FlashcardRepository(db)
      .find
      .runWith(Sink.seq).futureValue
    tested should contain only expected
  }
}
