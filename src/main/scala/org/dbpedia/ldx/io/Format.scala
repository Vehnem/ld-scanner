package org.dbpedia.ldx.io

import org.apache.jena.riot.Lang

import scala.collection.immutable.HashMap
import scala.collection.mutable


sealed trait Format {

  val mediaType: String
  val jenaLang: Lang
  val name: String
}

object Format {
  case class RDF_NTRIPLES() extends Format {
    override val mediaType: String = "application/n-triples"
    override val jenaLang: Lang = Lang.NTRIPLES
    override val name: String = "ntriples"
  }

  case class RDF_TURTLE() extends Format {
    override val mediaType: String = "text/turtle"
    override val jenaLang: Lang = Lang.TURTLE
    override val name: String = "turtle"
  }

  case class RDF_XML() extends Format {
    override val mediaType: String = "application/rdf+xml"
    override val jenaLang: Lang = Lang.RDFXML
    override val name: String = "rdfxml"
  }

  case class RDF_JSONLD() extends Format {
    override val mediaType: String = "application/ld+json"
    override val jenaLang: Lang = Lang.JSONLD
    override val name: String = "jsonld"
  }

  case class RDF_TRIG() extends Format {
    override val mediaType: String = "application/trig"
    override val jenaLang: Lang = Lang.TRIG
    override val name: String = "trig"
  }
  case class RDF_NQUADS() extends Format {
    override val mediaType: String = "application/n-quads"
    override val jenaLang: Lang = Lang.NQUADS
    override val name: String = "nquads"
  }

  import scala.reflect.runtime.{universe => ru}

  lazy val mimetoFormat: Map[String, Format] = getAllImplementationsOfSymbol[Format].map(
    format => (format.mediaType, format)
  ).toMap

  lazy val formatNameToFormat: Map[String, Format] = getAllImplementationsOfSymbol[Format].map(
    format => (format.name, format)
  ).toMap

  private def getAllImplementationsOfSymbol[T: ru.TypeTag]: Set[T] = {

    val subclassesOfTrait = ru.typeOf[T].typeSymbol.asClass.knownDirectSubclasses
    subclassesOfTrait.map(subclass => getInstanceOfClassSymbol(subclass.asClass).asInstanceOf[T])
  }

  private def getInstanceOfClassSymbol(classSymbol: ru.ClassSymbol): Any = {
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val tpe = classSymbol.info
    val classMirror = mirror.reflectClass(classSymbol)
    val constructorMirror = classMirror.reflectConstructor(tpe.decl(ru.termNames.CONSTRUCTOR).asMethod)

    constructorMirror()
  }
}

final case class UnknownFormatException(private val message: String) extends Exception(message)