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

package controllers

import base.SpecBase
import models.{InternationalAddress, UKAddress, UserAnswers}
import pages._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import print.AgentDetailsPrintHelper
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.AgentAnswerView

class AgentAnswerControllerSpec extends SpecBase {

  private val agentID: AffinityGroup.Agent.type = AffinityGroup.Agent

  "AgentAnswer Controller" must {

    "return OK and the correct view for a UK address GET" in {

      val answers: UserAnswers =
        emptyUserAnswers
          .set(AgentTelephoneNumberPage, "123456789").success.value
          .set(AgentUKAddressPage, UKAddress("Line1", "Line2", None, Some("TownOrCity"), "NE62RT")).success.value
          .set(AgentAddressUKYesNoPage, true).success.value
          .set(AgentNamePage, "Sam Curran Trust").success.value
          .set(AgentInternalReferencePage, "123456789").success.value

      val application = applicationBuilder(userAnswers = Some(answers), agentID).build()

      val printHelper = application.injector.instanceOf[AgentDetailsPrintHelper]

      val answerSection = printHelper.checkDetailsSection(answers, "Sam Curran Trust", fakeDraftId)

      val request = FakeRequest(GET, routes.AgentAnswerController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AgentAnswerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, Seq(answerSection))(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a International address GET" in {

      val answers: UserAnswers =
        emptyUserAnswers
          .set(AgentTelephoneNumberPage, "123456789").success.value
          .set(AgentInternationalAddressPage, InternationalAddress("Line1", "Line2", None, "Country")).success.value
          .set(AgentAddressUKYesNoPage, false).success.value
          .set(AgentNamePage, "Sam Curran Trust").success.value
          .set(AgentInternalReferencePage, "123456789").success.value

      val application = applicationBuilder(userAnswers = Some(answers), agentID).build()

      val printHelper = application.injector.instanceOf[AgentDetailsPrintHelper]

      val answerSection = printHelper.checkDetailsSection(answers, "Sam Curran Trust", fakeDraftId)

      val request = FakeRequest(GET, routes.AgentAnswerController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AgentAnswerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, Seq(answerSection))(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, agentID).build()

      val request = FakeRequest(GET, routes.AgentAnswerController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, agentID).build()

      val request = FakeRequest(POST, routes.AgentAnswerController.onSubmit(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}