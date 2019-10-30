package client

import models.QueryResult
import org.json4s.jackson.JsonMethods.parse

/**
 * This class handles queries related to users.
 * @param login login handle of Github user
 */
class UserQueries(val login:String) extends ErrorChecker {

  /**
   * Gets the total Number of Repositories that the user has created. Original plus Forked
   * @return the number of total repositories. Int
   */
  def getTotalRepositories(): Int ={
    val query = s"{user(login:$login){repositories{totalCount}}}"
    val queryResult = DataGetter.getData(query)
    // checks if actual data received instead of error message from server.
    if(isErrorFree(queryResult)){
      // parses string and stores data in user model
      val usr = parse(queryResult).extract[QueryResult].data.user
      // gets the number of repositories as received from the server
      val numberOfRepositories = usr.repositories.totalCount.getOrElse(-1)
      numberOfRepositories
    } else -1
  }

  /**
   * Returns the total number of original repositories that the user has created. Fork repositories not included.
   * @return the number of total original repositories. Int
   */
  def getOriginalRepositories(): Int ={
    // gets the total number of repositories from server
    val totalRepos = getTotalRepositories()
    val query = s"{user(login:$login){repositories(last:$totalRepos){nodes{isFork}}}}"
    val queryResult = DataGetter.getData(query)
    // checks if actual data received instead of error message from server.
    if(isErrorFree(queryResult)){
      // parses string and stores data in user model
      val usr = parse(queryResult).extract[QueryResult].data.user
      // filter the repositories based on the isFork flagged. Gets only original ones
      val originalRepos = usr.repositories.nodes.filter(_.isFork.contains(false))
      // returns the number of original repositories left after removing the forked ones.
      val numberOfRepositories = originalRepos.length
      numberOfRepositories
    } else -1
  }

  /**
   * Returns the primary language by taking the mode of primary language of each of users original repositories
   * @return users primary language. String.
   */
  def getPrimaryLanguage(): String ={
    // gets the total number of repositories from server
    val numberOfRepo = getTotalRepositories()
    val query = s"{user(login:$login){repositories(last:$numberOfRepo){nodes{isFork primaryLanguage{name}}}}}"
    val queryResult = DataGetter.getData(query)
    // checks if actual data received instead of error message from server.
    if(isErrorFree(queryResult)){
      // parses string and stores data in user model
      val usr = parse(queryResult).extract[QueryResult].data.user
      // removes all the forked repositories
      val originalRepos = usr.repositories.nodes.filter(_.isFork.contains(false))
      // removes the repositories that might not have any language. can just be a readme B)
      val primaryLang = originalRepos.filter(lang => lang.primaryLanguage.orNull != null)
      // gets the primary language for each repository
      val primaryLangList = primaryLang.map(_.primaryLanguage.get.name.get)
      // finds and returns the mode from the list of primary languages.
      primaryLangList.groupBy(identity).mapValues(_.size).maxBy(_._2)._1
    } else "Error"
  }

  /**
   * Generates a list of all the uniques languages the user know based on his original repositories.
   * @return a list of string containing the unique languages
   */
  def getAllLanguages(): List[String] ={
    // gets the total number of repositories from server
    val numberOfRepo = getTotalRepositories()
    val query = s"{user(login:$login){repositories(last:$numberOfRepo){nodes{isFork languages(first:10){nodes{name}}}}}}"
    val queryResult = DataGetter.getData(query)
    // checks if actual data received instead of error message from server.
    if(isErrorFree(queryResult)){
      val usr = parse(queryResult).extract[QueryResult].data.user
      // removes all the forked repositories
      val originalRepos = usr.repositories.nodes.filter(_.isFork.contains(false))
      // removes the repositories that might not have any language. can just be a readme B)
      val reposWithLangs = originalRepos.filter(rep => rep.languages.nodes.nonEmpty)
      // extracts a list of list of all languages that user have worked with
      val reposAllLang = reposWithLangs.map(rep => (rep.languages.nodes.map(_.name.get)))
      // removes duplicates and returns a unique list of languages what user has worked on
      val allLanguages = reposAllLang.flatten.distinct
      allLanguages
    } else List()
  }

}
