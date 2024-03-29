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

package forms.mappings

import base.SpecBase
import models.Enumerable
import org.scalatest.OptionValues
import play.api.data.{Form, FormError}

object MappingsSpec {

  sealed trait Foo
  case object Bar extends Foo
  case object Baz extends Foo

  object Foo {

    val values: Set[Foo] = Set(Bar, Baz)

    implicit val fooEnumerable: Enumerable[Foo] =
      Enumerable(values.toSeq.map(v => v.toString -> v): _*)
  }
}

class MappingsSpec extends SpecBase with OptionValues with Mappings {

  "text" must {

    val testForm: Form[String] =
      Form(
        "value" -> text("error.required")
      )

    "bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foobar"))
      result.get mustEqual "foobar"
    }

    "filter out smart apostrophes on binding" in {
      val boundValue = testForm bind Map("value" -> "We’re ‘aving fish ‘n’ chips for tea")
      boundValue.get mustEqual "We're 'aving fish 'n' chips for tea"
    }

    "bind and trim spaces" in {
      val result = testForm.bind(Map("value" -> "   foobar     "))
      result.get mustEqual "foobar"
    }

    "not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "return a custom error message" in {
      val form = Form("value" -> text("custom.error"))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "unbind a valid value" in {
      val result = testForm.fill("foobar")
      result.apply("value").value.value mustEqual "foobar"
    }
  }

  "postcode" must {

    val testForm: Form[String] =
      Form(
        "value" -> postcode()
      )

    val validPostcodes = Seq(
      "AA9A 9AA",
      "A9A 9AA",
      "A9 9AA",
      "A99 9AA",
      "AA9 9AA",
      "AA99 9AA"
    )

    validPostcodes.foreach {
      p =>

        s"bind a valid postcode $p" in {
          val result = testForm.bind(Map("value" -> p))
          result.get mustEqual p
        }

    }

    "not bind an invalid postcode due to format" in {
      val result = testForm.bind(Map("value" -> "AA1 1A"))
      result.errors must contain(FormError("value", "ukAddress.error.postcode.invalidCharacters"))
    }

    "not bind an invalid postcode due to too many spaces" in {
      val result = testForm.bind(Map("value" -> "AA1  1AA"))
      result.errors must contain(FormError("value", "ukAddress.error.postcode.invalidCharacters"))
    }
  }

  "boolean" must {

    val testForm: Form[Boolean] =
      Form(
        "value" -> boolean("error.required")
      )

    "bind true" in {
      val result = testForm.bind(Map("value" -> "true"))
      result.get mustEqual true
    }

    "bind false" in {
      val result = testForm.bind(Map("value" -> "false"))
      result.get mustEqual false
    }

    "not bind a non-boolean" in {
      val result = testForm.bind(Map("value" -> "not a boolean"))
      result.errors must contain(FormError("value", "error.boolean"))
    }

    "not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "unbind" in {
      val result = testForm.fill(true)
      result.apply("value").value.value mustEqual "true"
    }
  }
}
