package io.moat

import java.security.Key
import java.util.Date
import javax.crypto.spec.SecretKeySpec

case class JWT(token: String) extends AnyVal

case class JwtContext(key: Key)

object JwtContext {
  def apply(key: String, algorithm: Algorithms.Algorithm): JwtContext = apply(key.getBytes, algorithm)
  def apply(key: Array[Byte], algorithm: Algorithms.Algorithm): JwtContext = JwtContext(new SecretKeySpec(key, algorithm.jcaName))
}

case class Claims(iss: String, sub: String, aud: String, exp: Date, nbf: Date, iat: Date, jti: String, params: Map[String, AnyRef] = Map[String, AnyRef]())


