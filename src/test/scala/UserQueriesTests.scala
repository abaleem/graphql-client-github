import client.{DataGetter, UserQueries}
import com.typesafe.config.{Config, ConfigFactory}
import models.QueryResult
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.scalatest.FunSuite

class UserQueriesTests extends FunSuite{

  protected implicit val formats = DefaultFormats

  private val config: Config = ConfigFactory.load("hw1_config.conf")
  private val testUserLogin = config.getString("testingParam.testUser ")
  private val testUserNumberRepo = config.getInt("testingParam.testUserNumberRepo")
  private val testUserPrimaryLang = config.getString("testingParam.testUserPrimaryLang")


  /**
   * Tests the user data model by loading the data received into model and checking against user login
   *
   */
  test("Testing Users Data Model"){
    val userQuery = s"""{user(login:$testUserLogin){login}}"""
    val receivedResult = DataGetter.getData(userQuery)
    val user = parse(receivedResult).extract[QueryResult].data.user
    assert(user.login.get == testUserLogin)
  }

  /**
   * Tests the getTotalRepositories function for the user. Values may change over time and can be updated
   * in the config file.
   */
  test("Testing User Queries Function - getTotalRepositories"){
    val userQ = new UserQueries(testUserLogin)
    val numberRepos = userQ.getTotalRepositories()
    assert(numberRepos == testUserNumberRepo)
  }

  /**
   * Tests the getPrimaryLanguage function for the user. Will mostly stay constant but might change.
   * Changes can be updated in the config file.
   */
  test("Testing User Queries Function - getPrimaryLanguage"){
    val userQ = new UserQueries(testUserLogin)
    val primaryLanguage = userQ.getPrimaryLanguage()
    assert(primaryLanguage == testUserPrimaryLang)
  }

}
