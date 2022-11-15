package org.dbpedia.ldx.io

import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.dbpedia.ldx.LDXConfig
import org.dbpedia.ldx.model.UIN
import org.jsoup.Jsoup
import ujson.Value

import java.io.{ByteArrayInputStream, File, FileInputStream, InputStream, OutputStream, Reader, StringReader}
import scala.collection.JavaConverters._
import org.dbpedia.ldx.http.{LDClient, LDResponse}

import java.net.URI
import java.nio.charset.StandardCharsets
import scala.util.{Failure, Success, Try}

object DataMgr {

  def fetchWithResponse(uin: UIN): (LDResponse, Try[Model]) = {
    val client = new LDClient()

    client.send(uin.toString) match {
      case Success (ld_resp) => (ld_resp, parseLDResponse(ld_resp))
      case Failure (exception) => throw exception
    }
  }

  def fetch(uri: String): Try[Model] = {

    val client = new LDClient()

    client.send(uri) match {
      case Success(ld_resp) => parseLDResponse(ld_resp)
      case Failure(exception) => Failure(exception)
    }
  }

  private def parseLDResponse(ldResponse: LDResponse): Try[Model] = {
    val contentType = ldResponse.finalHttpResponse.headers().allValues("content-type").asScala.head
    val mimeType = contentType.split(";").head.trim
    Format.mimetoFormat.get(mimeType) match {
      case Some(format) => parse(ldResponse.finalHttpResponse.body(), format, ldResponse.requestURI.toString)
      case None => parseEmbedded(ldResponse.finalHttpResponse.body(), ldResponse.redirectChain.head)
    }
  }

  def load(file: File, format: Format): Try[Model] = {
    val fis = new FileInputStream(file)
    parse(fis, format, file.getPath)
  }

  def parseEmbedded(inputStream: InputStream, baseUri: URI): Try[Model] = Try {
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
        RDFDataMgr.read(m, new StringReader(dataWithBase), baseUri.toString, Format.RDF_JSONLD().jenaLang)
    })
    m
  }

  def parse(inputStream: InputStream, format: Format, base: String = null): Try[Model] = Try {
    val m = ModelFactory.createDefaultModel()
    base match {
      case null => RDFDataMgr.read(m, inputStream, format.jenaLang)
      case _: String => RDFDataMgr.read(m, inputStream, base, format.jenaLang)
    }
    m
  }

  def parseReader(reader: Reader, format: Format, base: String = null): Try[Model] = Try {
    val m = ModelFactory.createDefaultModel()
    RDFDataMgr.read(m, reader, base, format.jenaLang)
    m
  }

  def write(model: Model, os: OutputStream, format: Format): Unit = {
    RDFDataMgr.write(os, model, format.jenaLang)
  }
}
