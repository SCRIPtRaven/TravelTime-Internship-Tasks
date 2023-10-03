import upickle.default._
import os._

object Main extends App {
  case class Location(name: String, coordinates: (Double, Double))
  case class Region(name: String, coordinates: Seq[Seq[(Double, Double)]])

  implicit val locationRw: ReadWriter[Location] = macroRW
  implicit val regionRw: ReadWriter[Region] = macroRW
  
  def parseLocationsJson(file: String): Seq[Location] = {
    val jsonStr: String = os.read(os.pwd / file)
    val locations: Seq[Location] = upickle.default.read[Seq[Location]](jsonStr)

    locations
  }

  def parseRegionsJson(file: String): Seq[Region] = {
    val jsonStr: String = os.read(os.pwd / file)
    val regions: Seq[Region] = upickle.default.read[Seq[Region]](jsonStr)

    regions
  } 

  val locations = parseLocationsJson("locations.json")
  val regions = parseRegionsJson("regions.json")
  println(locations)
  println(regions)
}