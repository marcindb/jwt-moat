package jwt.moat

import jwt.moat.Checks._


trait JwtParser {

  def parse(check: Check, jwt: JWT, jwtContext: JwtContext): Either[CheckFailed, Claims]

}

object SecureAction extends JwtParser with JJWT {

  def apply[T]()(jwt: JWT)(f: PartialFunction[Either[CheckFailed, Claims], T])(implicit jwtContext: JwtContext): T =
    apply[T](successCheck)(jwt)(f)(jwtContext)

  def apply[T](check: Check)(jwt: JWT)(f: PartialFunction[Either[CheckFailed, Claims], T])(implicit jwtContext: JwtContext): T =
    f(parse(check, jwt, jwtContext))

}

