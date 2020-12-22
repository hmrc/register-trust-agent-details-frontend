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

package navigation.routes

import config.FrontendAppConfig
import controllers.routes
import models.{NormalMode, UserAnswers}
import pages.Page
import pages.agent._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

object AgentRoutes {
  def route(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case AgentInternalReferencePage => _ => _ => controllers.routes.AgentNameController.onPageLoad(NormalMode, draftId)
    case AgentNamePage => _ => _ => controllers.routes.AgentAddressYesNoController.onPageLoad(NormalMode, draftId)
    case AgentAddressYesNoPage => _ => ua => agentAddressYesNoRoute(ua, draftId)
    case AgentUKAddressPage => _ => _ => controllers.routes.AgentTelephoneNumberController.onPageLoad(NormalMode, draftId)
    case AgentInternationalAddressPage => _ => _ => controllers.routes.AgentTelephoneNumberController.onPageLoad(NormalMode, draftId)
    case AgentTelephoneNumberPage => _ => _ => controllers.routes.AgentAnswerController.onPageLoad(draftId)
    case AgentAnswerPage => _ => _ => completedRoute(draftId, config = FrontendAppConfig)
  }

  private def agentAddressYesNoRoute(userAnswers: UserAnswers, draftId: String) : Call =
    userAnswers.get(AgentAddressYesNoPage) match {
      case Some(false) => controllers.routes.AgentInternationalAddressController.onPageLoad(NormalMode, draftId)
      case Some(true) => controllers.routes.AgentUKAddressController.onPageLoad(NormalMode, draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  private def completedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }
}

