package app.flashcard.repository

import java.util.UUID

import app.flashcard.repository.UserRepository.User

class UserRepository(db: List[User]) {
  def find: List[User] = db
}

object UserRepository {

  case class User(id: UUID, name: String, mail: String, language: String)

  def apply(db: List[User]): UserRepository = new UserRepository(db)
}