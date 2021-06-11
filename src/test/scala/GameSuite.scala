import org.scalatest.FunSuite

import scala.util.Random

class GameSuite extends FunSuite {

  test("test next") {
    assert(Game.nextFieldIndex(0) == 8)
    assert(Game.nextFieldIndex(8) == 9)
    assert(Game.nextFieldIndex(15) == 7)
    assert(Game.nextFieldIndex(7) == 6)
    assert(Game.nextFieldIndex(31) == 23)
    assert(Game.nextFieldIndex(23) == 22)
    assert(Game.nextFieldIndex(16) == 24)
    assert(Game.nextFieldIndex(24) == 25)
  }

}
