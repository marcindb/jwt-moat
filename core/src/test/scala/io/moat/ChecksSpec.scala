package io.moat

import java.util.Date

import language.postfixOps

import Checks._
import org.scalatest.{FlatSpec, Matchers}
import scala.concurrent.duration._

class ChecksSpec extends FlatSpec with Matchers {

  val testClaims = new Claims("test", "test", "test", new Date(), new Date(), new Date(), "test")

  "has attribute" should "check whether given attribute exists" in {
    val claims = testClaims.copy(params = Map("test" -> "test"))
    assert(hasAttribute("test")(claims) === true)
    assert(hasAttribute("undefined")(claims) === false)
  }

  "attribute equals" should "check whether given attribute exists and equals to given value" in {
    val claims = testClaims.copy(params = Map("test" -> "test", "rules" -> List("user")))
    assert(attributeEquals("test", "test")(claims) === true)
    assert(attributeEquals("test", "")(claims) === false)
    assert(attributeEquals("test", 2)(claims) === false)
    assert(attributeEquals("rules", "test")(claims) === false)
    assert(attributeEquals("rules", List("user"))(claims) === true)
    assert(attributeEquals("undefined", "test")(claims) === false)
    //FIXME
    //assert(attributeEquals("test", List("user"))(claims) === true)
  }

  "and" should "enable to and checks" in {
    val claims = testClaims.copy(params = Map("test" -> "test", "rules" -> List("user")))
    assert((attributeEquals("test", "test") and hasAttribute("rules"))(claims) === true)
    assert((attributeEquals("test", "test") and hasAttribute("rule"))(claims) === false)
  }

  "or" should "enable to or checks" in {
    val claims = testClaims.copy(params = Map("test" -> "test", "rules" -> List("user")))
    assert((attributeEquals("test", "test") or hasAttribute("rules"))(claims) === true)
    assert((attributeEquals("test", "test") or hasAttribute("rule"))(claims) === true)
    assert((attributeEquals("test", "") or hasAttribute("rule"))(claims) === false)
  }

  "maxAgeInSeconds" should "check whether iat is before given time" in {
    val iat = new Date(System.currentTimeMillis() - 1001)
    val claims = testClaims.copy(params = Map("test" -> "test", "rules" -> List("user")), iat = iat)
    assert(maxAgeInSeconds(2)(claims) === true)
    assert(maxAgeInSeconds(1)(claims) === false)
  }

  "maxAge" should "check whether iat is before given time" in {
    val iat = new Date(System.currentTimeMillis() - 1001)
    val claims = testClaims.copy(params = Map("test" -> "test", "rules" -> List("user")), iat = iat)
    assert(maxAge(2 seconds)(claims) === true)
    assert(maxAge(1 second)(claims) === false)
  }

}

