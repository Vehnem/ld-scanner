package org.dbpedia.ldx.model

import java.net.URI
import scala.annotation.tailrec

/**
 * Uniform Resource Identifier Normalized
 */
case class UIN(private val uri: URI) {

  lazy val uin: URI = uri.normalize()

  override def toString: String = uin.toString

  override def equals(obj: Any): Boolean = {
    obj match {
      case uin2: UIN =>
        uin2.toString == toString
      case _ => false
    }
  }
}

object UIN {

  def apply(uri: String): UIN = {
    UIN(new URI(uri))
  }
}

