/* Base on build.sbt @3243f05 on 29 Jun */

// 组织名称
organization := "cnrv.io"

// 项目名称
name := "Learn FIRRTL by TDD"

// 项目版本
version := "0.1"

// Scala 版本
scalaVersion := "2.11.12"

// 可选的 Scala 版本
crossScalaVersions := Seq("2.11.12", "2.12.4")

// 根据当前选择的 Scala 版本选择对应 Scalac 和 javac 的版本
def scalacOptionsVersion(scalaVersion: String): Seq[String] = {
  Seq() ++ {
    // If we're building with Scala > 2.11, enable the compile option
    //  switch to support our anonymous Bundle definitions:
    //  https://github.com/scala/bug/issues/10047
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 => Seq()
      case _ => Seq("-Xsource:2.11")
    }
  }
}

scalacOptions := scalacOptionsVersion(scalaVersion.value) ++ Seq(
  "-deprecation"
)

def javacOptionsVersion(scalaVersion: String): Seq[String] = {
  Seq() ++ {
    // Scala 2.12 requires Java 8, but we continue to generate
    //  Java 7 compatible code until we need Java 8 features
    //  for compatibility with old clients.
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 =>
        Seq("-source", "1.7", "-target", "1.7")
      case _ =>
        Seq("-source", "1.8", "-target", "1.8")
    }
  }
}

javacOptions ++= javacOptionsVersion(scalaVersion.value)

// 依赖的库
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.6.0"

// JAR 打包配置
assemblyJarName in assembly := "firrtl.jar"

test in assembly := {} // Should there be tests?

assemblyOutputPath in assembly := file("./utils/bin/firrtl.jar")
