package models

import java.sql.Timestamp
import javax.inject.Inject

import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future

case class PublisherProperty(id: Long, name: String, publisherId: Long, deletedAt: Option[DateTime])

class PublisherPropertyRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.driver.api._

  private val PublisherProperties = TableQuery[PublisherPropertyTable]

  def all: Future[List[PublisherProperty]] = dbConfig.db.run(PublisherProperties.filterNot(_.deleted_at.isDefined).to[List].result)

  def findById(id: Long): Future[Option[PublisherProperty]] = dbConfig.db.run(PublisherProperties.filter(x => x.id === id && x.deleted_at.isEmpty).result.headOption)

  def findAllByPublisher(publisherId: Long): Future[List[PublisherProperty]] = dbConfig.db.run(PublisherProperties.filter(_.publisher_id === publisherId).filterNot(_.deleted_at.isDefined).sortBy(_.id).to[List].result)

  def insert(name: String, publisherId: Long): DBIO[Long] = PublisherProperties returning PublisherProperties.map(_.id) += PublisherProperty(0L, name, publisherId, None)

  def update(pp: PublisherProperty, publisherId: Long): Future[Int] = {
    val query = PublisherProperties.filter(x => x.id === pp.id && x.publisher_id === publisherId)
    val update = query.result.head.flatMap {
     x =>
        query.update(pp)
    }
    db.run(update)
  }

  def delete(id: Long, publisherId: Long): Future[Int] = {
    val query = PublisherProperties.filter(x => x.id === id && x.publisher_id === publisherId)
    val update = query.result.head.flatMap {
      pp =>
        query.update(pp.copy(deletedAt = Some(DateTime.now)))
    }
    db.run(update)
  }

  private class PublisherPropertyTable(tag: Tag) extends Table[PublisherProperty](tag, "PUBLISHER_PROPERTY") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def name = column[String]("NAME")

    def publisher_id = column[Long]("PUBLISHER_ID")

    def deleted_at = column[Option[DateTime]]("DELETED_AT")

    def * = (id, name, publisher_id, deleted_at) <> (PublisherProperty.tupled, PublisherProperty.unapply)

  }

  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, java.sql.Timestamp](
    d => new Timestamp(d.toDate.getTime), d => new DateTime(d.getTime)
  )

}