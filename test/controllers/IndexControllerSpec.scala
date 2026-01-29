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

import base.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.AgentTelephoneNumberPage
import play.api.Application
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.TestUserAnswers

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  "IndexController Controller" must {

    "Redirect to Internal Reference for new session" in {

      val application: Application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))

      val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.AgentInternalReferenceController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "Redirect to Check Answers for existing session" in {

      val userAnswers = TestUserAnswers.emptyUserAnswers
        .set(AgentTelephoneNumberPage, "telephone")
        .success
        .value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))

      val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.AgentAnswerController.onPageLoad(fakeDraftId).url

      application.stop()
    }
  }

}
