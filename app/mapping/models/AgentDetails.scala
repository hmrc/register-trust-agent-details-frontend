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

package mapping.models

import models.Address
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AgentDetails(arn: String,
                        agentName: String,
                        agentAddress: Address,
                        agentTelephoneNumber: String,
                        clientReference: String)

object AgentDetails {
  implicit val agentDetailsFormat: Format[AgentDetails] = Json.format[AgentDetails]

  implicit val reads: Reads[AgentDetails] =
    ((__ \ Symbol("arn")).read[String] and
      (__ \ Symbol("agentName")).read[String] and
      (__ \ Symbol("agentAddress")).read[Address] and
      (__ \ Symbol("agentTelephoneNumber")).read[String] and
      (__ \ Symbol("clientReference")).read[String]).tupled.map{

      case (arn, name, address, phoneNumber, clientRef) =>
        AgentDetails(arn, name, address, phoneNumber, clientRef)
    }

  implicit val writes: Writes[AgentDetails] =
    ((__ \ Symbol("arn")).write[String] and
      (__ \ Symbol("agentName")).write[String] and
      (__ \ Symbol("agentAddress")).write[Address] and
      (__ \ Symbol("agentTelephoneNumber")).write[String] and
      (__ \ Symbol("clientReference")).write[String]
      ).apply(unlift(AgentDetails.unapply))

}
