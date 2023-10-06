import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.nio.file.{Files, Paths}

class ArgumentParserSpec extends AnyFlatSpec with Matchers {

  "parseArgs" should "correctly parse command-line arguments into a map" in {
    val args = Array("--regions=regions.json", "--locations=locations.json", "--output=results.json")
    val expectedMap = Map("regions" -> "regions.json", "locations" -> "locations.json", "output" -> "results.json")
    val result = ArgumentParser.parseArgs(args)
    result shouldBe expectedMap
  }

  it should "ignore arguments that are not in key=value format" in {
    val args = Array("--regions", "locations.json", "--output=results.json")
    val expectedMap = Map("output" -> "results.json")
    val result = ArgumentParser.parseArgs(args)
    result shouldBe expectedMap
  }

  it should "strip '--' prefix from keys" in {
    val args = Array("--key=value")
    val expectedMap = Map("key" -> "value")
    val result = ArgumentParser.parseArgs(args)
    result shouldBe expectedMap
  }

  it should "print a warning if a file path does not exist" in {
    val args = Array("--regions=nonExistingFile.json", "--locations=locations.json")
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      ArgumentParser.parseArgs(args)
    }
    val output = stream.toString
    output should include ("File does not exist: nonExistingFile.json for key: regions")
  }
}