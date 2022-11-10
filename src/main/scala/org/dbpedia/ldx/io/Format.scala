package org.dbpedia.ldx.io

import org.apache.jena.riot.Lang

import scala.collection.immutable.HashMap
import scala.collection.mutable



sealed trait Format {

  val mediaType: String
  val jenaLang: Lang = Lang.NTRIPLES
}
case class RDF_NTRIPLES() extends Format {
  override val mediaType: String = "application/n-triples"
  override val jenaLang: Lang = Lang.NTRIPLES
}

case class RDF_TURTLE() extends Format {
  override val mediaType: String = "text/turtle"
  override val jenaLang: Lang = Lang.TURTLE
}

case class RDF_XML() extends Format {
  override val mediaType: String = "application/rdf+xml"
  override val jenaLang: Lang = Lang.RDFXML
}

case class RDF_JSONLD() extends Format {
  override val mediaType: String = "application/ld+json"
  override val jenaLang: Lang = Lang.JSONLD
}

case class RDF_NQUADS() extends Format {
  override val mediaType: String = "application/n-quads"
  override val jenaLang: Lang = Lang.NQUADS
}

case class RDF_TRIG() extends Format {
  override val mediaType: String = "application/trig"
  override val jenaLang: Lang = Lang.TRIG
}
object Format {

  val rdfMimeTypes: HashMap[String, Format] = HashMap(
    "application/n-triples" -> RDF_NTRIPLES(),
    "text/turtle" -> RDF_TURTLE(),
    "application/rdf+xml" -> RDF_XML(),
    "application/n-quads" -> RDF_NQUADS(),
    "application/trig" -> RDF_TRIG(),
    "application/ld+json" -> RDF_JSONLD()
  )
}

final case class UnknownFormatException(private val message: String) extends Exception(message)