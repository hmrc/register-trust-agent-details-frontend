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

import connector.SubmissionDraftConnector
import javax.inject.Inject
import models.UserAnswers
import pages.agent.AgentInternalReferencePage
import play.api.http
import play.api.i18n.Messages
import play.api.libs.json._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.DateFormatter
import viewmodels.DraftRegistration

import scala.concurrent.{ExecutionContext, Future}

class DefaultRegistrationsRepository @Inject()(dateFormatter: DateFormatter,
                                               submissionDraftConnector: SubmissionDraftConnector
                                              )(implicit ec: ExecutionContext) extends RegistrationsRepository {

  override def get(draftId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = {
    submissionDraftConnector.getDraftMain(draftId).map {
      response => Some(response.data.as[UserAnswers])
    }
  }

  override def getMostRecentDraftId()(implicit hc: HeaderCarrier) : Future[Option[String]] = {
    submissionDraftConnector.getCurrentDraftIds().map(_.headOption.map(_.draftId))
  }

  override def listDrafts()(implicit hc: HeaderCarrier, messages: Messages): Future[List[DraftRegistration]] = {
    submissionDraftConnector.getCurrentDraftIds().map {
      draftIds =>
        draftIds.flatMap {
          x => x.reference.map {
            reference => DraftRegistration(x.draftId, reference, dateFormatter.savedUntil(x.createdAt))
          }
        }
    }
  }

  override def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    submissionDraftConnector.setDraftMain(
      draftId = userAnswers.draftId,
      draftData = Json.toJson(userAnswers),
      reference = userAnswers.get(AgentInternalReferencePage)
    ).map {
      response => response.status == http.Status.OK
    }
  }

  private def decodePath(encodedPath: String): JsPath =
    encodedPath.split('/').foldLeft[JsPath](
      JsPath
    )(
      (cur: JsPath, component: String) => cur \ component
    )

  private def addSection(key: String, section: JsValue, data: JsValue): JsResult[JsValue] = {
    val path = decodePath(key).json
    val transform = __.json.update(path.put(section))

    data.transform(transform)
  }

  override def addDraftRegistrationSections(draftId: String, registrationJson: JsValue)(implicit hc: HeaderCarrier) : Future[JsValue] = {
    submissionDraftConnector.getRegistrationPieces(draftId).map {
      pieces =>
        val added: JsResult[JsValue] = pieces.keys.foldLeft[JsResult[JsValue]](
          JsSuccess(registrationJson)
        )(
          (cur, key) => cur.flatMap(addSection(key, pieces(key), _))
        )

        added match {
          case JsSuccess(value, _) => value
          case _ => registrationJson
        }
    }
  }

  override def getDraft(draftId: String)(implicit headerCarrier: HeaderCarrier, messages: Messages): Future[Option[DraftRegistration]] =
    listDrafts().map {
      drafts =>
        drafts.find(_.draftId == draftId)
    }

  override def removeDraft(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submissionDraftConnector.removeDraft(draftId)
}

trait RegistrationsRepository {
  def get(draftId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean]

  def listDrafts()(implicit hc: HeaderCarrier, messages: Messages): Future[List[DraftRegistration]]

  def getMostRecentDraftId()(implicit hc: HeaderCarrier) : Future[Option[String]]

  def addDraftRegistrationSections(draftId: String, registrationJson: JsValue)(implicit hc: HeaderCarrier) : Future[JsValue]

  def getDraft(draftId: String)(implicit hc: HeaderCarrier, messages: Messages): Future[Option[DraftRegistration]]

  def removeDraft(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse]
}
