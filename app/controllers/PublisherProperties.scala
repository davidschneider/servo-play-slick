package controllers

import javax.inject.Inject

import models.{PublisherProperty, PublisherPropertyRepo, PublisherRepo}
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class PublisherProperties @Inject()(publisherRepo: PublisherRepo, publisherPropertyRepo: PublisherPropertyRepo) extends Controller {

  implicit val publisherPropertyF = Json.format[PublisherProperty]

  def getPublisherPropertyById(publisherId: Long, propertyId: Long) = Action.async {
    implicit request =>
      publisherPropertyRepo.findAllByPublisher(publisherId).map(x => Ok(Json.toJson(x)))
  }

  def createPublisherProperty(publisherId: Long) = Action.async(parse.json) {
    implicit request =>
      val name = (request.body \ "name").as[String]
      publisherRepo.addProperty(name, publisherId).map(id => Ok(Json.obj("id" -> id)))
  }

  def updatePublisherProperty(publisherId: Long, propertyId: Long) = Action.async(parse.json) {
    implicit request =>
      val p = request.body.validate[PublisherProperty]
      p match {
        case JsSuccess(pp, _) => publisherPropertyRepo.update(pp, publisherId).map {
          i =>
            if(i < 0) NotFound
            else NoContent
        }
        case _ => Future.successful(BadRequest)
      }
  }

  def deletePublisherProperty(publisherId: Long, propertyId: Long) = Action.async {
    implicit request =>
      publisherPropertyRepo.delete(propertyId, publisherId).map {
        i =>
          if(i < 0) NotFound
          else NoContent
      }
  }

}