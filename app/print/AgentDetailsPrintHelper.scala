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

package print

import com.google.inject.Inject
import controllers.routes
import models.UserAnswers
import pages._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class AgentDetailsPrintHelper @Inject() (answerRowConverter: AnswerRowConverter) {

  def printSection(userAnswers: UserAnswers, name: String, draftId: String)(implicit
    messages: Messages
  ): AnswerSection =
    AnswerSection(
      rows = answers(userAnswers, name, draftId, canEdit = false),
      sectionKey = Some("answerPage.section.agent.heading")
    )

  def checkDetailsSection(userAnswers: UserAnswers, name: String, draftId: String)(implicit
    messages: Messages
  ): AnswerSection =
    AnswerSection(
      rows = answers(userAnswers, name, draftId, canEdit = true)
    )

  def answers(userAnswers: UserAnswers, name: String, draftId: String, canEdit: Boolean)(implicit
    messages: Messages
  ): Seq[AnswerRow] = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, canEdit = canEdit)

    Seq(
      bound.stringQuestion(
        AgentInternalReferencePage,
        "agentInternalReference",
        routes.AgentInternalReferenceController.onPageLoad(draftId).url
      ),
      bound.stringQuestion(AgentNamePage, "agentName", routes.AgentNameController.onPageLoad(draftId).url),
      bound.yesNoQuestion(
        AgentAddressUKYesNoPage,
        "agentAddressUKYesNo",
        routes.AgentAddressYesNoController.onPageLoad(draftId).url
      ),
      bound.addressQuestion(
        AgentUKAddressPage,
        "site.address.uk",
        routes.AgentUKAddressController.onPageLoad(draftId).url
      ),
      bound.addressQuestion(
        AgentInternationalAddressPage,
        "site.address.international",
        routes.AgentInternationalAddressController.onPageLoad(draftId).url
      ),
      bound.stringQuestion(
        AgentTelephoneNumberPage,
        "agentTelephoneNumber",
        routes.AgentTelephoneNumberController.onPageLoad(draftId).url
      )
    ).flatten
  }

}
