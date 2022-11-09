package org.dbpedia.ldx.io.schema

import ujson.Value

import java.net.URI
import java.net.http.HttpClient.{Redirect, Version}
import java.net.http.{HttpClient, HttpRequest}
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration

object SchemaOrg {

  lazy val context: Value = {
    val client = HttpClient.newBuilder()
      .version(Version.HTTP_1_1)
      .followRedirects(Redirect.ALWAYS)
      .build()

    val request = HttpRequest.newBuilder()
      .uri(new URI("https://raw.githubusercontent.com/schemaorg/schemaorg/main/data/releases/14.0/schemaorgcontext.jsonld"))
      .GET()
      .header("Accept", "application/ld+json")
      .timeout(Duration.ofSeconds(30))
      .build()

    val jsonld = client.send(request, BodyHandlers.ofString()).body()
    val contextData = ujson.read(jsonld)
    val res = contextData("@context")
    res
  }

}
