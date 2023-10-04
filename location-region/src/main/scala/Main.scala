// I have chosen the uPickle library for JSON file parsing because it has excellent case class support
import upickle.default._
import os._

object Main extends App {
  case class Location(name: String, coordinates: (Double, Double))
  case class Region(name: String, coordinates: Seq[Seq[(Double, Double)]])
  case class MatchedRegions(region: String, matchedLocations: Seq[String])

  implicit val locationRw: ReadWriter[Location] = macroRW
  implicit val regionRw: ReadWriter[Region] = macroRW
  implicit val matchedRw: ReadWriter[MatchedRegions] = macroRW
  
  def parseJson[T: ReadWriter](file: String, directory: os.Path): Seq[T] = {
    val parsedData: Seq[T] = upickle.default.read[Seq[T]](os.read(directory / file))
    parsedData
  }

  def matching(locations: Seq[Location], regions: Seq[Region]): Seq[MatchedRegions] = {
    val matchedRegions = regions.map { region =>
      val matchedLocations = locations.collect {
        case location if region.coordinates.exists(polygon => isLocationInRegion(location.coordinates, polygon)) =>
          location.name
      }
      MatchedRegions(region.name, matchedLocations)
    }
    matchedRegions
  }

  def isLocationInRegion(location: (Double, Double), region: Seq[(Double, Double)]): Boolean = {
    val x = location._1
    val y = location._2
    var isInside = false

    var i = 0
    var j = region.size - 1
    while (i < region.size) {
      val xi = region(i)._1
      val yi = region(i)._2
      val xj = region(j)._1
      val yj = region(j)._2

      // Raycasting algorithm
      val intersect = ((yi > y) != (yj > y)) &&
        (x < (xj - xi) * (y - yi) / (yj - yi) + xi)

      if (intersect) isInside = !isInside
      j = i
      i += 1
    }
    isInside
  }

  def outputToJson(file: String, results: Seq[MatchedRegions]): Unit = {
    os.write.over(os.pwd / file, upickle.default.write(results, indent = 2))
  }

  val workingDirectory: os.Path = os.pwd

  val locations: Seq[Location] = parseJson[Location]("locations.json", workingDirectory)
  val regions: Seq[Region] = parseJson[Region]("regions.json", workingDirectory)

  val results = matching(locations, regions)
  outputToJson("results.json", results)
}