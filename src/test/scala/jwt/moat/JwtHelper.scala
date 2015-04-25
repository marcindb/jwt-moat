package jwt.moat

import java.security.Key
import java.util

import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims

import scala.util.Random

object JwtHelper {

  def randomKey(length: Int): Array[Byte] = {
    val random = new Random()
    val key = Array.ofDim[Byte](length)
    random.nextBytes(key)
    key
  }

  def jwtString(key: Key, algorithm: String) = {
    val claims = new JwtClaims
    claims.setIssuer("Issuer")
    //claims.setAudience("Audience");
    claims.setExpirationTimeMinutesInTheFuture(10)
    claims.setGeneratedJwtId()
    claims.setIssuedAtToNow()
    claims.setNotBeforeMinutesInThePast(2)
    claims.setSubject("subject")
    claims.setClaim("email","mail@example.com")
    val groups = util.Arrays.asList("group-one", "other-group", "group-three")
    claims.setStringListClaim("groups", groups)
    val jws = new JsonWebSignature()
    jws.setPayload(claims.toJson)
    jws.setKey(key)
    jws.setAlgorithmHeaderValue(algorithm)
    jws.getCompactSerialization
  }

}
