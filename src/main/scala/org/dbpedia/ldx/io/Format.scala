package org.dbpedia.ldx.io

import org.apache.jena.riot.Lang



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
object Format {
  def getFormatByMimeType(mimeType: String): Option[Format] = {
    val formats = List(RDF_XML(), RDF_NTRIPLES(), RDF_NQUADS(), RDF_JSONLD(), RDF_TURTLE())

    formats.find(format => format.mediaType == mimeType)
  }
}

final case class UnknownFormatException(private val message: String) extends Exception(message)