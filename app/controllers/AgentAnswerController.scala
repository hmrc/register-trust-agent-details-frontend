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

import controllers.actions.AgentActionSets
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.{AgentAnswerPage, AgentNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import print.AgentDetailsPrintHelper
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.AgentAnswerView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AgentAnswerController @Inject() (
  override val messagesApi: MessagesApi,
  navigator: Navigator,
  actionSet: AgentActionSets,
  val controllerComponents: MessagesControllerComponents,
  view: AgentAnswerView,
  registrationsRepository: RegistrationsRepository,
  printHelper: AgentDetailsPrintHelper
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    actionSet.identifiedUserWithData(draftId)

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) { implicit request =>
    val name = request.userAnswers.get(AgentNamePage).getOrElse("")

    val answers = printHelper.checkDetailsSection(request.userAnswers, name, draftId)

    Ok(view(draftId, Seq(answers)))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async { implicit request =>
    // need to set user answers in case user has been redirected here from trusts-frontend after data adjusted
    registrationsRepository.set(request.userAnswers) map { _ =>
      Redirect(navigator.nextPage(AgentAnswerPage, draftId, request.userAnswers))
    }
  }

}
