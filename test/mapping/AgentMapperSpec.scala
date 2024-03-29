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

package mapping

import _root_.models._
import base.{SpecBase, SpecBaseHelpers}
import generators.Generators
import mapping.models.AgentDetails
import org.scalatest.OptionValues
import pages._

class AgentMapperSpec extends SpecBase with OptionValues with Generators with SpecBaseHelpers {

  final private val agentMapper: AgentDetailsMapper = new AgentDetailsMapper()

  "AgentMapper" must {

      "when user answers is empty" must {
        "must not be able to create AgentDetails" in {

          val userAnswers = emptyUserAnswers

          agentMapper.build(userAnswers) mustNot be(defined)
        }
      }

      "when user answers is not empty " must {

        "must able to create AgentDetails for a UK address" in {

          val userAnswers =
            emptyUserAnswers
              .set(AgentARNPage, "SARN123456").success.value
              .set(AgentNamePage, "Agency Name").success.value
              .set(AgentUKAddressPage, UKAddress("Line1", "Line2", None, Some("Newcastle"), "ab1 1ab")).success.value
              .set(AgentTelephoneNumberPage, "+1234567890").success.value
              .set(AgentInternalReferencePage, "1234-5678").success.value
              .set(AgentAddressUKYesNoPage, true).success.value

          agentMapper.build(userAnswers).value mustBe AgentDetails(
            arn = "SARN123456",
            agentName = "Agency Name",
            agentAddress = UKAddress("Line1", "Line2", None, Some("Newcastle"), "ab1 1ab"),
            agentTelephoneNumber = "+1234567890",
            clientReference = "1234-5678"
          )
        }

        "must able to create AgentDetails for a UK address with only required fields" in {

          val userAnswers =
            emptyUserAnswers
              .set(AgentARNPage, "SARN123456").success.value
              .set(AgentNamePage, "Agency Name").success.value
              .set(AgentUKAddressPage, UKAddress("Line1", "Newcastle", None, None, "NE62RT")).success.value
              .set(AgentTelephoneNumberPage, "+1234567890").success.value
              .set(AgentInternalReferencePage, "1234-5678").success.value
              .set(AgentAddressUKYesNoPage, true).success.value

          agentMapper.build(userAnswers).value mustBe AgentDetails(
            arn = "SARN123456",
            agentName = "Agency Name",
            agentAddress = UKAddress("Line1", "Newcastle", None, None, "NE62RT"),
            agentTelephoneNumber = "+1234567890",
            clientReference = "1234-5678"
          )
        }

        "must able to create AgentDetails for a international address" in {

          val userAnswers =
            emptyUserAnswers
              .set(AgentARNPage, "SARN123456").success.value
              .set(AgentNamePage, "Agency Name").success.value
              .set(AgentInternationalAddressPage, InternationalAddress("line1","line2",Some("line3"), "IN")).success.value
              .set(AgentTelephoneNumberPage, "+1234567890").success.value
              .set(AgentInternalReferencePage, "1234-5678").success.value
              .set(AgentAddressUKYesNoPage, false).success.value

          agentMapper.build(userAnswers).value mustBe AgentDetails(
            arn = "SARN123456",
            agentName = "Agency Name",
            agentAddress = InternationalAddress("line1","line2",Some("line3"), "IN"),
            agentTelephoneNumber = "+1234567890",
            clientReference = "1234-5678"
          )
        }

        "must able to create AgentDetails for a international address with minimum data" in {

          val userAnswers =
            emptyUserAnswers
              .set(AgentARNPage, "SARN123456").success.value
              .set(AgentNamePage, "Agency Name").success.value
              .set(AgentInternationalAddressPage, InternationalAddress("line1","line2",None, "IN")).success.value
              .set(AgentTelephoneNumberPage, "+1234567890").success.value
              .set(AgentInternalReferencePage, "1234-5678").success.value
              .set(AgentAddressUKYesNoPage, false).success.value

          agentMapper.build(userAnswers).value mustBe AgentDetails(
            arn = "SARN123456",
            agentName = "Agency Name",
            agentAddress = InternationalAddress("line1","line2",None, "IN"),
            agentTelephoneNumber = "+1234567890",
            clientReference = "1234-5678"
          )
        }

        "must not be able to create AgentDetails when no address available." in {
          val userAnswers =
            emptyUserAnswers
              .set(AgentARNPage, "SARN123456").success.value
              .set(AgentNamePage, "Agency Name").success.value
              .set(AgentInternalReferencePage, "1234-5678").success.value
              .set(AgentAddressUKYesNoPage, true).success.value
          agentMapper.build(userAnswers) mustNot be(defined)
        }

        "must not be able to create AgentDetails when no only agent name available." in {
          val userAnswers =
            emptyUserAnswers
              .set(AgentARNPage, "SARN123456").success.value
              .set(AgentNamePage, "Agency Name").success.value
          agentMapper.build(userAnswers) mustNot be(defined)
        }
      }
  }
}
