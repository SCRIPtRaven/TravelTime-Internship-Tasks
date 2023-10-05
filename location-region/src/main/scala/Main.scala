// I have chosen the uPickle library for JSON file parsing because it has excellent case class support
import upickle.default._
import os._
import scala.util.{Try, Success, Failure}

object Main extends App {
  case class Location(name: String, coordinates: (Double, Double))
  case class Region(name: String, coordinates: Seq[Seq[(Double, Double)]])
  case class MatchedRegions(region: String, matchedLocations: Seq[String])

  implicit val locationRw: ReadWriter[Location] = macroRW
  implicit val regionRw: ReadWriter[Region] = macroRW
  implicit val matchedRw: ReadWriter[MatchedRegions] = macroRW
  
  def parseJson[T: ReadWriter](jsonString: String): Try[Seq[T]] = {
    Try(upickle.default.read[Seq[T]](jsonString))
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

  def outputToJson(results: Seq[MatchedRegions]): Try[String] = {
    Try(upickle.default.write(results, indent = 2))
  }

  val argMap = args.map(arg => {
    val pair = arg.split("=")
    pair(0) -> pair(1)
  }).toMap
  val regionsFile = argMap.getOrElse("--regions", "regions.json")
  val locationsFile = argMap.getOrElse("--locations", "locations.json")
  val outputFile = argMap.getOrElse("--output", "results.json")

  val workingDirectory: os.Path = os.pwd
  val readLocations: String = os.read(workingDirectory / locationsFile)
  val readRegions: String = os.read(workingDirectory / regionsFile)

  val locations: Try[Seq[Location]] = parseJson[Location](readLocations)
  val regions: Try[Seq[Region]] = parseJson[Region](readRegions)
  
  (locations, regions) match {
    case (Success(locs), Success(regs)) =>
      val results = matching(locs, regs)
      val output = outputToJson(results)
      output match {
        case Success(jsonString) => os.write.over(workingDirectory / outputFile, jsonString)
        case Failure(_) => println("Failed to write results.")
      }
    case _ =>
      println("Input files could not be parsed.")
  }
}