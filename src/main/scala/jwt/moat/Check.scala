package jwt.moat

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions


object Checks {

  type Check = (Claims => Boolean)

  val successCheck = (claims: Claims) => true

  class AndCheck(left: Check, right: Check) extends Check {
    def apply(claims: Claims) = left(claims) && right(claims)
    def and(right: Check) = new AndCheck(left, right)
  }

  class OrCheck(left: Check, right: Check) extends Check {
    def apply(claims: Claims) = left(claims) || right(claims)
    def or(right: Check) = new OrCheck(left, right)
  }

  implicit def toAndCheck(check: Check): AndCheck = new AndCheck(check, successCheck)

  implicit def toOrCheck(check: Check): OrCheck = new OrCheck(check, successCheck)

  def hasAttribute(name: String): Check = (claims: Claims) => claims.params.get(name).isDefined

  def attributeEquals[T](name: String, value: T): Check =
    (claims: Claims) => claims.params.get(name).exists(_.asInstanceOf[T].equals(value))

  def maxAgeInSeconds(seconds: Long) = (claims: Claims) => (System.currentTimeMillis() - claims.iat.getTime)/1000 < seconds

  def maxAge(duration: FiniteDuration) = maxAgeInSeconds(duration.toSeconds)

}

trait CheckFailed

object HasRoleFailed extends CheckFailed