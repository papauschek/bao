

/***
 * @param actionField field for next action
 * @param taken if zero then steps are over
 */
case class Step(game: Game, actionField: Int, taken: Int) {

  def next(): Either[Step, Game] = {
    val nextGame = game.copy()
    val fields = nextGame.fields
    val nextField = Game.nextFieldIndex(actionField)
    if (taken >= 1) {
      val fieldValue = Game.addToField(fields, actionField, 1)
      if (taken == 1 && fieldValue >= 3 && actionField >= 8 && actionField < 24) {
        // take opponents seeds
        val opponentField = actionField % 16 + 8
        val opponentValue = fields(opponentField)
        Game.addToField(fields, opponentField, -opponentValue)
        Game.addToField(fields, actionField, -fieldValue)
        Left(Step(nextGame, actionField = nextField, fieldValue + opponentValue))
      } else if (taken == 1) {
        if (fieldValue == 1) {
          // last move
          Right(nextGame.copy(player = game.nextPlayer, moveCount = game.moveCount + 1))
        } else {
          // pick up seeds and continue
          Game.addToField(fields, actionField, -fieldValue)
          Left(Step(nextGame, actionField = nextField, fieldValue))
        }
      } else {
        // distribute seeds
        Left(Step(nextGame, actionField = nextField, taken - 1))
      }
    } else {
      Right(game.copy(player = game.nextPlayer, moveCount = game.moveCount + 1))
    }
  }

}

object Step {

  /** take the seeds */
  def play(game: Game, fieldIndex: Int): Step = {
    val nextGame = game.copy()
    val fields = nextGame.fields
    val takenCount: Int = fields(fieldIndex)
    Game.addToField(fields, fieldIndex, -takenCount)
    Step(nextGame, actionField = Game.nextFieldIndex(fieldIndex), takenCount)
  }

}
