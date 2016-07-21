import org.scalatest.FunSuite

class NQueens extends FunSuite {
  def isSafe(col: Int, refQueens: List[Int]): Boolean = {
    val row = refQueens.length
    val qWithR = (row - 1 to 0 by -1) zip refQueens
    qWithR forall {
      case (r, c) => col != c && math.abs(col - c) != row - r
    }
  }

  def queens(n: Int): Set[List[Int]] = {
    def placeQueen(queens: Int): Set[List[Int]] =
      if (queens == 0) Set(List())
      else
        for {
          q <- placeQueen(queens - 1)
          col <- 0 until n if isSafe(col, q)
        } yield col :: q
    placeQueen(n)
  }

  def print(soln: List[Int]) = {
    val lines = for (q <- soln.reverse)
      yield Vector.fill(soln.length)("* ").updated(q, "X ").mkString
    lines mkString "\n"
  }

  test("Placing 0 queens will give a set of empty list") {
    assert(queens(0) === Set(List()))
  }

  test("Placing 1 queen will give a set of empty list") {
    val result = queens(4)
    assert(result === Set(List(1, 3, 0, 2), List(2, 0, 3, 1)))
    println((result map print) mkString "\n\n")
  }
}
