val scala3Version = "3.7.4"
val zioVersion = "2.1.19"
val zioJsonVersion = "0.6.2"
val circeVersion = "0.14.10"
val zioDirect = "1.0.0-RC7"
val zioHttp = "3.0.1"
val doobieVersion = "1.0.0-RC4"
val zioInteropCatsVersion = "23.1.0.0"

ThisBuild / scalaVersion := scala3Version
ThisBuild / version := "0.1.0"

lazy val api = project
  .in(file("api"))
  .settings(
    name := "leetcode-quiz-api",
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      artifact.name + "." + artifact.extension
    },
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-json" % zioJsonVersion
    )
  )

lazy val app = project
  .in(file("app"))
  .dependsOn(api)
  .settings(
    name := "leetcode-quiz-app",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "services", xs @ _*) => MergeStrategy.concat
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "reference.conf" => MergeStrategy.concat
      case "application.conf" => MergeStrategy.concat
      case x => MergeStrategy.first
    },
    wartremoverErrors ++= Warts
      .allBut(
        Wart.Any,
        Wart.Equals,
        Wart.Nothing,
        Wart.ToString,
        Wart.FinalCaseClass,
        Wart.Overloading,
        Wart.DefaultArguments,
        Wart.Var,
        Wart.PlatformDefault,
        Wart.While,
        Wart.AsInstanceOf,
        Wart.Return,
        Wart.MutableDataStructures,
        Wart.EnumValueOf,
        Wart.SeqApply,
        Wart.OptionPartial
      ),
    assembly / mainClass := Some("com.github.ai.leetcodequiz.Main"),
    assembly / assemblyJarName := "leetcode-quiz-backend.jar",
    libraryDependencies ++= Seq(
      // Testing
      "org.scalameta" %% "munit" % "1.0.0" % Test,

      // ZIO
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-streams" % zioVersion,
      "dev.zio" %% "zio-http" % zioHttp,
      "dev.zio" %% "zio-direct" % zioDirect,

      // Logging
      "dev.zio" %% "zio-logging" % "2.3.2",
      "dev.zio" %% "zio-logging-slf4j" % "2.3.1",
      "ch.qos.logback" % "logback-classic" % "1.5.11",

      // JWT
      "com.auth0" % "java-jwt" % "4.5.0",

      // Jgit
      "org.eclipse.jgit" % "org.eclipse.jgit" % "6.2.0.202206071550-r",

      // Database
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "org.xerial" % "sqlite-jdbc" % "3.51.1.0",
      "dev.zio" %% "zio-interop-cats" % zioInteropCatsVersion,
      "com.typesafe" % "config" % "1.4.3",

      // Password Hashing
      "org.mindrot" % "jbcrypt" % "0.4",

      // CSV file parsing
      "org.apache.commons" % "commons-csv" % "1.10.0",

      // Json
      "dev.zio" %% "zio-json" % zioJsonVersion
    )
  )

lazy val apiClient = project
  .in(file("api-client"))
  .dependsOn(api)
  .settings(
    name := "leetcode-quiz-api-client",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
//    wartremoverErrors ++= Warts.unsafe,
    assembly / mainClass := Some("com.github.ai.leetcodequiz.client.ApiClientMain"),
    assembly / assemblyJarName := "leetcode-quiz-api-client.jar",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-direct" % zioDirect,
      "dev.zio" %% "zio-http" % zioHttp
    )
  )
