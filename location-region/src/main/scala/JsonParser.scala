import upickle.default._
import Models._
import scala.util.Try

object JsonParser {
  implicit val locationRw: ReadWriter[Location] = macroRW
  implicit val regionRw: ReadWriter[Region] = macroRW
  implicit val matchedRw: ReadWriter[MatchedRegions] = macroRW

  def parseJson[T: ReadWriter](jsonString: String): Try[Seq[T]] = {
    Try(upickle.default.read[Seq[T]](jsonString))
  }

  def outputToJson(results: Seq[MatchedRegions]): Try[String] = {
    Try(upickle.default.write(results, indent = 2))
  }
}
