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
    // Return false if the polygon is empty
    if (polygon.isEmpty) return false

    // Method to check if a point is within the vertical bounds of a line segment
    def isWithinVerticalBounds(y: Double, y1: Double, y2: Double): Boolean = {
      y > Math.min(y1, y2) && y <= Math.max(y1, y2)
    }

    // Method to check if a point is to the left of a line segment
    def isLeftOfSegment(x: Double, x1: Double, x2: Double): Boolean = {
      x <= Math.max(x1, x2)
    }

    // Method to calculate the x-coordinate of the point where 
    // the horizontal ray intersects the line segment
    def intersectionX(y: Double, x1: Double, y1: Double, x2: Double, y2: Double): Double = {
      (x2 - x1) * (y - y1) / (y2 - y1) + x1
    }

    // Count the number of intersections between the polygon and a horizontal ray
    // emanating from the point to the right
    val intersectionCount = polygon.zip(polygon.tail :+ polygon.head).count {
      case ((x1, y1), (x2, y2)) =>
        isWithinVerticalBounds(point._2, y1, y2) && 
        isLeftOfSegment(point._1, x1, x2) && 
        y1 != y2 && 
        point._1 <= intersectionX(point._2, x1, y1, x2, y2)
    }

    // Point is inside the polygon if the number of intersections is odd
    intersectionCount % 2 != 0
  }

}
