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

package repositories

import mapping.AgentDetailsMapper
import models._
import pages.AgentNamePage
import play.api.i18n.Messages
import play.api.libs.json.{JsNull, JsValue, Json}
import print.AgentDetailsPrintHelper
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class SubmissionSetFactory @Inject()(agentMapper: AgentDetailsMapper,
                                     printHelper: AgentDetailsPrintHelper) {

  def createFrom(userAnswers: UserAnswers)(implicit messages: Messages): RegistrationSubmission.DataSet = {
    val status = Some(Status.Completed) // TODO Do we need to evaluate this at runtime based on UserAnswers?

    RegistrationSubmission.DataSet(
      data = Json.toJson(userAnswers),
      status = status,
      registrationPieces = mappedDataIfCompleted(userAnswers, status),
      answerSections = answerSectionsIfCompleted(userAnswers, status)
    )
  }

  private def mappedPieces(agentJson: JsValue) =
    List(RegistrationSubmission.MappedPiece("agentDetails", agentJson))

  private def mappedDataIfCompleted(userAnswers: UserAnswers, status: Option[Status]): List[RegistrationSubmission.MappedPiece] = {
    if (status.contains(Status.Completed)) {
      agentMapper.build(userAnswers) match {
        case Some(agent) =>
          mappedPieces(Json.toJson(agent))
        case _ =>
          mappedPieces(JsNull)
      }
    } else {
      mappedPieces(JsNull)
    }
  }

  def answerSectionsIfCompleted(userAnswers: UserAnswers, status: Option[Status])
                               (implicit messages: Messages): List[RegistrationSubmission.AnswerSection] = {

    if (status.contains(Status.Completed)) {

      val name = userAnswers.get(AgentNamePage).getOrElse("")

      val section = printHelper.printSection(
        userAnswers,
        name,
        userAnswers.draftId
      )

      convertForSubmission(section) :: Nil

    } else {
      List.empty
    }
  }

  private def convertForSubmission(section: AnswerSection): RegistrationSubmission.AnswerSection = {
    RegistrationSubmission.AnswerSection(
      headingKey = section.headingKey,
      rows = section.rows.map(convertForSubmission),
      sectionKey = section.sectionKey
    )
  }

  private def convertForSubmission(row: AnswerRow): RegistrationSubmission.AnswerRow = {
    RegistrationSubmission.AnswerRow(
      label = row.label,
      answer = row.answer.toString,
      labelArg = row.labelArg
    )
  }

}
