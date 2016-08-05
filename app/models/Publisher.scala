package models

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future

case class Publisher(id: Long, name: String)

case class PublisherWithProperties(id: Long, name: String, properties: List[PublisherProperty])

class PublisherRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, publisherPropertyRepo: PublisherPropertyRepo) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.driver.api._

  private val Publishers = TableQuery[PublisherTable]

  def all: Future[List[Publisher]] = dbConfig.db.run(Publishers.to[List].result)

  def addProperty(name: String, publisherId: Long): Future[Long] = {
    val interaction = for {
      id <- publisherPropertyRepo.insert(name, publisherId)
    } yield id
    db.run(interaction.transactionally)
  }

  private class PublisherTable(tag: Tag) extends Table[Publisher](tag, "PUBLISHER") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def name = column[String]("NAME")

    def * = (id, name) <> (Publisher.tupled, Publisher.unapply)

  }

}