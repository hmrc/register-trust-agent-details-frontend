/*
 * Copyright 2020 HM Revenue & Customs
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

import com.google.inject.Inject
import config.FrontendAppConfig
import connector.SubmissionDraftConnector
import controllers.actions.AgentActionSets
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import utils.mappers.AgentDetailsMapper
import viewmodels.AnswerSection
import views.html.CheckYourAnswersView

import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            val appConfig: FrontendAppConfig,
                                            actions: AgentActionSets,
                                            agentMapper: AgentDetailsMapper,
                                            trustsConnector: SubmissionDraftConnector,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView,
                                            countryOptions : CountryOptions
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserWithData {
    implicit request =>

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers)

        val sections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.agentInternalReference,
              checkYourAnswersHelper.agentName,
              checkYourAnswersHelper.agentAddressYesNo,
              checkYourAnswersHelper.agentUKAddress,
              checkYourAnswersHelper.agentInternationalAddress,
              checkYourAnswersHelper.agenciesTelephoneNumber
            ).flatten
          )
        )

        Ok(view(sections))
  }

  def onSubmit() = actions.authWithData.async {
    implicit request =>

      agentMapper(request.userAnswers) match {
        case Some(agentDetails) =>
          for {
            _ <- trustsConnector.addAgentDetails(agentDetails)
          } yield {
            Redirect(appConfig.registrationProgress)
          }
        case None =>
          Logger.warn("[CheckYourAnswersController][submit] Unable to generate agent details to submit.")
          Future.successful(InternalServerError)
      }
  }

}
