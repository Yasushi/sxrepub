libraryDependencies ++= Seq(
  "xmlpull" % "xmlpull" % "1.1.3.4d_b4_min",
  "net.sf.kxml" % "kxml2" % "2.3.0",
  "org.slf4j" % "slf4j-api" % "1.6.6",
  "org.slf4j" % "slf4j-simple" % "1.6.6",
  "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.0" notTransitive(),
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.0" notTransitive(),
  "com.jsuereth" %% "scala-arm" % "1.2",
  "net.databinder" %% "dispatch-tagsoup" % "0.8.8"
)


scalaVersion := "2.9.2"
