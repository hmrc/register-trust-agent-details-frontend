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

package navigation

import config.FrontendAppConfig
import javax.inject.Inject
import models.ReadableUserAnswers
import pages._
import play.api.mvc.Call

class AgentDetailsNavigator @Inject() (config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(draftId)(page)(userAnswers)

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AgentInternalReferencePage    => _ => controllers.routes.AgentNameController.onPageLoad(draftId)
    case AgentNamePage                 => _ => controllers.routes.AgentAddressYesNoController.onPageLoad(draftId)
    case AgentUKAddressPage            => _ => controllers.routes.AgentTelephoneNumberController.onPageLoad(draftId)
    case AgentInternationalAddressPage => _ => controllers.routes.AgentTelephoneNumberController.onPageLoad(draftId)
    case AgentTelephoneNumberPage      => _ => controllers.routes.AgentAnswerController.onPageLoad(draftId)
    case AgentAnswerPage               => _ => completedRoute(draftId, config)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AgentAddressUKYesNoPage =>
      ua =>
        yesNoNav(
          ua,
          AgentAddressUKYesNoPage,
          controllers.routes.AgentUKAddressController.onPageLoad(draftId),
          controllers.routes.AgentInternationalAddressController.onPageLoad(draftId)
        )
  }

  private def completedRoute(draftId: String, config: FrontendAppConfig): Call =
    Call("GET", config.registrationProgressUrl(draftId))

  def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) orElse
      yesNoNavigation(draftId) orElse
      defaultRoute(draftId)

}
