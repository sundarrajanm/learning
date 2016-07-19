import org.scalatest.FunSuite

class ListHigerOrder extends FunSuite {

  def multiplyFactor(list: List[Int], factor: Int): List[Int] =
    list match {
      case Nil => list
      case head :: tail => head * factor :: multiplyFactor(tail, factor)
    }

  test("multiple the list of ints by a factor") {
    val inList: List[Int] = List(4, 5, 6)
    assert(multiplyFactor(inList, 4) === List(16, 20, 24))

    assert((inList map (x => x * 4)) === List(16, 20, 24))
    assert((inList filter (x => x % 2 === 0)) === List(4, 6))

    val (evens, odds) = inList partition (_ % 2 === 0)
    assert(evens === List(4, 6) && odds === List(5))

    assert((inList takeWhile (_ % 2 == 0)) === List(4))
    assert((inList dropWhile (_ % 2 == 0)) === List(5, 6))

    // reduce, fold

    assert((inList foldLeft 1)(_ * _) === 120)
  }
}
