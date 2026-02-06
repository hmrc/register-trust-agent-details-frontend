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

import controllers.actions.{AgentActionSets, RequiredAnswer}
import forms.YesNoFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.{AgentAddressUKYesNoPage, AgentNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.AgentAddressYesNoView

import scala.concurrent.{ExecutionContext, Future}

class AgentAddressYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  navigator: Navigator,
  yesNoFormProvider: YesNoFormProvider,
  actionSet: AgentActionSets,
  val controllerComponents: MessagesControllerComponents,
  view: AgentAddressYesNoView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("agentAddressUKYesNo")

  private def actions(draftId: String) =
    actionSet.requiredAnswerWithAgent(
      draftId,
      RequiredAnswer(AgentNamePage, routes.AgentNameController.onPageLoad(draftId))
    )

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) { implicit request =>
    val name = request.userAnswers.get(AgentNamePage).get

    val preparedForm = request.userAnswers.get(AgentAddressUKYesNoPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, draftId, name))
  }

  def onSubmit(draftId: String) = actions(draftId).async { implicit request =>
    val name = request.userAnswers.get(AgentNamePage).get

    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) => Future.successful(BadRequest(view(formWithErrors, draftId, name))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AgentAddressUKYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AgentAddressUKYesNoPage, draftId, updatedAnswers))
      )
  }

}
