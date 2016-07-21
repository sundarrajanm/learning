import org.scalatest.FunSuite

class OtherSequences extends FunSuite {

  test("String related sequence functions works") {
    val str = "HelloWorld"
    assert(str exists (_.isUpper))
    assert((str forall (_.isUpper)) === false)

    val nums = List(1, 2, 3)
    val zipped = Vector(('H',1), ('e',2), ('l',3))
    assert( (str zip nums) === zipped)
    assert( (zipped unzip) === (Vector('H', 'e', 'l'),Vector(1, 2, 3)))

    assert((str foldLeft 0)((x, y) => x + 1) === 10)
    val mappedGreat = "Great" flatMap (c => List("-", c))
    assert(mappedGreat === Vector("-", 'G', "-", 'r', "-", 'e', "-", 'a'
      , "-", 't'))

    def isPrime(num: Int): Boolean =
      (2 until num) forall (n => (num % n) !== 0)
    assert(isPrime(7) === true)
  }
}
