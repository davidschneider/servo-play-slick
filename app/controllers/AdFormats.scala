package controllers

import javax.inject.Inject

import models.{AdFormat, AdFormatRepo}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits._

class AdFormats @Inject()(adFormatRepo: AdFormatRepo)
  extends Controller {

  implicit val adFormatF = Json.format[AdFormat]

  def all = Action.async {
    implicit request =>
      adFormatRepo.all.map(x => Ok(Json.toJson(x)))
  }

}