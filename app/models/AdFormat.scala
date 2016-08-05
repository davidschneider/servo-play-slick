package models

import java.sql.Timestamp
import javax.inject.Inject

import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class AdFormat(id: Long, name: String, createdAt: DateTime, description: String, visible: Boolean, `type`: String, oldAdFormatId: Long, supportsHtml: Boolean, supportsJs: Boolean)

class AdFormatRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.driver.api._

  private val AdFormats = TableQuery[AdFormatTable]

  def all: Future[List[AdFormat]] = db.run(AdFormats.to[List].result)

  private class AdFormatTable(tag: Tag) extends Table[AdFormat](tag, "AD_FORMAT") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def name = column[String]("NAME")

    def created_at = column[DateTime]("CREATED_AT")

    def description = column[String]("DESCRIPTION")

    def visible = column[Boolean]("VISIBLE", O.Default(false))

    def `type` = column[String]("TYPE")

    def old_ad_format_id = column[Long]("OLD_AD_FORMAT_ID")

    def supports_html = column[Boolean]("SUPPORTS_HTML", O.Default(false))

    def supports_js = column[Boolean]("SUPPORTS_JS", O.Default(false))

    def * = (id, name, created_at, description, visible, `type`, old_ad_format_id, supports_html, supports_js) <> (AdFormat.tupled, AdFormat.unapply)

  }

  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, java.sql.Timestamp](
    d => new Timestamp(d.toDate.getTime), d => new DateTime(d.getTime)
  )

}