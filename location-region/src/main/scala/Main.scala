// I have chosen the uPickle library for JSON file parsing because it has excellent case class support
import upickle.default._
import os._

object Main extends App {
  case class Location(name: String, coordinates: (Double, Double))
  case class Region(name: String, coordinates: Seq[Seq[(Double, Double)]])
  case class MatchedRegions(regionName: String, locationName: String)

  implicit val locationRw: ReadWriter[Location] = macroRW
  implicit val regionRw: ReadWriter[Region] = macroRW
  
  // TODO: Look into ways to combine the two parsing functions into one
  // Options: Either clause, sealed trait, generics
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

  def matching(locations: Seq[Location], regions: Seq[Region]): Seq[MatchedRegions] = {
    // TODO: Implement
    ???
  }

  def outputToJson(file: String, results: Seq[MatchedRegions]): Unit = {
    // TODO: Implement
  }

  val locations = parseLocationsJson("locations.json")
  val regions = parseRegionsJson("regions.json")
  println(locations)
  println(regions)
}