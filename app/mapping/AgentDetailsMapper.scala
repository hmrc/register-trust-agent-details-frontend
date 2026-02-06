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

package mapping

import _root_.models._
import mapping.models.AgentDetails
import pages._
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsError, JsSuccess, Reads}

class AgentDetailsMapper {

  private def readAddress: Reads[Address] =
    AgentAddressUKYesNoPage.path.read[Boolean].flatMap[Address] {
      case true  => AgentUKAddressPage.path.read[UKAddress].widen[Address]
      case false => AgentInternationalAddressPage.path.read[InternationalAddress].widen[Address]
    }

  def build(answers: UserAnswers): Option[AgentDetails] = {
    lazy val readFromUserAnswers: Reads[AgentDetails] =
      (
        AgentARNPage.path.read[String] and
          AgentNamePage.path.read[String] and
          readAddress and
          AgentTelephoneNumberPage.path.read[String] and
          AgentInternalReferencePage.path.read[String]
      )(AgentDetails.apply _)

    answers.data.validate[AgentDetails](readFromUserAnswers) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(_)          =>
        None
    }
  }

}
