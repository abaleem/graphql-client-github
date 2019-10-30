package main.scala

import client.{RepositoryQueries, SearchQueries, UserQueries}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory


object Main {
  def main(args: Array[String]): Unit = {

    // Loading Config file for main demo
    val config: Config = ConfigFactory.load("hw1_config.conf")
    val userLogin = config.getString("mainDemo.userLogin")
    val repositoryOwner = config.getString("mainDemo.repositoryOwner")
    val repositoryName = config.getString("mainDemo.repositoryName")
    val dueDate = config.getString("mainDemo.dueDate")
    val dueTime = config.getString("mainDemo.dueTime")
    val searchParam = config.getString("mainDemo.searchParam")


    // Logging
    val LOG_TAG = Logger(LoggerFactory.getLogger("Main"))
    LOG_TAG.info("Running all the functions created as a sample.")


    // UserQueries functionality with some owner and repository
    val usrQ = new UserQueries(userLogin)

    val totalRepo = usrQ.getTotalRepositories()
    println(s"Total repositories for $userLogin = $totalRepo")

    val originalRepo = usrQ.getOriginalRepositories()
    println(s"Original repositories for $userLogin = $originalRepo")

    val primaryLang = usrQ.getPrimaryLanguage()
    println(s"Main language $userLogin uses = $primaryLang")

    val allLangs = usrQ.getAllLanguages()
    println(s"All language $userLogin uses = $allLangs")


    // RepositoryQueries functionality with some owner and repository
    val repoQ = new RepositoryQueries(repositoryOwner, repositoryName)

    val numberOfLanguagesUsed = repoQ.getNumberOfLanguagesUsed()
    println(s"Number of languages used in the repository $repositoryName = $numberOfLanguagesUsed")

    val languagesUsed = repoQ.getUsedLanguages()
    println(s"List of languages used in $repositoryName= $languagesUsed")

    val lastForksUsers = repoQ.getLastForks(10)
    println(s"Last Users that forked the repository $repositoryName = $lastForksUsers")

    val lateUpdaters = repoQ.checkLateForkUpdates(dueDate, dueTime)
    println(s"$repositoryName forkers that updated after the $dueDate $dueTime = $lateUpdaters")


    // SearchQueries functionality with some owner and repository
    val srchQ = new SearchQueries()

    val mostUsedLangsForSearch = srchQ.mostUsedLanguage(searchParam)
    print(s"Most used languages for search param: $searchParam = $mostUsedLangsForSearch")

  }
}