package jwt.moat

object Algorithms {

  sealed trait Algorithm {
    def jwtName: String
    def jcaName: String
  }

  object HS256 extends Algorithm {
    def jwtName: String = "HS256"
    def jcaName: String = "HmacSHA256"
  }

  object HS384 extends Algorithm {
    def jwtName: String = "HS384"
    def jcaName: String = "HmacSHA384"
  }

  object HS512 extends Algorithm {
    def jwtName: String = "HS512"
    def jcaName: String = "HmacSHA512"
  }

  object RS256 extends Algorithm {
    def jwtName: String = "RS256"
    def jcaName: String = "SHA256withRSA"
  }

  object RS384 extends Algorithm {
    def jwtName: String = "RS384"
    def jcaName: String = "SHA384withRSA"
  }

  object RS512 extends Algorithm {
    def jwtName: String = "RS512"
    def jcaName: String = "SHA512withRSA"
  }

}
