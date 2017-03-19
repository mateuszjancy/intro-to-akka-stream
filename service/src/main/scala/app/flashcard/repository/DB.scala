package app.flashcard.repository

import scala.concurrent.Future

class DB[T] {
  private var db = Set.empty[T]

  def insert(el: T): Future[T] = Future.successful {
    db = db + el;
    el
  }

  def find: Future[Set[T]] = Future.successful(db)

  def get(criteria: T => Boolean): Future[T] = Future.successful(db.find(criteria).get)
}

object DB {
  def apply[T](): DB[T] = new DB[T]()
}