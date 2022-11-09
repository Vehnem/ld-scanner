package org.dbpedia.ldx.http

import java.io.InputStream
import java.net.URI
import java.net.http.HttpResponse

case class LDResponse(
  requestURI: URI,
  finalHttpResponse: HttpResponse[InputStream],
  redirectChain: Array[URI] = Array[URI]()
)