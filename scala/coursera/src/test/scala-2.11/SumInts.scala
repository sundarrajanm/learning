import org.scalatest.FunSuite

class SumInts extends FunSuite {
  def fact(n: Int): Int = if (n == 0) 1 else n * fact(n - 1)

  def sum(f: Int => Int, from: Int, to: Int): Int = {
    def loop(from: Int, acc: Int): Int = {
      if (from > to) acc
      else loop(from + 1, f(from) + acc)
    }
    loop(from, 0)
  }

  def sumInts(from: Int, to: Int): Int = sum(x => x, from, to)
  def sumCubes(from: Int, to: Int): Int = sum(x => x * x * x, from,to)
  def sumFacts(from: Int, to: Int): Int = sum(fact, from, to)

  test("Sums all integers between a and b") {
    assert(sumInts(2, 4) === 9)
  }

  test("Sums cubes of all integers between a and b") {
    assert(sumCubes(3, 6) === 432)
  }

  test("Sums factorial of all integers from a and b") {
    assert(sumFacts(1, 4) == 33)
  }
}

class SumIntsCurry extends FunSuite {

  def fact(n: Int): Int = if (n == 0) 1 else n * fact(n - 1)

  def sum(f: Int => Int)(from: Int, to: Int): Int =
      if (from > to) 0 else f(from) + sum(f)(from + 1, to)

  def sumInts: (Int, Int) => Int = sum(x => x)
  def sumCubes: (Int, Int) => Int = sum(x => x * x * x)
  def sumFacts: (Int, Int) => Int = sum(fact)

  test("Sums all integers between a and b") {
    assert(sumInts(2, 4) === 9)
  }

  test("Sums cubes of all integers between a and b") {
    assert(sumCubes(3, 6) === 432)
  }

  test("Sums factorial of all integers from a and b") {
    assert(sumFacts(1, 4) == 33)
  }
}
