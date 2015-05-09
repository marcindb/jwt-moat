package io.moat

import io.jsonwebtoken.{Claims => JjwtClaims, Jwts}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

trait JJWT extends JwtParser {

  self =>

  val logger = LoggerFactory.getLogger(self.getClass)

  import Checks._

  override def parse(check: Check, jwt: JWT, jwtContext: JwtContext): Either[CheckFailed, Claims] =
      Try(Jwts.parser().setSigningKey(jwtContext.key).parseClaimsJws(jwt.token)) match {
        case Success(claims) =>
          val c = map(claims.getBody)
          if(check(c)) Right(c) else Left(HasRoleFailed)
        case Failure(e) => Left(HasRoleFailed)
      }

  implicit def map(claims: JjwtClaims): Claims =
    Claims(claims.getIssuer, claims.getSubject , claims.getAudience,
      claims.getExpiration, claims.getNotBefore, claims.getIssuedAt, claims.getId,
      claims.asScala.toMap)

}
