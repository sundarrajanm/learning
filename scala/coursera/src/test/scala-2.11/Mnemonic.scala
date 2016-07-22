import org.scalatest.FunSuite

import scala.io.Source

/**
  * Phone keys mnemonics assigned to them.
  */
class Mnemonic extends FunSuite {

  val in = Source.fromURL("http://lamp.epfl.ch/files/content/sites/lamp/files/teaching/progfun/linuxwords.txt")
  val words = in.getLines().toList filter (x => x forall(c => c.isLetter))
  val mnemonics = Map(
    '2' -> "ABC",
    '3' -> "DEF",
    '4' -> "GHI",
    '5' -> "JKL",
    '6' -> "MNO",
    '7' -> "PQRS",
    '8' -> "TUV",
    '9' -> "WXYZ"
  )

  val charCode = for {
    (n, str) <- mnemonics
    c <- str
  } yield c -> n

  def wordCode(word: String): String =
    word.toUpperCase() map charCode

  val wordsForNum: Map[String, Seq[String]] =
    words groupBy wordCode withDefaultValue Seq()

  def encode(number: String): Set[List[String]] = {
    if(number.isEmpty()) Set(List())
    else {
      for {
        split <- 1 to number.length
        word <- wordsForNum(number take split)
        rest <- encode(number drop split)
      } yield word :: rest
    }.toSet
  }

  def translate(number: String) =
    encode(number) map (_ mkString " ")

  test("7225247386 encodes to 'Scala is fun'") {
    assert(translate("7225247386") contains "Scala is fun")
  }
}
