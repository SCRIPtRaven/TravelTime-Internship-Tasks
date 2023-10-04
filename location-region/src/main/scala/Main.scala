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

  def isLocationInRegion(point: (Double, Double), polygon: Seq[(Double, Double)]): Boolean = {
    polygon.zip(polygon.tail :+ polygon.head).count { case ((x1, y1), (x2, y2)) =>
      (point._2 > Math.min(y1, y2)) && (point._2 <= Math.max(y1, y2)) &&
        (point._1 <= Math.max(x1, x2)) && (y1 != y2) &&
        (point._1 <= (x2 - x1) * (point._2 - y1) / (y2 - y1) + x1)
    } % 2 != 0
  }

  def outputToJson(file: String, directory: os.Path, results: Seq[MatchedRegions]): Unit = {
    os.write.over(directory / file, upickle.default.write(results, indent = 2))
  }

  val argMap = args.map(arg => {
    val pair = arg.split("=")
    pair(0) -> pair(1)
  }).toMap
  val regionsFile = argMap.getOrElse("--regions", "regions.json")
  val locationsFile = argMap.getOrElse("--locations", "locations.json")
  val outputFile = argMap.getOrElse("--output", "results.json")

  val workingDirectory: os.Path = os.pwd

  val locations: Seq[Location] = parseJson[Location](locationsFile, workingDirectory)
  val regions: Seq[Region] = parseJson[Region](regionsFile, workingDirectory)
  val results = matching(locations, regions)
  outputToJson(outputFile, workingDirectory, results)
}