import play.core.PlayVersion
import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-27" % "3.2.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "0.32.0-play-27",
    "uk.gov.hmrc"             %% "play-frontend-govuk"        % "0.56.0-play-27",
    "uk.gov.hmrc"             %% "play-language"              % "4.5.0-play-27",
    "uk.gov.hmrc"             %% "domain"                     % "5.10.0-play-27"
  )

  val test: Seq[ModuleID] = Seq(
    "org.pegdown"              % "pegdown"                % "1.6.0",
    "org.scalatest"           %% "scalatest"              % "3.0.8",
    "org.scalatestplus.play"  %% "scalatestplus-play"     % "4.0.3",
    "uk.gov.hmrc"             %% "hmrctest"               % "3.9.0-play-26",
    "org.jsoup"                % "jsoup"                  % "1.12.1",
    "com.typesafe.play"       %% "play-test"              % PlayVersion.current,
    "org.mockito"              % "mockito-all"            % "1.10.19",
    "org.scalacheck"          %% "scalacheck"             % "1.14.3",
    "wolfendale"              %% "scalacheck-gen-regexp"  % "0.1.2",
    "com.github.tomakehurst"   % "wiremock-standalone"    % "2.25.1"
  )

  def apply(): Seq[ModuleID] = compile ++ test
}
