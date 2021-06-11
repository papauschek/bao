import org.scalatest.FunSuite

class ComputerPlayerSuite extends FunSuite {

  test("test evaluation depth = 0") {
    assert(evaluate(Game.initial, depth = 0) == 32)
    assert(evaluate(Game.initial.playField(0), depth = 0) == 28)
  }

  test("test evaluation depth = 1") {
    assert(evaluate(Game.initial, depth = 1) == 36)
    assert(evaluate(Game.initial.playField(0), depth = 1) == 34)
  }

  test("can play full game") {
    var game = Game.initial
    var isGameOver = false
    while (!game.isGameOver && !isGameOver) {

      val moves = ComputerPlayer.evaluateMoves(game)
      moves.maxByOption(_.value) match {
        case Some(bestMove) =>

          println()
          println(game)
          println(s"player=${game.player}, bestMove=${bestMove.field}, value=${bestMove.value}")
          game = game.playField(bestMove.field)

        case _ => isGameOver = true
      }
    }
  }

  private def evaluate(game: Game, depth: Int): Int = {
    ComputerPlayer.evaluate(game, ComputerPlayer.MAX_DEPTH - depth).value
  }

}
