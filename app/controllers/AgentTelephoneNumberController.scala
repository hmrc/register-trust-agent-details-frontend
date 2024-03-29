/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.AgentTelephoneNumberFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.{AgentNamePage, AgentTelephoneNumberPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.AgentTelephoneNumberView

import scala.concurrent.{ExecutionContext, Future}

class AgentTelephoneNumberController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                registrationsRepository: RegistrationsRepository,
                                                navigator: Navigator,
                                                formProvider: AgentTelephoneNumberFormProvider,
                                                actionSet: AgentActionSets,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: AgentTelephoneNumberView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) =
    actionSet.requiredAnswerWithAgent(draftId, RequiredAnswer(AgentNamePage, routes.AgentNameController.onPageLoad(draftId)))

  val form: Form[String] = formProvider()

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val agencyName = request.userAnswers.get(AgentNamePage).get.toString

      val preparedForm = request.userAnswers.get(AgentTelephoneNumberPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, agencyName))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val agencyName = request.userAnswers.get(AgentNamePage).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, agencyName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AgentTelephoneNumberPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AgentTelephoneNumberPage, draftId, updatedAnswers))
        }
      )
  }
}
