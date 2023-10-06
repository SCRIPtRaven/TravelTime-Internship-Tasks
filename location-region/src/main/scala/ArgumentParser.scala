object ArgumentParser {
  def parseArgs(args: Array[String]): Map[String, String] = {
    args.map(arg => {
      val pair = arg.split("=")
      pair(0) -> pair(1)
    }).toMap
  }
}
