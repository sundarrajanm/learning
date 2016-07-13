import scala.collection.mutable.{ArrayBuffer, MutableList}

class Game {
  var points = MutableList[Int]()

  def roll(pins: Int): Unit = {
    points += pins
  }

  def score() = {
    var total = 0
    var i = 0
    var frame = 1
    while(i < points.length && frame <= 10) {
      if (points(i) == 10) {
        total += countNextRollsInclusive(i, 3)
        i += 1
        frame += 1
      } else {
        val framePoints = (points(i) + points(i + 1))
        if (framePoints == 10) {
          total += countNextRollsInclusive(i, 3)
        } else {
          total += (points(i) + points(i + 1))
        }
        i += 2
        frame += 1
      }
    }
    total
  }

  def countNextRollsInclusive(from: Int, last: Int): Int = {
    (0 until last).foldLeft(0)((carry, idx) => carry + points(idx))
  }
}
