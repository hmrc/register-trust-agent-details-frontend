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

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen
import org.scalacheck.Arbitrary.arbitrary

class UKAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new UKAddressFormProvider()()

  ".line1" must {

    val fieldName = "line1"
    val requiredKey = "ukAddress.error.line1.required"
    val lengthKey = "ukAddress.error.line1.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.addressLineRegex)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )

  }

  ".line2" must {

    val fieldName = "line2"
    val requiredKey = "ukAddress.error.line2.required"
    val lengthKey = "ukAddress.error.line2.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.addressLineRegex)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )

  }

  ".line3" must {

    val fieldName = "line3"
    val lengthKey = "ukAddress.error.line3.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.addressLineRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "  line3  ", "line4" -> "line4", "postcode" -> "AB12CD"))
<<<<<<< HEAD
      result.value.value.line3 mustBe Some("line3")
=======
      result.value.value.line3 shouldBe Some("line3")
>>>>>>> 65a7454 ([DDCE-3247] Added tests for whitespace changes)
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "  ", "line4" -> "line4", "postcode" -> "AB12CD"))
<<<<<<< HEAD
      result.value.value.line3 mustBe None
=======
      result.value.value.line3 shouldBe None
>>>>>>> 65a7454 ([DDCE-3247] Added tests for whitespace changes)
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "", "line4" -> "line4", "postcode" -> "AB12CD"))
<<<<<<< HEAD
      result.value.value.line3 mustBe None
=======
      result.value.value.line3 shouldBe None
>>>>>>> 65a7454 ([DDCE-3247] Added tests for whitespace changes)
    }
  }

  ".line4" must {

    val fieldName = "line4"
    val lengthKey = "ukAddress.error.line4.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.addressLineRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "line3", "line4" -> "  line4  ", "postcode" -> "AB12CD"))
<<<<<<< HEAD
      result.value.value.line4 mustBe Some("line4")
=======
      result.value.value.line4 shouldBe Some("line4")
>>>>>>> 65a7454 ([DDCE-3247] Added tests for whitespace changes)
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "line3", "line4" -> "  ", "postcode" -> "AB12CD"))
<<<<<<< HEAD
      result.value.value.line4 mustBe None
=======
      result.value.value.line4 shouldBe None
>>>>>>> 65a7454 ([DDCE-3247] Added tests for whitespace changes)
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "line3", "line4" -> "", "postcode" -> "AB12CD"))
<<<<<<< HEAD
      result.value.value.line4 mustBe None
=======
      result.value.value.line4 shouldBe None
>>>>>>> 65a7454 ([DDCE-3247] Added tests for whitespace changes)
    }
  }

  ".postcode" must {

    val fieldName = "postcode"
    val requiredKey = "ukAddress.error.postcode.required"
    val invalidKey = "ukAddress.error.postcode.invalidCharacters"

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = Validation.postcodeRegex,
      generator = arbitrary[String],
      error = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, invalidKey)
    )

  }

}
