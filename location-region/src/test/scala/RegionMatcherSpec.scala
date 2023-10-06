import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Models._

class RegionMatcherSpec extends AnyFlatSpec with Matchers {
  
  "isLocationInRegion" should "return true if a point is inside a polygon" in {
    val point = (2.0, 2.0)
    val polygon = Seq((0.0, 0.0), (4.0, 0.0), (4.0, 4.0), (0.0, 4.0))
    val result = RegionMatcher.isLocationInRegion(point, polygon)
    result shouldEqual true
  }

  it should "return false if a point is outside a polygon" in {
    val point = (5.0, 5.0)
    val polygon = Seq((0.0, 0.0), (4.0, 0.0), (4.0, 4.0), (0.0, 4.0))
    val result = RegionMatcher.isLocationInRegion(point, polygon)
    result shouldEqual false
  }

  it should "return false if a polygon is empty" in {
    val point = (3.0, 3.0)
    val polygon = Seq()
    val result = RegionMatcher.isLocationInRegion(point, polygon)
    result shouldEqual false
  }

  "matching" should "return matched regions and locations" in {
    val locations = Seq(Location("Loc1", (2.0, 2.0)), Location("Loc2", (5.0, 5.0)))
    val regions = Seq(Region("Reg1", Seq(Seq((0.0, 0.0), (4.0, 0.0), (4.0, 4.0), (0.0, 4.0)))))
    val expected = Seq(MatchedRegions("Reg1", Seq("Loc1")))
    val result = RegionMatcher.matching(locations, regions)
    result shouldEqual expected
  }

  it should "return empty matched locations if no locations are in any region" in {
    val locations = Seq(Location("Loc1", (5.0, 5.0)))
    val regions = Seq(Region("Reg1", Seq(Seq((0.0, 0.0), (4.0, 0.0), (4.0, 4.0), (0.0, 4.0)))))
    val expected = Seq(MatchedRegions("Reg1", Seq()))
    val result = RegionMatcher.matching(locations, regions)
    result shouldEqual expected
  }

}
