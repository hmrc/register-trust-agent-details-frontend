/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import controllers.actions.register.RegistrationIdentifierAction
import logging.Session
import models.UserAnswers
import pages.AgentTelephoneNumberPage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject() (
  identify: RegistrationIdentifierAction,
  repository: RegistrationsRepository,
  val controllerComponents: MessagesControllerComponents
)(implicit val executionContext: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private def redirectToCheckAnswers(draftId: String): Call =
    controllers.routes.AgentAnswerController.onPageLoad(draftId)

  private def redirectToStart(draftId: String): Call =
    controllers.routes.AgentInternalReferenceController.onPageLoad(draftId)

  def onPageLoad(draftId: String): Action[AnyContent] = identify.async { implicit request =>
    repository.get(draftId) flatMap {
      case Some(answers) =>
        Future.successful(redirect(answers, draftId))
      case _             =>
        val userAnswers = UserAnswers(draftId, Json.obj(), request.identifier)
        repository.set(userAnswers) map { _ =>
          redirect(userAnswers, draftId)
        }
    }
  }

  private def redirect(userAnswers: UserAnswers, draftId: String)(implicit hc: HeaderCarrier): Result =
    userAnswers.get(AgentTelephoneNumberPage) match {
      case Some(_) =>
        logger.info(s"[${Session.id(hc)}] Sending user to check agent answers")
        Redirect(redirectToCheckAnswers(draftId))
      case None    =>
        logger.info(s"[${Session.id(hc)}] Starting journey for agent details")
        Redirect(redirectToStart(draftId))
    }

}
