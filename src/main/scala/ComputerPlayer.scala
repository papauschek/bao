

object ComputerPlayer {

  val MAX_DEPTH: Int = 5

  private val MAX_VALUE = 64

  def evaluateMoves(game: Game, maxDepth: Int): Seq[EvaluatedMove] = {
    game.playableFields().map {
      field =>
        val opponentGame = game.playField(field)
        val Evaluation(opponentValue) = evaluate(opponentGame, maxDepth)
        val playerValue = MAX_VALUE - opponentValue
        EvaluatedMove(field, playerValue)
    }
  }

  /** @return positive value if current player in the lead */
  def evaluate(game: Game, maxDepth: Int, depth: Int = 0): Evaluation = {

    if (game.hasCurrentPlayerWon) {
      Evaluation(MAX_VALUE)
    } else if (game.hasCurrentPlayerLost) {
      Evaluation(0)
    } else if (depth >= maxDepth) {
      val sumBase = 32 + game.player * 2
      val playerSeeds = game.fields(sumBase) + game.fields(sumBase + 1)
      Evaluation(playerSeeds) // [1-63] return number of current players seeds as evaluation value
    } else {

      var index = Game.playerStartIndex(game.player)
      val maxIndex = index + 16
      val fields = game.fields
      var bestIndex = -1
      var bestValue = Int.MaxValue

      while (index < maxIndex) {
        if (fields(index) >= 2) {

          val nextGame = game.playField(index)
          val evaluation = evaluate(nextGame, maxDepth, depth = depth + 1)

          // lowest opponent value is the best value for current player
          if (evaluation.value < bestValue) {
            bestValue = evaluation.value
            bestIndex = index
          }

        }
        index += 1
      }

      if (bestIndex == -1) {
        Evaluation(0) // current player has no more moves and lost
      } else {
        // current player value is opposite of best opponent value
        Evaluation(MAX_VALUE - bestValue)
      }

    }

  }

}

case class Evaluation(value: Int)

case class EvaluatedMove(field: Int, value: Int)
