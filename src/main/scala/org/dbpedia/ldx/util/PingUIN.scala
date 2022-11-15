package org.dbpedia.ldx.util


import org.apache.jena.rdf.model.Model
import org.dbpedia.ldx.model.UIN
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

class PingUIN {

  private val log = LoggerFactory.getLogger(classOf[PingUIN])

  private final val sameAsPropertyURIs =
    Set(
      "http://schema.org/sameAs",
      "https://schema.org/sameAs",
      "http://www.w3.org/2002/07/owl#sameAs"
    )

  def findIdentity(requestUIN: UIN, candidates: Array[UIN], model: Model): Option[UIN] = {

    //    val sameAsBuffer = new ListBuffer[(UIN, UIN)]

    val foundURIs = model.listStatements().asScala.flatMap({
      stmt =>
        //        if (sameAsPropertyURIs.contains(stmt.getPredicate.getURI)) {
        //          sameAsBuffer.append((
        //            UIN(stmt.getSubject.getURI),
        //            UIN(stmt.getObject.asResource().getURI)
        //          ))
        //        }
        List(
          UIN(stmt.getSubject.getURI),
          // TODO check object retrieve w.r.t flatMap; is None resolved correctly in the upper flatMap
          {
            if (stmt.getObject.isURIResource)
              UIN(stmt.getObject.asResource().getURI)
            else
              None
          }
        )
    }).toSet

    log.debug(candidates.mkString(" | "))
    log.debug(foundURIs.mkString(" | "))

    candidates.find({
      candidate =>
        foundURIs.exists {
          foundURI =>
            foundURI.equals(candidate)
        }
    })

    // TODO match SOME(uin) and if some then retrieve for this UIN again and check again
  }
}
