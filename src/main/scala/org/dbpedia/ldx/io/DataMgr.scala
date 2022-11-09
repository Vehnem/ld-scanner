package org.dbpedia.ldx.io

import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.jsoup.Jsoup
import ujson.Value

import java.io.{InputStream, StringReader}
import scala.collection.JavaConverters._

class DataMgr {

  //  def fetch(uri: String): Model = {
  //
  //    val client = HttpClient.newBuilder()
  //      .version(Version.HTTP_1_1)
  //      .followRedirects(Redirect.ALWAYS)
  //      .build()
  //
  //    val request = HttpRequest.newBuilder()
  //      .uri(new URI(uri))
  //      .GET()
  //      .header("Accept", config.ldAcceptHeader)
  //      .timeout(Duration.ofSeconds(30))
  //      .build()
  //
  //    val response_raw = client.send(request, BodyHandlers.ofInputStream())
  //    val contentType = response_raw.headers().allValues("content-type").asScala.head
  //    val mimeTypeWoParam = contentType.split(";").head
  //
  //    if (config.rdfMimeTypes.contains(mimeTypeWoParam)) {
  //      // RDF format
  //      parse(response_raw.body(), config.langByMimeType(mimeTypeWoParam))
  //    } else {
  //      // MicroData and JSON+LD
  //      parseEmbedded(response_raw.body(), uri)
  //    }
  //  }

  def parseEmbedded(inputStream: InputStream, baseUri: String): Model = {
    val document = Jsoup.parse(inputStream, null, "")
    val m = ModelFactory.createDefaultModel()
    document.select("script[type=application/ld+json]").iterator().asScala.foreach({
      ele =>
        val jsonld = ele.html
        val data: Value.Value = ujson.read(jsonld)
        //        data("@context") = schema_org_context
        //        Value.Selector.StringSelector("@context").update()
        try {
          data("@id") = data("url")
        } catch {
          case _: Throwable =>
            println("could not perform 'data(@id) = data(url)'")
        }
        val dataWithBase =
          data.render().replaceFirst("\\{\"type", "{\"@base\": \"" + baseUri + "\", \"type")
        println(dataWithBase)
        RDFDataMgr.read(m, new StringReader(dataWithBase), baseUri, Lang.JSONLD)
    })
    m
  }

  def parse(inputStream: InputStream, lang: Lang): Model = {
    val m = ModelFactory.createDefaultModel()
    RDFDataMgr.read(m, inputStream, lang)
    m
  }
}
