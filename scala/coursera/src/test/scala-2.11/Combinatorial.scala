import org.scalatest.FunSuite

class Combinatorial extends FunSuite {

  def isPrime(num: Int): Boolean =
    (2 until num) forall (n => (num % n) !== 0)

  def combine(in: Int) = ((1 to in) flatMap (i =>
      (1 to i) map (j => (i, j))
    )) filter (pair => isPrime(pair._1 + pair._2))

  test("Print combination") {
    assert(combine(4) === Vector((1,1), (2,1), (3,2), (4,1), (4,3)))
  }

  def combineWithFor(in: Int) = for {
    i <- 1 to in
    j <- 1 to i if isPrime(i + j)
  } yield (i, j)

  test("Print combination using for-expression") {
    assert(combineWithFor(4) === Vector((1,1), (2,1), (3,2), (4,1), (4,3)))
  }
}
