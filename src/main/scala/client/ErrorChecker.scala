package client

import com.typesafe.scalalogging.Logger
import models.{ErrorList, QueryErrors}
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.slf4j.LoggerFactory

/**
 * This checks that response received contains actually the data we need. If also prints out error message otherwise.
 * This class is inherited by other query classes.
 */
class ErrorChecker {

  protected implicit val formats = DefaultFormats
  private val LOG_TAG = Logger(LoggerFactory.getLogger("ErrorChecker"))

  /**
   *
   * @param queryResult result received from the github server. String.
   * @return returns true if no error message send by the server and false otherwise.
   */
  def isErrorFree(queryResult: String): Boolean ={
    // checks to see if the error class populated. only done when error received.
    val output = parse(queryResult).extract[QueryErrors]
    val error = output.errors
    _logError(error)
    if(error.nonEmpty) false else true
  }

  /**
   *
   * @param queryResult result received from the github server. String.
   * @return returns a list of all errors send by the server
   */
  def getError(queryResult: String): List[String] ={
    val output = parse(queryResult).extract[QueryErrors]
    val error = output.errors
    error.map(_.message.get)
  }

  /**
   *
   * @param error list of error objects to log and print.
   */
  private def _logError(error: List[ErrorList])={
    error.foreach(err => LOG_TAG.error("Error in query result = " + err.message.get))
  }

}
