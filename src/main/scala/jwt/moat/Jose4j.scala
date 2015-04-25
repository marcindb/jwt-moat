package jwt.moat

import java.util.Date

import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.jwt.{JwtClaims, NumericDate}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object Jose4j  {

  val logger = LoggerFactory.getLogger(Jose4j.getClass)

  import Checks._

  def apply(check: Check, jwt: JWT, jwtContext: JwtContext) = {
    val jwtConsumer = new JwtConsumerBuilder()
      .setRequireExpirationTime() // the JWT must have an expiration time
      .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
      .setRequireSubject() // the JWT must have a subject claim
      //.setExpectedAudience("Audience") // to whom the JWT is intended for
      .setVerificationKey(jwtContext.key) // verify the signature with the public key
      .build()

    Try(jwtConsumer.processToClaims(jwt.token)) match {
      case Success(claims) =>
        val c = map(claims)
        if(check(c)) Right(c) else Left(HasRoleFailed)
      case Failure(e) => Left(HasRoleFailed)
    }
  }

  implicit def map(jwtClaims: JwtClaims): Claims =
    Claims(jwtClaims.getIssuer, jwtClaims.getSubject , "",
      jwtClaims.getExpirationTime, jwtClaims.getNotBefore, jwtClaims.getIssuedAt, jwtClaims.getJwtId,
      jwtClaims.getClaimsMap.asScala.toMap)

  implicit def map(numericDate: NumericDate): Date = new Date(numericDate.getValueInMillis)

}
