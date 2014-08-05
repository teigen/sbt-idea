import org.sbtidea.test.util.AbstractScriptedTestBuild
import sbt._
import sbt.Keys._
import org.sbtidea.SbtIdeaPlugin._

object ScriptedTestBuild extends AbstractScriptedTestBuild("source-generators") {
  
  lazy val root = Project("main", file("."), settings = Defaults.defaultSettings ++ scriptedTestSettings ++ Seq(
    libraryDependencies ++= dependencies, scalacOptions ++= Seq("-unchecked", "-deprecation", "-P:continuations:enable", "-language:implicitConversions"),
    ideaBasePackage := Some("foo.bar"),
    autoCompilerPlugins := true,
    libraryDependencies <<= (scalaVersion, libraryDependencies) { (ver, deps) =>
      deps :+ compilerPlugin("org.scala-lang.plugins" % "continuations" % ver)
    },
    sourceGenerators in Compile <+= Def.task{
      val content = """
        |package generated
        |
        |object Generated { def hi = "Hello" }""".stripMargin
      val out = (sourceManaged in Compile).value / "generated" / "Generated.scala"
      IO.write(out, content)
      Seq(out)
    }
  ))

  lazy val dependencies = Seq(
    "junit" % "junit" % "4.8.2",
    "org.eclipse.jetty" % "jetty-server" % "7.0.0.v20091005" % "test",
    "javax.servlet" % "servlet-api" % "2.5" % "provided",
    "mysql" % "mysql-connector-java" % "5.1.25" % "runtime"
  )
}
