import org.scalatest.FunSuite

class ListArrange extends FunSuite {

  def listArray(list1: List[Int], list2: List[Int]) = {
    ((list1 ++ list2) distinct) sortWith((x, y) => x < y)
  }

  test("Arranges two lists in ascending order") {
    assert(listArray(List(), List()) === List())
    assert(listArray(List(), List(1)) === List(1))
    assert(listArray(List(2), List()) === List(2))
    assert(listArray(List(2), List(4)) === List(2, 4))
    assert(listArray(List(2), List(4, 3)) === List(2, 3, 4))
    assert(listArray(List(2, 4), List(4, 3)) === List(2, 3, 4))
    assert(listArray(List(2, 4, 3, 6), List(1, 7)) === List(1, 2, 3, 4, 6, 7))
  }
}
