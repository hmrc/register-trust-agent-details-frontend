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

package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.language.implicitConversions

final case class UKAddress(
                            line1: String,
                            line2: String,
                            line3: Option[String] = None,
                            line4: Option[String] = None,
                            postCode: String
                          ) extends Address

object UKAddress {

  implicit lazy val formats: Format[UKAddress] = Format.apply(reads, writes)

  implicit lazy val reads: Reads[UKAddress] = {
    (
      (__ \ "line1").read[String] and
        (__ \ "line2").read[String] and
        (__ \ "line3").readNullable[String] and
        (__ \ "line4").readNullable[String] and
        (__ \ "postCode").read[String]
      ) (UKAddress.apply _)
  }

  implicit lazy val writes: Writes[UKAddress] = {
    (
      (__ \ 'line1).write[String] and
        (__ \ 'line2).write[String] and
        (__ \ 'line3).writeNullable[String] and
        (__ \ 'line4).writeNullable[String] and
        (__ \ 'postCode).write[String] and
        (__ \ 'country).write[String]
      ).apply(address => (
      address.line1,
      address.line2,
      address.line3,
      address.line4,
      address.postCode,
      "GB"
    ))
  }

}

final case class InternationalAddress(
                                       line1: String,
                                       line2: String,
                                       line3: Option[String] = None,
                                       country: String
                                     ) extends Address

object InternationalAddress {

  implicit lazy val formats: OFormat[InternationalAddress] = Json.format[InternationalAddress]
}

sealed trait Address

object Address {

  implicit lazy val reads: Reads[Address] = {

    implicit class ReadsWithContravariantOr[A](a: Reads[A]) {

      def or[B >: A](b: Reads[B]): Reads[B] = {
        a.map[B](identity).orElse(b)
      }
    }

    implicit def convertToSupertype[A, B >: A](a: Reads[A]): Reads[B] =
      a.map(identity)

      UKAddress.formats or
      InternationalAddress.formats
  }

  implicit lazy val writes: Writes[Address] = Writes {
    case address: UKAddress            => Json.toJson(address)(UKAddress.formats)
    case address: InternationalAddress => Json.toJson(address)(InternationalAddress.formats)
  }
}
