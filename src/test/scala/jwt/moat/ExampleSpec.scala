package jwt.moat

import jwt.moat.JwtHelper._
import org.scalatest._

class ExampleSpec extends FlatSpec with Matchers {

  "Valid JWT" should "pass moat" in {
    val key = randomKey(32)
    implicit val jwtContext = JwtContext(key, Algorithms.HS256)
    val jwt = JwtHelper.jwtString(jwtContext.key, Algorithms.HS256)
    val r = Secure(JWT(jwt)){
      case Right(user) => Some(user)
      case Left(e) => None
    }
    r should be ('defined)
  }

  "JWT signed with different key" should "be not accepted" in {
    val key1 = randomKey(32)
    val key2 = randomKey(32)
    val signedContext = JwtContext(key1, Algorithms.HS256)
    implicit val expectedContext = JwtContext(key2, Algorithms.HS256)
    val jwt = JwtHelper.jwtString(signedContext.key, Algorithms.HS256)
    val r = Secure(JWT(jwt)){
      case Right(user) => Some(user)
      case Left(e) => None
    }
    r should be (None)
  }

  import Checks._
  "JWT with missing attribute" should "be not accepted" in {
    val key1 = randomKey(32)
    val key2 = randomKey(32)
    val signedContext = JwtContext(key1, Algorithms.HS256)
    implicit val expectedContext = JwtContext(key2, Algorithms.HS256)
    val jwt = JwtHelper.jwtString(signedContext.key, Algorithms.HS256)
    val r = Secure(hasAttribute("test"))(JWT(jwt)){
      case Right(user) => Some(user)
      case Left(e) => None
    }
    r should be (None)
  }

}
