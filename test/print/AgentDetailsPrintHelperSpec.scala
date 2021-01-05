/*
 * Copyright 2021 HM Revenue & Customs
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

package print

import base.SpecBaseHelpers
import models.UKAddress
import org.scalatest.FreeSpec
import org.scalatest.MustMatchers.convertToAnyMustWrapper
import pages._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class AgentDetailsPrintHelperSpec extends FreeSpec with SpecBaseHelpers {

  private val helper = injector.instanceOf[AgentDetailsPrintHelper]

  private val agentName = "Agency Ltd"

  "Agent details printer" - {

    "print section for UK based agent" - {

      val userAnswers = emptyUserAnswers
        .set(AgentInternalReferencePage, "CRN/12.1").success.value
        .set(AgentNamePage, agentName).success.value
        .set(AgentAddressYesNoPage, true).success.value
        .set(AgentUKAddressPage, UKAddress("line1", "line2", postCode = "NE981ZZ")).success.value
        .set(AgentTelephoneNumberPage, "+telephone").success.value

      val result = helper.printSection(userAnswers, agentName, fakeDraftId)

      result mustBe AnswerSection(
        None,
        Seq(
          AnswerRow("agentInternalReference.checkYourAnswersLabel", Html("CRN/12.1"), Some(controllers.routes.AgentInternalReferenceController.onPageLoad(fakeDraftId).url), agentName),
          AnswerRow("agentName.checkYourAnswersLabel", Html(agentName), Some(controllers.routes.AgentNameController.onPageLoad(fakeDraftId).url), agentName),
          AnswerRow("agentAddressYesNo.checkYourAnswersLabel", Html("Yes"), Some(controllers.routes.AgentAddressYesNoController.onPageLoad(fakeDraftId).url), agentName),
          AnswerRow("site.address.uk.checkYourAnswersLabel", Html("line1<br />line2<br />NE981ZZ"), Some(controllers.routes.AgentUKAddressController.onPageLoad(fakeDraftId).url), agentName),
          AnswerRow("agentTelephoneNumber.checkYourAnswersLabel", Html("+telephone"), Some(controllers.routes.AgentTelephoneNumberController.onPageLoad(fakeDraftId).url), agentName)
        )
      )
    }

  }

}
