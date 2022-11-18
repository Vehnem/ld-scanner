package org.dbpedia.ldx.util

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.apache.jena.vocabulary.{OWL, RDF}
import org.dbpedia.ldx.model.UIN
import org.scalatest.funsuite.AnyFunSuite

import java.io.{BufferedInputStream, ByteArrayInputStream}
import java.nio.charset.StandardCharsets

class PingUINTest extends AnyFunSuite {


  private val ntriples =
    s"""<http://dbpedia.org/resource/Berlin> <${OWL.sameAs.getURI}> <http://de.dbpedia.org/resource/Berlin> .
       |<http://dbpedia.org/resource/Berlin> <${RDF.`type`.getURI}> <http://dbpedia.org/ontology/City> .
       |""".stripMargin

  private val model = ModelFactory.createDefaultModel()
  RDFDataMgr.read(
    model,
    new BufferedInputStream(
      new ByteArrayInputStream(ntriples.getBytes(StandardCharsets.UTF_8)),
    ),
    Lang.NTRIPLES
  )

  test("some identity") {
    val pingUIN = new PingUIN()
    val uin = UIN("http://dbpedia.org/resource/Berlin")
    val optUIN = pingUIN.findIdentity(uin, Array(uin), model)
    assert(optUIN.isDefined, "no identity found")
  }

  test("no identity") {

  }
}
