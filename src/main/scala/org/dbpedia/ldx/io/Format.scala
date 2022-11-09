package org.dbpedia.ldx.io

sealed trait Format {

  val mediaType: String

  case class RDF_NTRIPLES() extends Format {
    override val mediaType: String = "application/n-triples"
  }

  case class RDF_TURTLE() extends Format {
    override val mediaType: String = "text/turtle"
  }

  case class RDF_XML() extends Format {
    override val mediaType: String =   "application/rdf+xml"
  }

  case class RDF_JSONLD() extends Format {
    override val mediaType: String = "application/ld+json"
  }

//  case class RDF_NQUADS() extends Format
}
