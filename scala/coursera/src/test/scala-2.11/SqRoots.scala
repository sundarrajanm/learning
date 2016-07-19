import org.scalatest.FunSuite

class SqRoots extends FunSuite {

  def abs(d: Double) = if (d < 0) -d else d

  def isGoodEnough(guess: Double, x: Double): Boolean =
    abs(guess * guess - x) < 0.0001

  def improve(guess: Double, x: Double): Double =
    (guess + x / guess) / 2

  def sqrtIter(guess: Double, x: Double): Double =
    if(isGoodEnough(guess, x)) guess
    else sqrtIter(improve(guess, x), x)


  def sqrt(in: Double): Double = sqrtIter(1.0, in)

  test("Works") {
    assert(Math.round(sqrt(4)) == 2)
  }
}
