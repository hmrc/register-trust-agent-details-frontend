/*
 * Copyright 2022 HM Revenue & Customs
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
import org.scalatest.OptionValues
import play.api.Application
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.TestUserAnswers

import scala.concurrent.Future

class LogoutControllerSpec extends SpecBase with OptionValues {

  "Logout Controller" must {

    "Redirect to feedback form with new session" in {

      val application: Application = applicationBuilder(userAnswers = Some(TestUserAnswers.emptyUserAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, routes.LogoutController.logout().url)
        .withSession("test" -> "value")

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER

      session(result).get("test") mustBe None

      redirectLocation(result).value mustBe frontendAppConfig.logoutUrl

      application.stop()
    }

  }
}
