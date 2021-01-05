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

package print

import java.time.LocalDate

import com.google.inject.Inject
import models.{Address, FullName, UserAnswers}
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.HtmlFormat
import print.CheckAnswersFormatters.{addressFormatter, dateFormatter, yesOrNo}
import queries.Gettable
import utils.countryOptions.CountryOptions
import viewmodels.AnswerRow

class AnswerRowConverter @Inject()() {

  def bind(userAnswers: UserAnswers, name: String, countryOptions: CountryOptions, canEdit: Boolean)
          (implicit messages: Messages): Bound = new Bound(userAnswers, name, countryOptions, canEdit)

  class Bound(userAnswers: UserAnswers, name: String, countryOptions: CountryOptions, canEdit: Boolean)
             (implicit messages: Messages) {

    def nameQuestion(query: Gettable[FullName],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          HtmlFormat.escape(x.toString),
          Some(changeUrl),
          canEdit = canEdit
        )
      }
    }

    def stringQuestion(query: Gettable[String],
                       labelKey: String,
                       changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          HtmlFormat.escape(x),
          Some(changeUrl),
          name,
          canEdit = canEdit
        )
      }
    }

    def yesNoQuestion(query: Gettable[Boolean],
                      labelKey: String,
                      changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          yesOrNo(x),
          Some(changeUrl),
          name,
          canEdit = canEdit
        )
      }
    }

    def dateQuestion(query: Gettable[LocalDate],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          HtmlFormat.escape(x.format(dateFormatter)),
          Some(changeUrl),
          name,
          canEdit = canEdit
        )
      }
    }
    def addressQuestion[T <: Address](query: Gettable[T],
                                      labelKey: String,
                                      changeUrl: String)
                                     (implicit reads: Reads[T]): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          addressFormatter(x, countryOptions),
          Some(changeUrl),
          name,
          canEdit = canEdit
        )
      }
    }

  }
}
