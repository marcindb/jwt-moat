package jwt

import scala.language.implicitConversions


package object moat {

  import Checks._

  def secure[T]()(jwt: JWT)(f: PartialFunction[Either[CheckFailed, Claims], T])(implicit jwtContext: JwtContext): T =
    secure[T](successCheck)(jwt)(f)(jwtContext)

  def secure[T](check: Check)(jwt: JWT)(f: PartialFunction[Either[CheckFailed, Claims], T])(implicit jwtContext: JwtContext): T =
    f(Jose4j(check, jwt, jwtContext))

  def secur[T, U](check: Check)(map: Claims => U)(jwt: JWT)(f: PartialFunction[Either[CheckFailed, U], T])(implicit jwtContext: JwtContext): T =
    f(Jose4j(check, jwt, jwtContext).right.map(map(_)))

}

