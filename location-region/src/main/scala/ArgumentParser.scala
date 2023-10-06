import java.nio.file.{Files, Paths}

object ArgumentParser {
  def parseArgs(args: Array[String]): Map[String, String] = {
    val parsedArgs = args.flatMap(arg => {
      val pair = arg.split("=")
      if (pair.length == 2) Some(pair(0).stripPrefix("--") -> pair(1))
      else None
    }).toMap

    parsedArgs.foreach {
      case (key, value) if key == "regions" || key == "locations" || key == "output" =>
        if (!Files.exists(Paths.get(value))) {
          println(s"File does not exist: $value for key: $key")
        }
      case _ =>
    }

    parsedArgs
  }
}
