/*
 * Copyright 2022 HM Revenue & Customs
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

import base.SpecBase
import play.api.data.FormError
import utils.DateErrorFormatter.addErrorClass

class DateErrorFormatterSpec extends SpecBase {

  "addErrorClass" must {
    "return an error class when given an error that matches the dateArg" in {

      val result = addErrorClass(Some(FormError("key", "message", Seq("dateArg"))), "dateArg")

      result mustEqual "govuk-input--error"
    }

    "return an error class when error args are empty" in {

      val result = addErrorClass(Some(FormError("key", "dateMessage", Nil)), "dateArg")

      result mustEqual "govuk-input--error"
    }

    "return an empty string when given an error that does not match the dateArg" in {

      val result = addErrorClass(Some(FormError("key", "message", Seq("SomeOtherArg"))), "dateArg")

      result mustEqual ""
    }

    "return an empty string when error is not defined" in {

      val result = addErrorClass(None, "dateArg")

      result mustEqual ""
    }
  }
}
