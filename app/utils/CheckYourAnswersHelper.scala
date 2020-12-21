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

package utils

import javax.inject.Inject
import models.{NormalMode, UserAnswers}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions, dateFormatter: DateFormatter)
                                      (userAnswers: UserAnswers, draftId: String, canEdit: Boolean)
                                      (implicit messages: Messages) {


  def assetInternationalAddress(index: Int): Option[AnswerRow] = userAnswers.get(BusinessInternationalAddressPage(index)) map {
    x =>
      AnswerRow(
        "assetInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.register.asset.business.routes.BusinessInternationalAddressController.onPageLoad(NormalMode, index, draftId).url),
        assetName(index, userAnswers),
        canEdit = canEdit
      )
  }




  def agentInternationalAddress: Option[AnswerRow] = userAnswers.get(AgentInternationalAddressPage) map {
    x =>
      AnswerRow(
        "site.address.international.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.register.agents.routes.AgentInternationalAddressController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }



  def agentUKAddress: Option[AnswerRow] = userAnswers.get(AgentUKAddressPage) map {
    x =>
      AnswerRow(
        "site.address.uk.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.agents.routes.AgentUKAddressController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def agentAddressYesNo: Option[AnswerRow] = userAnswers.get(AgentAddressYesNoPage) map {
    x =>
      AnswerRow(
        "agentAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.agents.routes.AgentAddressYesNoController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def agentName: Option[AnswerRow] = userAnswers.get(AgentNamePage) map {
    x =>
      AnswerRow(
        "agentName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.agents.routes.AgentNameController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }



  def agentInternalReference: Option[AnswerRow] = userAnswers.get(AgentInternalReferencePage) map {
    x =>
      AnswerRow(
        "agentInternalReference.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.agents.routes.AgentInternalReferenceController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def agenciesTelephoneNumber: Option[AnswerRow] = userAnswers.get(AgentTelephoneNumberPage) map {
    x =>
      AnswerRow(
        "agentTelephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.agents.routes.AgentTelephoneNumberController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

}