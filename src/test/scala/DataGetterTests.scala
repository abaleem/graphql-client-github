import client.{DataGetter}
import com.typesafe.config.{Config, ConfigFactory}
import org.json4s.DefaultFormats
import org.scalatest.FunSuite

class DataGetterTests extends FunSuite{

  protected implicit val formats = DefaultFormats

  private val config: Config = ConfigFactory.load("hw1_config.conf")
  private val testUserLogin = config.getString("testingParam.testUser ")

  /**
   * Tests the data getter object. by checking against the expected result for a query with any username.
   */
  test("Checking Data Getter"){
    val userQuery = s"""{user(login:$testUserLogin){login}}"""
    val expectedResult = s"""{"data":{"user":{"login":"$testUserLogin"}}}"""
    val receivedResult = DataGetter.getData(userQuery)
    assert(expectedResult == receivedResult)
  }

}
