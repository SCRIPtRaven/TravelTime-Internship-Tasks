object Models {
  case class Location(name: String, coordinates: (Double, Double))
  case class Region(name: String, coordinates: Seq[Seq[(Double, Double)]])
  case class MatchedRegions(region: String, matchedLocations: Seq[String])
}
