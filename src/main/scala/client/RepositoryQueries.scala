package client

import java.time._

import com.typesafe.scalalogging.Logger
import models.QueryResult
import org.json4s.jackson.JsonMethods.parse
import org.slf4j.LoggerFactory

/**
 * This class handles queries related to repositories.
 * @param owner owner is the handle for the repository owner
 * @param name name is the name of the repository
 */
class RepositoryQueries(val owner:String, val name:String) extends ErrorChecker {

  private val LOG_TAG = Logger(LoggerFactory.getLogger("RepositoryQueries"))

  /**
   * It returns a list of (number) of handles of users who forked the repository last.
   * @param number number of last forks to see
   * @return returns the list of user handles who last forked the the repository
   */
  def getLastForks(number: Int): List[String] ={
    // make sures that number provided by the user lies within range of api requirement. Sets to default value of 10 if doesn't
    val numberVerified = _numberVerify(number)
    val query = s"{repository(owner:$owner, name:$name){isFork forks(last: $numberVerified){totalCount nodes{owner{login}}}}}"
    val queryResult = DataGetter.getData(query)
    // checks if actual data received instead of error message from server.
    if(isErrorFree(queryResult)){
      // parses string and stores data in model
      val resp = parse(queryResult).extract[QueryResult].data.repository
      // maps the list of repositories to handle of owners
      val forkersList = resp.forks.nodes.map(_.owner.login.get)
      forkersList
    } else List()
  }

  /**
   * Number of languages used ina repository
   * @return the number of languages. Int.
   */
  def getNumberOfLanguagesUsed(): Int ={
    val query = s"{repository(owner:$owner, name:$name){languages{totalCount}}}"
    val queryResult = DataGetter.getData(query)
    // checks if actual data received instead of error message from server.
    if(isErrorFree(queryResult)){
      // parses string and stores data in model
      val resp = parse(queryResult).extract[QueryResult].data.repository
      // gets total number of languages as received from the server.
      val numberOfLanguages = resp.languages.totalCount.getOrElse(-1)
      numberOfLanguages
    } else -1
  }

  /**
   * List of all languages used for a certain repository
   * @return the list of all the languages. List[String]
   */
  def getUsedLanguages(): List[String] ={
    // uses the getNumberOfLanguagesUsed function to know how many languages node to query for
    val numberOfLanguages = getNumberOfLanguagesUsed()
    val query = s"{repository(owner:$owner, name:$name){languages(first:$numberOfLanguages) {nodes {name}}}}"
    val queryResult = DataGetter.getData(query)
    // checks if actual data received instead of error message from server.
    if(isErrorFree(queryResult)){
      // parses string and stores data in model
      val resp = parse(queryResult).extract[QueryResult].data.repository
      // maps the list of language data model to language name
      val languageList = resp.languages.nodes.map(_.name.getOrElse("Error"))
      languageList
    }
    else List()
  }

  /**
   * Checks which forks have been updated after a specific date. Can be used to check for late submissions.
   * @param dueDate due date for the last update on fork. Date and time is in Zulu Time
   * @param dueTime due time for the last update on fork. Date and time is in Zulu Time.
   * @return a list all usernames that forked the library and updates after the deadline
   */
  def checkLateForkUpdates(dueDate:String, dueTime:String): List[String] = {
    // converts input strings for dueDate and dueTime into a date format.
    val deadlineDataTime = ZonedDateTime.parse(s"${dueDate}T${dueTime}Z")
    val query = s"{repository(owner:$owner, name:$name){forks(last: 100){nodes{updatedAt owner{login}}}}}"
    val queryResult = DataGetter.getData(query)
    // checks if actual data received instead of error message from server.
    if(isErrorFree(queryResult)){
      // parses string and stores data in model
      val resp = parse(queryResult).extract[QueryResult].data.repository
      // filters that forks that have been updated after the due date
      val afterDeadlineForks = resp.forks.nodes.filter(fork => ZonedDateTime.parse(fork.updatedAt.get).isAfter(deadlineDataTime))
      // maps the forks repository to login handle of the user
      val afterDeadlineUser = afterDeadlineForks.map(_.owner.login.get)
      afterDeadlineUser
    } else List()
  }

  /**
   *
   * @param number verifies that number is within range of 0 to 100. Sets to 10 if otherwise.
   * @return returns the number itself if within range, otherwise return default value of 10.
   */
  private def _numberVerify(number: Int): Int ={
    if(number>0 && number<=100) {
      number
    } else {
      LOG_TAG.warn("Invalid Number. Should be between 0 and 100. Setting Default to 10")
      10
    }
  }
}
