import os._
import Models._
import scala.util.{Try, Success, Failure}
import JsonParser.{locationRw, regionRw}

object Main extends App {
  val argMap = ArgumentParser.parseArgs(args)
  val regionsFile = argMap.getOrElse("--regions", "regions.json")
  val locationsFile = argMap.getOrElse("--locations", "locations.json")
  val outputFile = argMap.getOrElse("--output", "results.json")

  val workingDirectory: os.Path = os.pwd
  val readLocations: String = os.read(workingDirectory / locationsFile)
  val readRegions: String = os.read(workingDirectory / regionsFile)

  val locations: Try[Seq[Location]] = JsonParser.parseJson[Location](readLocations)
  val regions: Try[Seq[Region]] = JsonParser.parseJson[Region](readRegions)

  (locations, regions) match {
    case (Success(locs), Success(regs)) =>
      val results = RegionMatcher.matching(locs, regs)
      val output = JsonParser.outputToJson(results)
      output match {
        case Success(jsonString) => os.write.over(workingDirectory / outputFile, jsonString)
        case Failure(_) => println("Failed to write results.")
      }
    case _ =>
      println("Input files could not be parsed.")
  }
}
