package org.dbpedia.ldx.http

import org.dbpedia.ldx.LDXConfig

import java.io.InputStream
import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.util.Try

class LDClient {

  private val timeOut = LDXConfig.timeOut
  private val acceptHeader = LDXConfig.acceptHeader

  private val client: HttpClient =
    HttpClient.newBuilder()
      .followRedirects(HttpClient.Redirect.NEVER)
      .version(HttpClient.Version.HTTP_1_1)
      .build()

  def send(uri_str: String): Try[LDResponse] = Try {
    val uri = new URI(uri_str)
    var location = uri
    var httpResponse: HttpResponse[InputStream] = null
    var continue = true
    val responseBuffer = new ArrayBuffer[URI]

    while (continue) {
      httpResponse = singleRequest(location)
      val statusCode = httpResponse.statusCode()
      responseBuffer.append(location)
      if (statusCode >= 300 && statusCode < 400) {
        location = new URI(httpResponse.headers().allValues("location").asScala.head)
      } else {
        continue = false
      }
    }
    LDResponse(uri, httpResponse, responseBuffer.toArray)
  }

  private def singleRequest(uri: URI): HttpResponse[InputStream] = {

    val request =
      HttpRequest.newBuilder(uri)
        .GET()
        .timeout(timeOut)
        .header("accept", acceptHeader)
        .build()

    client.send(request, HttpResponse.BodyHandlers.ofInputStream())
  }
}
