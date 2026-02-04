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

import base.SpecBase
import controllers.routes
import generators.Generators
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

trait AgentRoutes {

  self: ScalaCheckPropertyChecks with Generators with SpecBase =>

  def agentRoutes()(implicit navigator: Navigator) = {

    "go to AgentName from AgentInternalReference Page" in
      forAll(arbitrary[UserAnswers]) { userAnswers =>
        navigator
          .nextPage(AgentInternalReferencePage, fakeDraftId, userAnswers)
          .mustBe(routes.AgentNameController.onPageLoad(fakeDraftId))
      }

    "go to AgentAddressYesNo from AgentName Page" in
      forAll(arbitrary[UserAnswers]) { userAnswers =>
        navigator
          .nextPage(AgentNamePage, fakeDraftId, userAnswers)
          .mustBe(routes.AgentAddressYesNoController.onPageLoad(fakeDraftId))
      }

    "go to AgentUKAddress from AgentAddressYesNo Page when user answers yes" in
      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.set(AgentAddressUKYesNoPage, value = true).success.value
        navigator
          .nextPage(AgentAddressUKYesNoPage, fakeDraftId, answers)
          .mustBe(routes.AgentUKAddressController.onPageLoad(fakeDraftId))
      }

    "go to AgentTelephoneNumber from AgentUKAddress Page" in
      forAll(arbitrary[UserAnswers]) { userAnswers =>
        navigator
          .nextPage(AgentUKAddressPage, fakeDraftId, userAnswers)
          .mustBe(routes.AgentTelephoneNumberController.onPageLoad(fakeDraftId))
      }

    "go to AgentInternationalAddress from AgentAddressYesNo Page when user answers no" in
      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.set(AgentAddressUKYesNoPage, value = false).success.value
        navigator
          .nextPage(AgentAddressUKYesNoPage, fakeDraftId, answers)
          .mustBe(routes.AgentInternationalAddressController.onPageLoad(fakeDraftId))
      }

    "go to AgentTelephoneNumber from AgentInternationalAddress Page" in
      forAll(arbitrary[UserAnswers]) { userAnswers =>
        navigator
          .nextPage(AgentInternationalAddressPage, fakeDraftId, userAnswers)
          .mustBe(routes.AgentTelephoneNumberController.onPageLoad(fakeDraftId))
      }

    "go to CheckAgentAnswer Page from AgentTelephoneNumber page" in
      forAll(arbitrary[UserAnswers]) { userAnswers =>
        navigator
          .nextPage(AgentTelephoneNumberPage, fakeDraftId, userAnswers)
          .mustBe(routes.AgentAnswerController.onPageLoad(fakeDraftId))
      }
  }

}
