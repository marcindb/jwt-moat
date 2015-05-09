package jwt.moat

import java.util.Date

import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.jwt.{JwtClaims, NumericDate}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

trait Jose4j extends JwtParser {

  self =>

  val logger = LoggerFactory.getLogger(self.getClass)

  import Checks._

  override def parse(check: Check, jwt: JWT, jwtContext: JwtContext): Either[CheckFailed, Claims] = {
    val jwtConsumer = new JwtConsumerBuilder()
      .setRequireExpirationTime()
      .setAllowedClockSkewInSeconds(30)
      .setRequireSubject()
      //.setExpectedAudience("Audience") // to whom the JWT is intended for
      .setVerificationKey(jwtContext.key)
      .build()

    Try(jwtConsumer.processToClaims(jwt.token)) match {
      case Success(claims) =>
        val c = map(claims)
        if(check(c)) Right(c) else Left(HasRoleFailed)
      case Failure(e) => Left(HasRoleFailed)
    }
  }

  private implicit def map(jwtClaims: JwtClaims): Claims =
    Claims(jwtClaims.getIssuer, jwtClaims.getSubject , "",
      jwtClaims.getExpirationTime, jwtClaims.getNotBefore, jwtClaims.getIssuedAt, jwtClaims.getJwtId,
      jwtClaims.getClaimsMap.asScala.toMap)

  private implicit def map(numericDate: NumericDate): Date = new Date(numericDate.getValueInMillis)

}
