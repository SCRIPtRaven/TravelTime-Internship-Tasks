import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Models._
import JsonParser._
import upickle.default._
import scala.util.{Success, Failure}

class JsonParserSpec extends AnyFlatSpec with Matchers {

  "parseJson" should "correctly parse a valid JSON string into a Seq[Location]" in {
    val json = """[{"name": "Location1", "coordinates": [1.0, 1.0]}]"""
    val expected = Seq(Location("Location1", (1.0, 1.0)))
    val result = JsonParser.parseJson[Location](json)
    result shouldBe Success(expected)
  }

  it should "correctly parse a valid JSON string into a Seq[Region]" in {
    val json = """[{"name": "Region1", "coordinates": [[[1.0, 1.0], [2.0, 2.0]], [[3.0, 3.0], [4.0, 4.0]]]}]"""
    val expected = Seq(Region("Region1", Seq(Seq((1.0, 1.0), (2.0, 2.0)), Seq((3.0, 3.0), (4.0, 4.0)))))
    val result = JsonParser.parseJson[Region](json)
    result shouldBe Success(expected)
  }

  it should "fail for an invalid JSON string for Location" in {
    val json = """[{"name": "Location1", "coordinates": "invalid"}]"""
    val result = JsonParser.parseJson[Location](json)
    result shouldBe a [Failure[_]]
  }

  it should "fail for an invalid JSON string for Region" in {
    val json = """[{"name": "Region1", "coordinates": "invalid"}]"""
    val result = JsonParser.parseJson[Region](json)
    result shouldBe a [Failure[_]]
  }

  "outputJson" should "correctly serialize a Seq[MatchedRegions] into a JSON string" in {
    val data = Seq(MatchedRegions("Region1", Seq("Location1", "Location2")))
    val result = JsonParser.outputToJson(data)
    
    result match {
      case Success(jsonString) => 
        val deserialized = upickle.default.read[Seq[MatchedRegions]](jsonString)
        deserialized shouldEqual data
      case Failure(exception) => 
        fail(s"Serialization failed with exception: $exception")
    }
  }

  it should "correctly serialize a Seq[MatchedRegions] into a JSON string when a region has no matched locations" in {
    val data = Seq(MatchedRegions("Region1", Seq.empty))
    val result = JsonParser.outputToJson(data)
    
    result match {
      case Success(jsonString) => 
        val deserialized = upickle.default.read[Seq[MatchedRegions]](jsonString)
        deserialized shouldEqual data
      case Failure(exception) => 
        fail(s"Serialization failed with exception: $exception")
    }
  }
}
