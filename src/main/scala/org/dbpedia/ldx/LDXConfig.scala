package org.dbpedia.ldx

import java.time.Duration
import scala.collection.immutable.HashMap

object LDXConfig {

  val timeOut: Duration = Duration.ofSeconds(30)

  val acceptHeader: String =
      "application/n-triples;q=1," +
      "text/turtle;q=0.9," +
      "application/rdf+xml;q=0.7," +
      "application/trig,application/n-quads;q=0.9," +
      "application/ld+json;q=0.8," +
      "*/*;q=0.5"
}
