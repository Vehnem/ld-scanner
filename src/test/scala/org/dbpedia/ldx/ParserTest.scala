package org.dbpedia.ldx

import org.dbpedia.ldx.io.DataMgr
import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import scala.util.{Failure, Success}

class ParserTest extends AnyFunSuite {

  test("DataMgrWorking.fetch") {

    val fileTypes = Array("nt", "owl", "ttl")

    for (fileEnding <- fileTypes) {
      val resp = DataMgr.fetch(s"https://archivo.dbpedia.org/download?o=http%3A//advene.org/ns/cinelab/ld&f=${fileEnding}&v=2020.06.10-175249")
      resp match {
        case Success(model) =>
          println(s"Fetched ${fileEnding} for Cinelab Ontology successfully: ${model.size()} Triples")
          assert(model.size() > 0)
        case Failure(exception) => throw exception
      }
    }

    val resp = DataMgr.fetch("https://www.imdb.com/title/tt4263482/")
    resp match {
      case Success(model) =>
        println(s"Fetched Embedded JSONLD file successfully: ${model.size()} Triples")
        assert(model.size() > 0)
      case Failure(exception) => throw exception
    }

  }
}
