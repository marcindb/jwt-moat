package jwt.moat

import java.util.Date

import io.jsonwebtoken.{Claims => JjwtClaims, Jwts}
import org.jose4j.jwt.NumericDate
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object JJWT  {

  val logger = LoggerFactory.getLogger(Jose4j.getClass)

  import Checks._

  def apply(check: Check, jwt: JWT, jwtContext: JwtContext) =
      Try(Jwts.parser().setSigningKey(jwtContext.key).parseClaimsJws(jwt.token)) match {
        case Success(claims) =>
          val c = map(claims.getBody)
          if(check(c)) Right(c) else Left(HasRoleFailed)
        case Failure(e) => Left(HasRoleFailed)
      }


  implicit def map(claims: JjwtClaims): Claims =
    Claims(claims.getIssuer, claims.getSubject , "",
      claims.getExpiration, claims.getNotBefore, claims.getIssuedAt, claims.getId,
      claims.asScala.toMap)

  implicit def map(numericDate: NumericDate): Date = new Date(numericDate.getValueInMillis)

}
