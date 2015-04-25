package jwt.moat

import jwt.moat.JwtHelper._
import org.scalatest._

class ExampleSpec extends FlatSpec with Matchers {

  "Valid JWT" should "pass moat" in {
    val key = randomKey(32)
    implicit val jwtContext = JwtContext(key,"HmacSHA256")
    val jwt = JwtHelper.jwtString(jwtContext.key, "HS256")
    val r = secure()(JWT(jwt)){
      case Right(user) => Some(user)
      case Left(e) => None
    }
    r should be ('defined)
  }

  "JWT signed with different key" should "be not accepted" in {
    val key1 = randomKey(32)
    val key2 = randomKey(32)
    val signedContext = JwtContext(key1,"HmacSHA256")
    implicit val expectedContext = JwtContext(key2,"HmacSHA256")
    val jwt = JwtHelper.jwtString(signedContext.key, "HS256")
    val r = secure()(JWT(jwt)){
      case Right(user) => Some(user)
      case Left(e) => None
    }
    r should be (None)
  }

  import Checks._
  "JWT with missing attribute" should "be not accepted" in {
    val key1 = randomKey(32)
    val key2 = randomKey(32)
    val signedContext = JwtContext(key1,"HmacSHA256")
    implicit val expectedContext = JwtContext(key2,"HmacSHA256")
    val jwt = JwtHelper.jwtString(signedContext.key, "HS256")
    val r = secure(hasAttribute("test"))(JWT(jwt)){
      case Right(user) => Some(user)
      case Left(e) => None
    }
    r should be (None)
  }

  "JWT with missing attribute 2" should "be not accepted" in {
    val key1 = randomKey(32)
    val key2 = randomKey(32)
    val signedContext = JwtContext(key1,"HmacSHA256")
    implicit val expectedContext = JwtContext(key2,"HmacSHA256")
    val jwt = JwtHelper.jwtString(signedContext.key, "HS256")
    trait User
    val map = (c : Claims) => new User{}
    val r = secur(hasAttribute("test"))(map)(JWT(jwt)){
      case Right(user) => Some(user)
      case Left(e) => None
    }
    r should be (None)
  }

}
