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

package generators

import org.scalacheck.Arbitrary
import pages.agent._

trait PageGenerators {

  implicit lazy val arbitraryAgentInternationalAddressPage: Arbitrary[AgentInternationalAddressPage.type] =
    Arbitrary(AgentInternationalAddressPage)

  implicit lazy val arbitraryAgentUKAddressPage: Arbitrary[AgentUKAddressPage.type] =
    Arbitrary(AgentUKAddressPage)

  implicit lazy val arbitraryAgentAddressYesNoPage: Arbitrary[AgentAddressYesNoPage.type] =
    Arbitrary(AgentAddressYesNoPage)

  implicit lazy val arbitraryAgentNamePage: Arbitrary[AgentNamePage.type] =
    Arbitrary(AgentNamePage)

  implicit lazy val arbitraryAgentInternalReferencePage: Arbitrary[AgentInternalReferencePage.type] =
    Arbitrary(AgentInternalReferencePage)

  implicit lazy val arbitraryAgentTelephoneNumberPage: Arbitrary[AgentTelephoneNumberPage.type] =
    Arbitrary(AgentTelephoneNumberPage)

}
