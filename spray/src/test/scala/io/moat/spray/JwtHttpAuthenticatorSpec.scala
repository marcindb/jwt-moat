package io.moat.spray

import io.moat.{Algorithms, JwtContext}
import org.scalatest.{FlatSpec, Matchers}
import spray.http.HttpHeaders._
import spray.http._
import spray.routing.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import spray.routing._
import spray.routing.authentication._
import spray.testkit.ScalatestRouteTest

import scala.concurrent.Future

class JwtHttpAuthenticatorSpec extends FlatSpec with Matchers with ScalatestRouteTest with Directives {

  val rawKey = JwtHelper.randomKey(32)
  implicit val jwtContext = JwtContext(rawKey, Algorithms.HS256)

  val subject = "subject"
  val validJwt = JwtHelper.jwtString(subject, jwtContext.key, Algorithms.HS256)

  val jwtAuthenticator = JwtAuth("Realm")
  val challenge = `WWW-Authenticate`(HttpChallenge("Bearer", "Realm"))

  val Ok = HttpResponse()
  val completeOk = complete(Ok)

  def echoComplete[T]: T ⇒ Route = { x ⇒ complete(x.toString) }

  val doAuth = BasicAuth(UserPassAuthenticator[BasicUserContext] { userPassOption ⇒
    Future.successful(Some(BasicUserContext(userPassOption.get.user)))
  }, "Realm")

  "the JwtHttpAuthenticator" should "reject requests without Authorization header with an AuthenticationFailedRejection" in {
    Get() ~> {
      authenticate(jwtAuthenticator) {
        echoComplete
      }
    } ~> check {
      rejection shouldEqual AuthenticationFailedRejection(CredentialsMissing, List(challenge))
    }
  }

  "the JwtHttpAuthenticator" should "reject unauthenticated requests with Authorization header with an AuthenticationFailedRejection" in {
    Get().withHeaders(Authorization(OAuth2BearerToken("mF_9.B5f-4.1JqM/"))) ~> {
      authenticate(jwtAuthenticator) {
        echoComplete
      }
    } ~> check {
      rejection shouldEqual AuthenticationFailedRejection(CredentialsRejected, List(challenge))
    }
  }

  "the JwtHttpAuthenticator" should "reject requests with illegal Authorization header with 401" in {
    Get().withHeaders(RawHeader("Authorization", "illegal header")) ~> handleRejections(RejectionHandler.Default) {
      authenticate(jwtAuthenticator) {
        echoComplete
      }
    } ~> check {
      status shouldEqual StatusCodes.Unauthorized
    }
  }

  "the JwtHttpAuthenticator" should "extract the object representing the user identity created by successful authentication" in {
    Get().withHeaders(Authorization(OAuth2BearerToken(validJwt))) ~> {
      authenticate(jwtAuthenticator) {
        user => complete(user.sub)
      }
    } ~> check {
      responseAs[String] should be(subject)
    }
  }

  "the JwtHttpAuthenticator" should "properly handle exceptions thrown in its inner route" in {
    object TestException extends spray.util.SingletonException
    Get().withHeaders(Authorization(OAuth2BearerToken(validJwt))) ~> {
      handleExceptions(ExceptionHandler.default) {
        authenticate(jwtAuthenticator) { _ ⇒ throw TestException }
      }
    } ~> check {
      status shouldEqual StatusCodes.InternalServerError
    }
  }

}
