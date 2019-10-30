package client

import models.QueryResult
import org.json4s.jackson.JsonMethods.parse

/**
 * This class handles queries related to search. For repositories.
 */
class SearchQueries extends ErrorChecker {

  /**
   * Returns the most popular languages amongst the first 100 search results for a query.
   * @param searchParam search parameter to search popular languages againt
   * @return map of languages and the number of times they were used in the last 100 search results
   */
  def mostUsedLanguage(searchParam: String): Seq[(String, Int)] ={
    // query and result received.
    val query = s"{search(query:$searchParam, type:REPOSITORY, first:100) {nodes{... on Repository{languages(first:10){nodes{name}}}}}}"
    val queryResult = DataGetter.getData(query)
    // checks if actual data received instead of error message from server.
    if(isErrorFree(queryResult)){
      // parses string and stores data in model
      val srch = parse(queryResult).extract[QueryResult].data.search
      // extracts the repos that have some language used.
      val reposWithLangs = srch.nodes.filter(rep => rep.languages.nodes.nonEmpty)
      // generates a list of list all languages used for for each repo
      val reposAllLang = reposWithLangs.map(rep => rep.languages.nodes.map(_.name.get))
      // flattens the list of list.
      val allLanguages = reposAllLang.flatten
      // sorts the languages according to descending order of times they were used.
      val allLanguagesSortedMap = allLanguages.groupBy(identity).mapValues(_.size).toSeq.sortWith(_._2 > _._2)
      allLanguagesSortedMap
    } else List()

  }
}
