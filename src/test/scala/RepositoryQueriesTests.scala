import client.{DataGetter, RepositoryQueries, UserQueries}
import com.typesafe.config.{Config, ConfigFactory}
import models.QueryResult
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.scalatest.FunSuite

class RepositoryQueriesTests extends FunSuite{

  protected implicit val formats = DefaultFormats

  private val config: Config = ConfigFactory.load("hw1_config.conf")
  private val testUserLogin = config.getString("testingParam.testUser ")
  private val testRepoName = config.getString("testingParam.testRepo ")
  private val testRepoLangsUsed = config.getInt("testingParam.testRepoLangsUsed")


  /**
   * Tests the repository data model by loading the data received into model and checking against repository name
   *
   */
  test("Testing Repository Data Model "){
    val repoQuery = s"""{repository(owner:$testUserLogin, name:$testRepoName){name}}"""
    val receivedResult = DataGetter.getData(repoQuery)
    val repo = parse(receivedResult).extract[QueryResult].data.repository
    assert(repo.name.get == testRepoName)
  }

  /**
   * Tests the getNumberOfLanguagesUsed function by the RepositoryQueries class. I tested it against my
   * own repository machlearn where I used 2 languages. This wont be ever updated so wont been to change the params but
   * can be done from the config file. make sure to change the owner, name and no of languages.
   */
  test("Testing RepositoryQueries Function - getNumberOfLanguagesUsed"){
    val repoQ = new RepositoryQueries(testUserLogin, testRepoName)
    val langsUsedQueried = repoQ.getNumberOfLanguagesUsed()
    assert(langsUsedQueried == testRepoLangsUsed)
  }



}
