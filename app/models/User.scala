package models

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

case class User(id: Long, firstName: String, lastName: String, email: String, title: String, phoneNumber: String, isAdmin: Boolean, active: Boolean)

class UserRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.driver.api._

  private val Users = TableQuery[UserTable]

  private class UserTable(tag: Tag) extends Table[User](tag, "USER") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def first_name = column[String]("FIRST_NAME")

    def last_name = column[String]("LAST_NAME")

    def email = column[String]("EMAIL")

    def title = column[String]("TITLE")

    def phone_number = column[String]("PHONE_NUMBER")

    def is_admin = column[Boolean]("IS_ADMIN")

    def active = column[Boolean]("ACTIVE")

    def * = (id, first_name, last_name, email, title, phone_number, is_admin, active) <> (User.tupled, User.unapply)

  }

}