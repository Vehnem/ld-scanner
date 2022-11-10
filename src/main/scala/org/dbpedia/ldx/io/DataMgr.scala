package org.dbpedia.ldx.io

import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.dbpedia.ldx.LDXConfig
import org.jsoup.Jsoup
import ujson.Value

import java.io.{File, FileInputStream, InputStream, StringReader}
import scala.collection.JavaConverters._
import org.dbpedia.ldx.http.LDClient

import scala.util.{Failure, Success, Try}

object DataMgr {

  def fetch(uri: String): Try[Model] = {

    val client = new LDClient()
    val ldResponse = client.send(uri)

    ldResponse match {
      case Success(ld_resp) =>
        val contentType = ld_resp.finalHttpResponse.headers().allValues("content-type").asScala.head
        val mimeType = contentType.split(";").head.trim
        Format.rdfMimeTypes.get(mimeType) match {
          case Some(format) => parse(ld_resp.finalHttpResponse.body(), format)
          case None => parseEmbedded(ld_resp.finalHttpResponse.body(), uri)
        }
      case Failure(exception) => Failure(exception)
    }
  }

  def load(file: File, format: Format): Try[Model] = {
    val fis = new FileInputStream(file)
    parse(fis, format)
  }

  def parseEmbedded(inputStream: InputStream, baseUri: String): Try[Model] = Try {
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
        //println(dataWithBase)
        RDFDataMgr.read(m, new StringReader(dataWithBase), baseUri, Lang.JSONLD)
    })
    m
  }

  def parse(inputStream: InputStream, format: Format): Try[Model] = Try {
    val m = ModelFactory.createDefaultModel()
    RDFDataMgr.read(m, inputStream, format.jenaLang)
    m
  }
}
