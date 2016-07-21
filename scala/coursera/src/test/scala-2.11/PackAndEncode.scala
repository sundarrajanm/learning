import org.scalatest.FunSuite

class PackAndEncode extends FunSuite {

  def pack(inList: List[String]): List[List[String]] =
    inList match {
      case Nil => Nil
      case head :: tail =>
        val (takeList, dropList) = inList span (x => x equals head)
        takeList :: pack(dropList)
    }

  test("Pack consecutive duplicates of a list elements into sublists") {
    val list1 = pack(List("a", "a", "a", "b", "b", "c"))
    assert(list1 === List(List("a", "a", "a"), List("b", "b"), List("c")))
  }

  def encode(inList: List[String]): List[(String, Int)] =
    inList match {
      case Nil => Nil
      case head :: tail =>
        val (takeList, dropList) = inList span (x => x equals head)
        (takeList.head, takeList.length) :: encode(dropList)
    }

  test("Encode consecutive duplicates of a list into a list of pair of (element, Count)") {
    val list1 = encode(List("a", "a", "a", "b", "b", "c"))
    assert(list1 === List(("a", 3), ("b", 2), ("c", 1)))
  }
}
