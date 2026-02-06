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

package base

import controllers.actions.register._
import controllers.actions.{FakeDraftIdRetrievalActionProvider, _}
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import org.scalatest.{BeforeAndAfter, TestSuite, TryValues}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import utils.TestUserAnswers

trait SpecBaseHelpers
    extends PlaySpec with GuiceOneAppPerSuite with TryValues with Mocked with BeforeAndAfter with FakeTrustsApp {
  this: TestSuite =>

  final lazy val fakeDraftId: String = TestUserAnswers.draftId

  def emptyUserAnswers: UserAnswers = TestUserAnswers.emptyUserAnswers

  final lazy val fakeNavigator = new FakeNavigator()

  private def fakeDraftIdAction(userAnswers: Option[UserAnswers]) = new FakeDraftIdRetrievalActionProvider(userAnswers)

  protected def applicationBuilder(
    userAnswers: Option[UserAnswers] = None,
    affinityGroup: AffinityGroup = AffinityGroup.Agent,
    enrolments: Enrolments = Enrolments(Set.empty[Enrolment]),
    navigator: Navigator = fakeNavigator
  ): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[RegistrationDataRequiredAction].to[RegistrationDataRequiredActionImpl],
        bind[RegistrationIdentifierAction].toInstance(
          new FakeIdentifyForRegistration(affinityGroup, frontendAppConfig)(injectedParsers, trustsAuth, enrolments)
        ),
        bind[DraftIdRetrievalActionProvider].toInstance(fakeDraftIdAction(userAnswers)),
        bind[RegistrationsRepository].toInstance(registrationsRepository),
        bind[AffinityGroup].toInstance(Agent),
        bind[Navigator].toInstance(navigator)
      )

}

trait SpecBase extends PlaySpec with SpecBaseHelpers
