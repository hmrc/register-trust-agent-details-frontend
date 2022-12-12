import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"         %% "play-frontend-hmrc"             % "3.6.0-play-28",
    "uk.gov.hmrc"         %% "play-conditional-form-mapping"  % "1.12.0-play-28",
    "uk.gov.hmrc"         %% "domain"                         % "8.1.0-play-28",
    "uk.gov.hmrc"         %% "bootstrap-frontend-play-28"     % "7.8.0",
    "org.typelevel"       %% "cats-core"                      % "2.8.0",
    "uk.gov.hmrc"         %% "tax-year"                       % "3.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"           %% "scalatest"                % "3.2.14",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.1.0",
    "org.scalatestplus"       %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.jsoup"                % "jsoup"                    % "1.15.3",
    "com.typesafe.play"       %% "play-test"                % PlayVersion.current,
    "org.mockito"             %% "mockito-scala-scalatest"  % "1.17.12",
    "org.scalacheck"          %% "scalacheck"               % "1.17.0",
    "com.github.tomakehurst"   % "wiremock-standalone"      % "2.27.2",
    "wolfendale"              %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.vladsch.flexmark"     % "flexmark-all"             % "0.62.2"
  ).map(_ % "test")

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion,
    "commons-codec"     % "commons-codec" % "1.12"
  )
}
