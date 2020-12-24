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

package repositories

import java.time.LocalDateTime

import base.RegistrationSpecBase
import connector.SubmissionDraftConnector
import models._
import models.core.http.{AddressType, IdentificationOrgType, LeadTrusteeOrgType, LeadTrusteeType}
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsArray, Json}
import play.api.test.Helpers.OK
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.DateFormatter
import viewmodels.{AnswerRow, AnswerSection, DraftRegistration}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RegistrationRepositorySpec extends RegistrationSpecBase with MustMatchers with MockitoSugar {

  private val userAnswersDateTime = LocalDateTime.of(2020, 2, 24, 13, 34, 0)

  private def createRepository(mockConnector: SubmissionDraftConnector) = {
    val mockDateFormatter: DateFormatter = mock[DateFormatter]
    when(mockDateFormatter.savedUntil(any())(any())).thenReturn("4 February 2012")

    new DefaultRegistrationsRepository(mockDateFormatter, mockConnector)
  }

  "RegistrationRepository" when {
    "getting user answers" must {
      "read answers from main section" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val userAnswers = models.UserAnswers(draftId = draftId, internalAuthId = "internalAuthId")

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val response = SubmissionDraftResponse(LocalDateTime.now, Json.toJson(userAnswers), None)

        when(mockConnector.getDraftMain(any())(any(), any())).thenReturn(Future.successful(response))

        val result = Await.result(repository.get(draftId), Duration.Inf)

        result mustBe Some(userAnswers)
        verify(mockConnector).getDraftMain(draftId)(hc, executionContext)
      }
    }

    "getting most recent draft id" must {
      "return the first draft's id received from connector" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val drafts = List(
          SubmissionDraftId(
            "draft1",
            LocalDateTime.of(2012, 2, 1, 12, 30, 0),
            Some("reference1")
          ),
          SubmissionDraftId(
            "draft2",
            LocalDateTime.of(2011, 1, 2, 9, 42, 0),
            Some("reference2")
          )
        )

        when(mockConnector.getCurrentDraftIds()(any(), any())).thenReturn(Future.successful(drafts))

        val result = Await.result(repository.getMostRecentDraftId(), Duration.Inf)

        result mustBe Some("draft1")
        verify(mockConnector).getCurrentDraftIds()(hc, executionContext)
      }
    }

    "listing drafts" must {
      "return the drafts received from connector" in {

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val drafts = List(
          SubmissionDraftId(
            "draft1",
            LocalDateTime.of(2012, 2, 1, 12, 30, 0),
            Some("reference1")
          ),
          SubmissionDraftId(
            "draft2",
            LocalDateTime.of(2012, 2, 1, 12, 30, 0),
            Some("reference2")
          )
        )

        when(mockConnector.getCurrentDraftIds()(any(), any())).thenReturn(Future.successful(drafts))

        val result = Await.result(repository.listDrafts()(any(), any()), Duration.Inf)

        result mustBe List(
          DraftRegistration("draft1", "reference1", "4 February 2012"),
          DraftRegistration("draft2", "reference2", "4 February 2012")
        )
      }
    }

    "setting user answers" must {
      "write answers to main section" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val userAnswers = models.UserAnswers(draftId = draftId, internalAuthId = "internalAuthId")

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        when(mockConnector.setDraftMain(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

        val result = Await.result(repository.set(userAnswers), Duration.Inf)

        result mustBe true
        verify(mockConnector).setDraftMain(draftId, Json.toJson(userAnswers), None)(hc, executionContext)
      }
    }

    "adding a registration section" must {

      "combine into empty sections" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val registrationSectionsData = Json.obj(
          "field/subfield" -> Json.obj(
            "dataField"-> "newData"
            ),
          "field/subfield2" -> JsArray(
            Seq(
              Json.obj("subSubField2"-> "newData")
            )
          )
        )

        when(mockConnector.getRegistrationPieces(any())(any(), any())).thenReturn(Future.successful(registrationSectionsData))

        val currentRegistrationJson = Json.parse(
          """
            |{
            | "existingObject": {
            |   "existingField": "existingValue"
            | },
            | "field" : {
            |   "subfield": {
            |     "otherDataField": "otherData"
            |   },
            |   "subfield2": []
            | }
            |}
            |""".stripMargin)


        val result = Await.result(repository.addDraftRegistrationSections(draftId, currentRegistrationJson), Duration.Inf)

        val expectedCombinedRegistrationJson = Json.parse(
          """
            |{
            | "existingObject": {
            |   "existingField": "existingValue"
            | },
            | "field" : {
            |   "subfield": {
            |     "dataField": "newData",
            |     "otherDataField": "otherData"
            |   },
            |   "subfield2": [
            |     {
            |       "subSubField2": "newData"
            |     }
            |   ]
            | }
            |}
            |""".stripMargin)

        result mustBe expectedCombinedRegistrationJson
        verify(mockConnector).getRegistrationPieces(draftId)(hc, executionContext)
      }
    }

    "getting draft" must {

      val drafts = List(
        SubmissionDraftId(
          "draft1",
          LocalDateTime.of(2012, 2, 1, 12, 30, 0),
          Some("reference1")
        ),
        SubmissionDraftId(
          "draft2",
          LocalDateTime.of(2012, 2, 1, 12, 30, 0),
          Some("reference2")
        )
      )

      val mockConnector = mock[SubmissionDraftConnector]
      val repository = createRepository(mockConnector)
      when(mockConnector.getCurrentDraftIds()(any(), any())).thenReturn(Future.successful(drafts))

      "return draft from list of current drafts if it exists" in {

        val result1 = Await.result(repository.getDraft("draft1")(any(), any()), Duration.Inf)
        result1 mustBe Some(DraftRegistration("draft1", "reference1", "4 February 2012"))

        val result2 = Await.result(repository.getDraft("draft2")(any(), any()), Duration.Inf)
        result2 mustBe Some(DraftRegistration("draft2", "reference2", "4 February 2012"))
      }

      "return None if draft is not found" in {

        val result1 = Await.result(repository.getDraft("draft3")(any(), any()), Duration.Inf)
        result1 mustBe None
      }
    }

    "removing draft" must {
      "get response from connector" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val draftId: String = "draftId"

        val status: Int = 200

        when(mockConnector.removeDraft(any())(any(), any())).thenReturn(Future.successful(HttpResponse(status, "")))

        val result = Await.result(repository.removeDraft(draftId), Duration.Inf)

        result.status mustBe status

        verify(mockConnector).removeDraft(draftId)(hc, executionContext)
      }
    }
  }
}
