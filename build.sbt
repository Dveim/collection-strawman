// Convenient setting that allows writing `set scalaVersion := dotty.value` in sbt shell to switch from Scala to Dotty
val dotty = settingKey[String]("dotty version")
dotty in ThisBuild := dottyLatestNightlyBuild.get

val commonSettings = Seq(
  organization := "ch.epfl.scala",
  version := "0.2.0-SNAPSHOT",
  scalaVersion := "2.13.0-M1",
  crossScalaVersions := scalaVersion.value :: dotty.value :: Nil,
  scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-opt-warnings", "-Yno-imports", "-language:higherKinds", "-opt:l:classpath"),
  testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a"),
  fork in Test := true,
  parallelExecution in Test := false
)

val collections =
  project.in(file("."))
    .settings(commonSettings: _*)
    .settings(
      name := "collection-strawman",
      libraryDependencies ++= Seq(
        "com.novocode" % "junit-interface" % "0.11" % Test
      ),
      pomExtra :=
        <developers>
          <developer><id>ichoran</id><name>Rex Kerr</name></developer>
          <developer><id>odersky</id><name>Martin Odersky</name></developer>
          <developer><id>julienrf</id><name>Julien Richard-Foy</name></developer>
          <developer><id>szeiger</id><name>Stefan Zeiger</name></developer>
        </developers>,
      homepage := Some(url("https://github.com/scala/collection-strawman")),
      licenses := Seq("BSD 3-clause" -> url("http://opensource.org/licenses/BSD-3-Clause")),
      scmInfo := Some(
        ScmInfo(
          url("https://github.com/scala/collection-strawman"),
          "scm:git:git@github.com:scala/collection-strawman.git"
        )
      ),
      // For publishing snapshots
      credentials ++= (
        for {
          username <- sys.env.get("SONATYPE_USERNAME")
          password <- sys.env.get("SONATYPE_PASSWORD")
        } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)
      ).toList
    )

val timeBenchmark =
  project.in(file("benchmarks/time"))
    .dependsOn(collections)
    .enablePlugins(JmhPlugin)
    .settings(commonSettings: _*)
    .settings(
      charts := Def.inputTaskDyn {
        val benchmarks = Def.spaceDelimited().parsed
        val targetDir = crossTarget.value
        val jmhReport = targetDir / "jmh-result.json"
        val runTask = run in Jmh
        Def.inputTask {
          val _ = runTask.evaluated
          strawman.collection.Bencharts(jmhReport, "Execution time (lower is better)", targetDir)
          targetDir
        }.toTask(s" -rf json -rff ${jmhReport.absolutePath} ${benchmarks.mkString(" ")}")
      }.evaluated
    )


val timeJava8Benchmark =
  project.in(file("benchmarks/timeJava8"))
    .enablePlugins(JmhPlugin)
    .settings(commonSettings: _*)
    .settings(
      charts := Def.inputTaskDyn {
        val benchmarks = Def.spaceDelimited().parsed
        val targetDir = crossTarget.value
        val jmhReport = targetDir / "jmh-result.json"
        val runTask = run in Jmh
        Def.inputTask {
          val _ = runTask.evaluated
          strawman.collection.Bencharts(jmhReport, "Execution time (lower is better)", targetDir)
          targetDir
        }.toTask(s" -rf json -rff ${jmhReport.absolutePath} ${benchmarks.mkString(" ")}")
      }.evaluated
    )

val memoryBenchmark =
  project.in(file("benchmarks/memory"))
    .dependsOn(collections)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.2",
      charts := Def.inputTaskDyn {
        val targetDir = crossTarget.value
        val report = targetDir / "report.json"
        val runTask = run in Compile
        Def.inputTask {
          val _ = runTask.evaluated
          strawman.collection.Bencharts(report, "Memory footprint (lower is better)", targetDir)
          targetDir
        }.toTask(s" ${report.absolutePath}")
      }.evaluated
    )

lazy val charts = inputKey[File]("Runs the benchmarks and produce charts")
