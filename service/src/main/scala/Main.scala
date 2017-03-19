import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn

object Main extends App with Context {
  override implicit lazy val actorSystem = ActorSystem("my-system")
  override implicit lazy val materializer = ActorMaterializer()

  implicit val executionContext = actorSystem.dispatcher

  userService.sendSummary.run()

  val bindingFuture = Http().bindAndHandle(quizRoute.routes ~ flashcardRoute.routes, "localhost", 8090)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => actorSystem.terminate())
}
