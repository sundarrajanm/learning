import org.scalatest.FunSuite

class ConcatWithFold extends FunSuite {

  def concatWithFoldRight(list1: List[Int], list2: List[Int]) =
    (list1 foldRight list2) { (value, items) =>
      println(s"x: $value, y: $items")
      value :: items
    }

  def concatWithFoldLeft(list1: List[Int], list2: List[Int]) =
    (list1 foldLeft list2) { (items, value) =>
      println(s"x: $value, y: $items")
      items :+ value
    }

  test("concat two lists with fold right function") {
    val result = concatWithFoldRight(List(1, 5, 2), List(4, 3, 7))
    assert(result === List(1, 5, 2, 4, 3, 7))
  }

  test("concat two lists with fold left function") {
    val result = concatWithFoldLeft(List(4, 3, 7), List(1, 5, 2))
    assert(result === List(1, 5, 2, 4, 3, 7))
  }
}
