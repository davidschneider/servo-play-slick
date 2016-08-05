package controllers

import javax.inject.Inject

import models._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits._

class Publishers @Inject()(publishersRepo: PublisherRepo, publisherPropertyRepo: PublisherPropertyRepo) extends Controller {

  implicit val publisherPropertyF = Json.format[PublisherProperty]
  implicit val publisherF = Json.format[Publisher]
  implicit val publisherWithPropertiesF = Json.format[PublisherWithProperties]

  def all = Action.async {
    implicit request =>
      publishersRepo.all.map(x => Ok(Json.toJson(x)))
  }

  def allWithProperties = Action.async {
    implicit request =>

      val publishersF = publishersRepo.all
      val propertiesF = publisherPropertyRepo.all

      for {
        publishers <- publishersF
        properties <- propertiesF
      } yield {
        Ok {
          Json.toJson {
            val propertiesGrouped = properties.groupBy(_.publisherId)
            for {
              publisher <- publishers
            } yield PublisherWithProperties(publisher.id, publisher.name, propertiesGrouped.getOrElse(publisher.id, Nil))
          }
        }
      }

  }

}