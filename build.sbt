import de.heikoseeberger.sbtheader._
import sbt.Keys._


lazy val settings = Seq(
  name := "spark-etl",
  description := "geotrellis ETL code (archived)",
  version := Version.geotrellis,
  scalaVersion := Version.scala,
  crossScalaVersions := Seq(Version.crossScala),
  organization := "org.locationtech.geotrellis",
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url("http://geotrellis.github.io")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/locationtech/geotrellis"), "scm:git:git@github.com:locationtech/geotrellis.git"
  )),
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-language:higherKinds",
    "-language:postfixOps",
    "-language:existentials",
    "-language:experimental.macros",
    "-feature",
    "-Ypartial-unification", // Required by Cats
    "-target:jvm-1.8"
  ),
  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.10.1" cross CrossVersion.binary),
  addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.1" cross CrossVersion.full),
  projectDependencies := Seq(
    "org.locationtech.geotrellis" %% "geotrellis-spark"      % Version.geotrellis,
    "org.locationtech.geotrellis" %% "geotrellis-s3"         % Version.geotrellis,
    "org.locationtech.geotrellis" %% "geotrellis-accumulo"   % Version.geotrellis,
    "org.locationtech.geotrellis" %% "geotrellis-cassandra"  % Version.geotrellis,
    "org.locationtech.geotrellis" %% "geotrellis-hbase"      % Version.geotrellis,
    "com.networknt"                % "json-schema-validator" % "0.1.23",
    "org.apache.spark"            %% "spark-core"            % Version.spark % Provided,
    "org.apache.spark"            %% "spark-sql"             % Version.spark % Test,
    "org.scalatest"               %% "scalatest"             % "3.0.7"       % Test
  ),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val sonatype = "https://oss.sonatype.org/"
    val locationtech = "https://repo.locationtech.org/content/repositories"
    if (isSnapshot.value) {
      // Publish snapshots to LocationTech
      Some("LocationTech Snapshot Repository" at s"${locationtech}/geotrellis-snapshots")
    } else {
      val milestoneRx = """-(M|RC)\d+$""".r
      milestoneRx.findFirstIn(Version.geotrellis) match {
        case Some(v) =>
          // Public milestones to LocationTech
          Some("LocationTech Release Repository" at s"${locationtech}/geotrellis-releases")
        case None =>
          // Publish releases to Sonatype
          Some("Sonatype Release Repository" at s"${sonatype}service/local/staging/deploy/maven2")
      }
    }
  },
  credentials ++= List(Path.userHome / ".ivy2" / ".credentials")
    .filter(_.asFile.canRead)
    .map(Credentials(_)),
  pomExtra := (
    <developers>
      <developer>
        <id>echeipesh</id>
        <name>Eugene Cheipesh</name>
        <url>http://github.com/echeipesh/</url>
      </developer>
      <developer>
        <id>lossyrob</id>
        <name>Rob Emanuele</name>
        <url>http://github.com/lossyrob/</url>
      </developer>
    </developers>),
  shellPrompt := { s => Project.extract(s).currentProject.id + " > " },
  resolvers ++= Seq(
    "geosolutions" at "http://maven.geo-solutions.it/",
    "osgeo" at "http://download.osgeo.org/webdav/geotools/",
    Resolver.sonatypeRepo("releases"),
    Resolver.bintrayRepo("azavea", "geotrellis"),
    "locationtech-releases" at "https://repo.locationtech.org/content/groups/releases",
    "locationtech-snapshots" at "https://repo.locationtech.org/content/groups/snapshots"
  ),
  headerLicense := Some(HeaderLicense.ALv2("2019", "Azavea")),
  // preserve year of old headers
  headerMappings :=
    Map(FileType.scala -> CommentStyle.cStyleBlockComment.copy(commentCreator = new CommentCreator() {
      val Pattern = "(?s).*?(\\d{4}(-\\d{4})?).*".r
      def findYear(header: String): Option[String] = header match {
        case Pattern(years, _) => Some(years)
        case _                 => None
      }
      override def apply(text: String, existingText: Option[String]): String = {
        val newText = CommentStyle.cStyleBlockComment.commentCreator.apply(text, existingText)
        existingText
          .flatMap(findYear)
          .map(year => newText.replace("2018", year))
          .getOrElse(newText)
      } } )),
  scapegoatVersion in ThisBuild := "1.3.8",
  updateOptions := updateOptions.value.withGigahorse(false)
)

lazy val root = project
  .in(file("spark-etl"))
  .settings(settings)

