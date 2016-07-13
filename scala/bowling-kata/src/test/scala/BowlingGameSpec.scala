import org.scalatest.{BeforeAndAfter, FlatSpec}

class BowlingGameSpec extends FlatSpec with BeforeAndAfter {

  var game :Game = _

  before {
    game = new Game
  }

  def rollPinsMany (pins: Int, times: Int) = {
    (1 to times).foreach(i => game.roll(pins))
  }

  it should "score zero, when all rolls are misses." in {
    rollPinsMany(0, 20)
    assert(game.score() === 0)
  }

  it should "score 20, when only one pin is knock down with each ball." in {
    rollPinsMany(1, 20)
    assert(game.score() === 20)
  }

  it should "score 16, when a spare in 1st frame, followed 3 pins and" +
    "followed by all misses." in {
    game roll 6
    game roll 4
    game roll 3
    rollPinsMany(0, 17)
    assert(game.score() === 16)
  }

  it should "score 24, when a strike in 1st frame, followed by 3 and then" +
    "4 pins, followed by all misses." in {
    game roll 10
    game roll 3
    game roll 4
    rollPinsMany(0, 16)
    assert(game.score() === 24)
  }

  it should "score 300, with perfect game - 12 strikes." in {
    rollPinsMany(10, 12)
    assert(game.score() === 300)
  }
}
