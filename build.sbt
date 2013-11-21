name := "ProjectCream"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.amazonaws" % "aws-java-sdk" % "1.6.4",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
)

play.Project.playJavaSettings
