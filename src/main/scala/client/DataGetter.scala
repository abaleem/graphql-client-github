package client

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.LoggerFactory
import scala.io.Source

object DataGetter {

  private val LOG_TAG = Logger(LoggerFactory.getLogger("DataGetter"))

  private val config: Config = ConfigFactory.load("hw1_config.conf")
  private val GITHUB_ENDPOINT = config.getString("github.ENDPOINT")
  private val ACCESS_TOKEN = config.getString("github.ACCESS_TOKEN")

  /**
   * Takes a query properly formatted and returns the result received from the server.
   * @param query input query for github graphql api (v4)
   * @return the response to that query in string format.
   */

  def getData(query: String): String ={

    // adding borders to query. adding it here to make sure the programs only type minimal query.
    val queryToSend = s"""{"query":"$query"}"""

    // client that executes the query
    val client = HttpClientBuilder.create.build

    // building the http request
    val httpUriRequest = new HttpPost(GITHUB_ENDPOINT)
    httpUriRequest.addHeader("Authorization", s"Bearer $ACCESS_TOKEN")
    httpUriRequest.addHeader("Accept", "application/json")
    val gqlReq = new StringEntity(queryToSend)
    httpUriRequest.setEntity(gqlReq)

    // sending query and receiving response
    LOG_TAG.info(s"Sending Query = $queryToSend")
    val response = client.execute(httpUriRequest)
    LOG_TAG.info(s"Response Received = $response")

    // checking if a response received.
    if(response.getEntity != null){

      val respString = Source.fromInputStream(response.getEntity.getContent).getLines.mkString

      // response might not be null but still invalid hence checking status code.
      response.getStatusLine.getStatusCode match{
        // 200 means that we received. The response could be our data or some error with our query.
        case 200 => {
          LOG_TAG.info(s"Data Fetched = $respString")
          respString
        }
        // we can also receive a bad request if the query cant be interpreted by the server
        case statusCode if statusCode != 200 => {
          LOG_TAG.info(s"Data fetch not successful. Status code = $statusCode")
          respString
        }
      }
    } else{
      LOG_TAG.error("Response entity is null")
      "Error"
    }
  }
}
