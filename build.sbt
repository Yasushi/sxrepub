libraryDependencies ++= Seq(
  "xmlpull" % "xmlpull" % "1.1.3.4d_b4_min",
  "net.sf.kxml" % "kxml2" % "2.3.0",
  "org.slf4j" % "slf4j-api" % "1.6.1",
  "org.slf4j" % "slf4j-simple" % "1.6.1",
  "com.github.scala-incubator.io" % "scala-io-core_2.9.0-1" % "0.2.0-SNAPSHOT" notTransitive(),
  "com.github.scala-incubator.io" % "scala-io-file_2.9.0-1" % "0.2.0-SNAPSHOT" notTransitive(),
  "com.github.jsuereth.scala-arm" %% "scala-arm" % "0.3",
  "net.databinder" %% "dispatch-tagsoup" % "0.8.7"
)

resolvers += ScalaToolsSnapshots
