package jwt.moat

import jwt.moat.Checks._


trait JwtParser {

  def parse(check: Check, jwt: JWT, jwtContext: JwtContext): Either[CheckFailed, Claims]

}

object Secure extends JwtParser with JJWT {

  def apply[T](jwt: String)(f: PartialFunction[Either[CheckFailed, Claims], T])(implicit jwtContext: JwtContext): T =
    apply[T](successCheck)(jwt)(f)(jwtContext)

  def apply[T](check: Check)(jwt: String)(f: PartialFunction[Either[CheckFailed, Claims], T])(implicit jwtContext: JwtContext): T =
    f(parse(check, JWT(jwt), jwtContext))

}

