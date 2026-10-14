import sbt.*

object AppDependencies {

  val bootstrapVersion = "10.8.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"   %% "play-frontend-hmrc-play-30"            % "13.16.0",
    "uk.gov.hmrc"   %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc"   %% "play-conditional-form-mapping-play-30" % "3.5.0",
    "uk.gov.hmrc"   %% "domain-play-30"                        % "11.0.0",
    "org.typelevel" %% "cats-core"                             % "2.13.0",
    "uk.gov.hmrc"   %% "tax-year"                              % "6.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"          %% "bootstrap-test-play-30" % bootstrapVersion,
    "org.scalatestplus"    %% "scalacheck-1-20"        % "3.2.20.0",
    "org.wiremock"          % "wiremock-standalone"    % "3.13.2",
    "io.github.wolfendale" %% "scalacheck-gen-regexp"  % "1.1.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
