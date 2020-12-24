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

package config

import java.net.{URI, URLEncoder}
import java.time.LocalDate

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.{Lang, Messages}
import play.api.mvc.{Call, Request}

@Singleton
class FrontendAppConfig @Inject()(val configuration: Configuration) {

  private final val ENGLISH = "en"
  private final val WELSH = "cy"

  private val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "trusts"

  lazy val serviceName: String = configuration.get[String]("serviceName")

  private def loadConfig(key: String) = configuration.get[String](key)

  val analyticsToken: String = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String = configuration.get[String](s"google-analytics.host")

  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  val betaFeedbackUrl = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  val whoShouldRegisterUrl: String = configuration.get[String]("urls.whoShouldRegister")
  val trustsAndTaxesUrl: String = configuration.get[String]("urls.trustsAndTaxes")
  val trustsHelplineUrl: String = configuration.get[String]("urls.trustsHelpline")
  val ggSignInUrl: String = configuration.get[String]("urls.ggSignIn")

  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val lostUtrUrl : String = configuration.get[String]("urls.lostUtr")
  lazy val logoutUrl: String = loadConfig("urls.logout")

  lazy val registrationProgressUrlTemplate: String =
    configuration.get[String]("urls.registrationProgress")

  lazy val registrationStartUrl: String = configuration.get[String]("urls.registrationStart")

  lazy val registrationProgress: String = configuration.get[String]("urls.registrationProgress")

  def registrationProgressUrl(draftId: String): String =
    registrationProgressUrlTemplate.replace(":draftId", draftId)

  def routeToSwitchLanguage: String => Call =
    (lang: String) => controllers.routes.LanguageSwitchController.switchToLanguage(lang)


  lazy val otacUrl : String = configuration.get[String]("urls.otacLogin")

  lazy val agentsSubscriptionsUrl : String = configuration.get[String]("urls.agentSubscriptions")
  lazy val agentServiceRegistrationUrl = s"$agentsSubscriptionsUrl?continue=$loginContinueUrl"

  lazy val locationCanonicalList: String = loadConfig("location.canonical.list.all")
  lazy val locationCanonicalListNonUK: String = loadConfig("location.canonical.list.nonUK")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val ttlInSeconds = configuration.get[Int]("mongodb.registration.ttlSeconds")

  lazy val trustsUrl = configuration.get[Service]("microservice.services.trusts").baseUrl

  lazy val authUrl = configuration.get[Service]("microservice.services.auth").baseUrl

  lazy val trustsStoreUrl: String = configuration.get[Service]("microservice.services.trusts-store").baseUrl + "/trusts-store"

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang(ENGLISH),
    "cymraeg" -> Lang(WELSH)
  )

  lazy val auditSubmissions : Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.submissions.enabled")

  lazy val auditCannotCreateRegistration : Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.cannotCreateRegistration.enabled")

  lazy val maintainATrustFrontendUrl : String =
    configuration.get[String]("urls.maintainATrust")

  lazy val countdownLength: String = configuration.get[String]("timeout.countdown")
  lazy val timeoutLength: String = configuration.get[String]("timeout.length")

  private val day: Int = configuration.get[Int]("minimumDate.day")
  private val month: Int = configuration.get[Int]("minimumDate.month")
  private val year: Int = configuration.get[Int]("minimumDate.year")
  lazy val minDate: LocalDate = LocalDate.of(year, month, day)

  lazy val assetValueUpperLimitExclusive: Long = configuration.get[Long]("assetValueUpperLimitExclusive")

  private lazy val accessibilityBaseLinkUrl: String = configuration.get[String]("urls.accessibility")

  def accessibilityLinkUrl(implicit request: Request[_]): String = {
    val userAction = URLEncoder.encode(new URI(request.uri).getPath, "UTF-8")
    s"$accessibilityBaseLinkUrl?userAction=$userAction"
  }

  def helplineUrl(implicit messages: Messages): String = {
    val path = messages.lang.code match {
      case WELSH => "urls.welshHelpline"
      case _ => "urls.trustsHelpline"
    }

    configuration.get[String](path)
  }
}
