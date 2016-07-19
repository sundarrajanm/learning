import org.scalatest.FunSuite

class ProductsInt extends FunSuite {

  def mapReduce(f: Int => Int, combine: (Int, Int) => Int,
                terminateValue: Int) (from: Int, to: Int): Int =
    if (from > to)
      terminateValue
    else
      combine(f(from), mapReduce(f, combine, terminateValue)(from + 1, to))

  def product(f: Int => Int)(from: Int, to: Int): Int =
    mapReduce(f, (x, y) => x * y, 1)(from, to)

  def factorial(in: Int) = product(x => x)(1, in)

  test("Calculates products of the values of a function for" +
    "the points on a given interval") {

    assert(product(x => x)(5, 7) === 210)
  }

  test("Calculates factorial using product function") {
    assert(factorial(5) === 120)
  }

  def sum(f: Int => Int)(from: Int, to: Int): Int =
    mapReduce(f, (x, y) => x + y, 0)(from, to)

  def sumSquares(from: Int, to: Int): Int = sum(x => x * x)(from, to)

  test("Calculates sum of squares using generalized mapReduce function") {
    assert(sumSquares(5, 7) === 110)
  }
}
