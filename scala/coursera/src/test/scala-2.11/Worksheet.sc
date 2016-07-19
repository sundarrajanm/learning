object Worksheet {
  class Rational (x: Int, y: Int) {
    def numer = x
    def denom = y
  }

  val t = new Rational(10, 20)
  t.numer
}