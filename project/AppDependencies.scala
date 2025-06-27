import sbt.*

object AppDependencies {

  val bootstrapVersion = "9.13.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"   %% "play-frontend-hmrc-play-30"            % "12.6.0",
    "uk.gov.hmrc"   %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc"   %% "play-conditional-form-mapping-play-30" % "3.3.0",
    "uk.gov.hmrc"   %% "domain-play-30"                        % "11.0.0",
    "org.typelevel" %% "cats-core"                             % "2.13.0",
    "uk.gov.hmrc"   %% "tax-year"                              % "6.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapVersion,
    "org.scalatest"           %% "scalatest"                % "3.2.19",
    "org.scalatestplus"       %% "scalacheck-1-17"          % "3.2.18.0",
    "org.jsoup"                % "jsoup"                    % "1.21.1",
    "org.mockito"             %% "mockito-scala-scalatest"  % "2.0.0",
    "org.scalacheck"          %% "scalacheck"               % "1.18.1",
    "org.wiremock"             % "wiremock-standalone"      % "3.13.1",
    "wolfendale"              %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.vladsch.flexmark"     % "flexmark-all"             % "0.64.8"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
