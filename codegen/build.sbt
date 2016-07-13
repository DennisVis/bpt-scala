name := "bpt-codegen"

libraryDependencies ++= Seq(
  "com.typesafe.slick"  %% "slick"              % "3.1.1",
  "com.typesafe.slick"  %% "slick-codegen"      % "3.1.1",
  "org.postgresql"      %  "postgresql"         % "9.4.1208.jre7"
)

lazy val slick = TaskKey[Seq[File]]("gen-tables")
lazy val slickCodeGenTask = (baseDirectory, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val outputDir = (dir / "../src/main/scala").getPath
  val url = "jdbc:postgresql://localhost:5432/bpt"
  val jdbcDriver = "org.postgresql.Driver"
  val slickDriver = "slick.driver.PostgresDriver"
  val pkg = "com.xebia.bpt.persistence"
  toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg), s.log))
  val fname = outputDir + "/com/xebia/bpt/persistence/Tables.scala"
  Seq(file(fname))
}

slick <<= slickCodeGenTask
sourceGenerators in Compile <+= slickCodeGenTask
