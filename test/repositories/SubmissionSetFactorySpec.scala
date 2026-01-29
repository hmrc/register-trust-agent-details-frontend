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

package repositories

import base.SpecBase
import models.RegistrationSubmission.{AnswerRow, AnswerSection, MappedPiece}
import models._
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.libs.json.{JsNull, JsValue, Json}
import utils.TestUserAnswers.draftId

class SubmissionSetFactorySpec extends SpecBase with MockitoSugar {

  val submissionSetFactory: SubmissionSetFactory = injector.instanceOf[SubmissionSetFactory]

  "SubmissionSetFactory" should {

    "create the expected dataset if no agent details are given" in {

      val result: RegistrationSubmission.DataSet =
        submissionSetFactory.createFrom(UserAnswers(draftId = draftId, internalAuthId = "internalAuthId"))

      val expectedData: JsValue = Json.parse(
        """
          |{"_id":"id","data":{},"internalId":"internalAuthId"}
          |""".stripMargin
      )

      val expected = RegistrationSubmission.DataSet(
        expectedData,
        List(MappedPiece("agentDetails", JsNull)),
        List(AnswerSection(None, List(), Some("answerPage.section.agent.heading")))
      )

      result mustBe expected
    }

    "create the expected dataset when agent details are given" in {

      val result: RegistrationSubmission.DataSet =
        submissionSetFactory.createFrom(
          UserAnswers(draftId = draftId, data = Json.obj(), internalAuthId = "internalAuthId")
            .set(AgentARNPage, "SARN123456")
            .success
            .value
            .set(AgentNamePage, "Agency Name")
            .success
            .value
            .set(AgentUKAddressPage, UKAddress("71 North Road", "Magical", Some("Earth"), Some("Land"), "SK137AX"))
            .success
            .value
            .set(AgentTelephoneNumberPage, "12345668984")
            .success
            .value
            .set(AgentInternalReferencePage, "testInternalRef")
            .success
            .value
            .set(AgentAddressUKYesNoPage, true)
            .success
            .value
        )

      val expectedAddress =
        """
          |{"line1": "71 North Road", "line2": "Magical", "line3": "Earth", "line4": "Land", "postCode": "SK137AX","country": "GB"}
          |""".stripMargin

      val expectedDatasetData: JsValue = Json.parse(
        s"""
           |{
           |  "_id": "id",
           |  "data": {
           |    "agent": {
           |      "internalReference": "testInternalRef", "addressUKYesNo": true,
           |      "telephoneNumber": "12345668984", "ukAddress": $expectedAddress,
           |      "name": "Agency Name", "agentARN": "SARN123456"
           |    }
           |  },
           |  "internalId": "internalAuthId"
           |}
           |""".stripMargin
      )

      val agentDetailsJson = Json.parse(
        s"""
           |{
           |  "arn": "SARN123456", "agentName": "Agency Name",
           |  "agentAddress": $expectedAddress, "agentTelephoneNumber": "12345668984",
           |  "clientReference": "testInternalRef"
           |}
           |""".stripMargin
      )

      val expected = RegistrationSubmission.DataSet(
        expectedDatasetData,
        List(MappedPiece("agentDetails", agentDetailsJson)),
        List(
          AnswerSection(
            None,
            List(
              AnswerRow("agentInternalReference.checkYourAnswersLabel", "testInternalRef", "Agency Name"),
              AnswerRow("agentName.checkYourAnswersLabel", "Agency Name", "Agency Name"),
              AnswerRow("agentAddressUKYesNo.checkYourAnswersLabel", "Yes", "Agency Name"),
              AnswerRow(
                "site.address.uk.checkYourAnswersLabel",
                "71 North Road<br />Magical<br />Earth<br />Land<br />SK137AX",
                "Agency Name"
              ),
              AnswerRow("agentTelephoneNumber.checkYourAnswersLabel", "12345668984", "Agency Name")
            ),
            Some("answerPage.section.agent.heading")
          )
        )
      )

      result mustBe expected
    }
  }

}
