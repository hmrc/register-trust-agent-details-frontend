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

package models

import base.SpecBase
import play.api.libs.json.Json

class AddressSpec extends SpecBase {

  "Address" when {

    val addressLine1 = "Line 1"
    val addressLine2 = "Line 2"
    val addressLine3 = "Line 3"

    "UK address" must {

      val addressLine4 = "Line 4"
      val postCode = "AB1 1AB"

      val address = UKAddress(addressLine1, addressLine2, Some(addressLine3), Some(addressLine4), postCode)

      val addressJson = Json.parse(
        s"""
          |{
          |  "line1": "$addressLine1",
          |  "line2": "$addressLine2",
          |  "line3": "$addressLine3",
          |  "line4": "$addressLine4",
          |  "postCode": "$postCode",
          |  "country": "GB"
          |}
          |""".stripMargin
      )

      "write to json" in {

        val result = Json.toJson(address)

        result mustBe addressJson
      }

      "read from json" in {

        val result = addressJson.as[UKAddress]

        result mustBe address
      }
    }

    "international address" must {

      val country = "FR"

      val address = InternationalAddress(addressLine1, addressLine2, Some(addressLine3), country)

      val addressJson = Json.parse(
        s"""
           |{
           |  "line1": "$addressLine1",
           |  "line2": "$addressLine2",
           |  "line3": "$addressLine3",
           |  "country": "$country"
           |}
           |""".stripMargin
      )

      "write to json" in {

        val result = Json.toJson(address)

        result mustBe addressJson
      }

      "read from json" in {

        val result = addressJson.as[InternationalAddress]

        result mustBe address
      }
    }
  }
}
