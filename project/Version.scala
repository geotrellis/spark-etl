import scala.util.Properties

object Version {
  val scala = "2.11.12"
  val crossScala = "2.12.7"
  val geotrellis = "2.3.0"
  val spark = Properties.envOrElse("SPARK_VERSION", "2.4.1")
}
