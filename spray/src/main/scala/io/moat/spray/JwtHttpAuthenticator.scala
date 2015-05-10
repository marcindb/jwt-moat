package io.moat.spray

import io.moat.{Claims, Secure, JwtContext}
import spray.http.HttpHeaders.`WWW-Authenticate`
import spray.http._
import spray.routing.RequestContext
import spray.routing.authentication._

import scala.concurrent.{Future, ExecutionContext}

class JwtHttpAuthenticator(val realm: String)(implicit val jwtContext: JwtContext, val executionContext: ExecutionContext)
  extends HttpAuthenticator[Claims] {

  def authenticate(credentials: Option[HttpCredentials], ctx: RequestContext) = {
    Future.successful(credentials.flatMap {
      case OAuth2BearerToken(token) => Some(token)
      case _ => None
    }.flatMap {
      Secure(_) {
        case Right(user) => Some(user)
        case Left(e) => None
      }
    })
  }

  def getChallengeHeaders(httpRequest: HttpRequest) =
    `WWW-Authenticate`(HttpChallenge("Bearer", realm, params = Map.empty)) :: Nil

}

object JwtAuth {

  def apply(realm: String)(implicit jwtContext: JwtContext, ec: ExecutionContext): JwtHttpAuthenticator =
    new JwtHttpAuthenticator(realm)
}
