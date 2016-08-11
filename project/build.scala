import io.gatling.sbt.GatlingPlugin
import sbt._
import Keys._
import org.scalatra.sbt._
import com.earldouglas.xwp.JettyPlugin
import com.earldouglas.xwp.JettyPlugin.autoImport._
import com.earldouglas.xwp.ContainerPlugin.autoImport._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object BptScalaBuild extends Build {
  val Organization = "com.xebia"
  val Name = "bpt-scala"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.8"
  val ScalatraVersion = "2.4.0"

  lazy val aaaMain = Project (
    "bpt-scala",
    file("."),
    settings = ScalatraPlugin.scalatraSettings ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= Seq(
        "org.scalatra"        %% "scalatra"           % ScalatraVersion,
        "org.scalatra"        %% "scalatra-scalate"   % ScalatraVersion,
        "org.scalatra"        %% "scalatra-json"      % ScalatraVersion,
        "org.json4s"          %% "json4s-jackson"     % "3.3.0",
        "com.typesafe.slick"  %% "slick"              % "3.1.1",
        "org.postgresql"      %  "postgresql"         % "9.4.1208.jre7",
        "com.mchange"         %  "c3p0"               % "0.9.5.2",
        "ch.qos.logback"      %  "logback-classic"    % "1.1.5"             % "runtime",
        "org.eclipse.jetty"   %  "jetty-webapp"       % "9.2.15.v20160210"  % "container",
        "javax.servlet"       %  "javax.servlet-api"  % "3.1.0"             % "provided",
        "org.scalatra"        %% "scalatra-specs2"    % ScalatraVersion     % "test"
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding(
                "context", "_root_.org.scalatra.scalate.ScalatraRenderContext",
                importMembers = true,
                isImplicit = true
              )
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      },
      containerPort in Jetty := 3000
    )
  ).enablePlugins(JettyPlugin).aggregate(codegen)

  lazy val codegen = Project("codegen", file("codegen"))

  lazy val loadTest = Project(
    "load-test",
    file("load-test"),
    settings = Seq(
      organization := Organization,
      name := "bpt-load-test",
      version := Version,
      scalaVersion := ScalaVersion,
      libraryDependencies ++= Seq(
        "io.gatling"            % "gatling-test-framework"    % "2.2.2",
        "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2"
      )
    )
  ).enablePlugins(GatlingPlugin).dependsOn(aaaMain)
}
