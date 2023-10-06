import Models._

object RegionMatcher {
  def matching(locations: Seq[Location], regions: Seq[Region]): Seq[MatchedRegions] = {
    regions.map { region =>
      val matchedLocations = locations.collect {
        case location if region.coordinates.exists(polygon => isLocationInRegion(location.coordinates, polygon)) =>
          location.name
      }
      MatchedRegions(region.name, matchedLocations)
    }
  }

  def isLocationInRegion(point: (Double, Double), polygon: Seq[(Double, Double)]): Boolean = {
    if (polygon.isEmpty) return false
    
    polygon.zip(polygon.tail :+ polygon.head).count { case ((x1, y1), (x2, y2)) =>
      (point._2 > Math.min(y1, y2)) && (point._2 <= Math.max(y1, y2)) &&
      (point._1 <= Math.max(x1, x2)) && (y1 != y2) &&
      (point._1 <= (x2 - x1) * (point._2 - y1) / (y2 - y1) + x1)
    } % 2 != 0
  }
}
