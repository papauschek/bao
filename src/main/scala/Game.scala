import scala.collection.mutable
import scala.collection.mutable.{ArrayBuilder, WrappedArray}

/**
 *
 * @param fields player 0 outer, [0-7]
 *               player 0 inner,
 *               player 1 inner,
 *               player 1 outer. [24-31]
 *               line sums [32-35] */
class Game(val fields: Array[Byte],
           val player: Int,
           val moveCount: Int) {

  def nextPlayer: Int = 1 - player

  def isGameOver: Boolean = {
    fields(33) == 0 || fields(34) == 0
  }

  def hasCurrentPlayerWon: Boolean = {
    fields(34 - player) == 0
  }

  def hasCurrentPlayerLost: Boolean = {
    fields(33 + player) == 0
  }

  def canPlayField(fieldIndex: Int): Boolean = {
    !isGameOver && fields(fieldIndex) >= 2 && fieldIndex / 16 == player
  }

  def playableFields(): Array[Int] = {
    if (isGameOver) {
      Array.empty
    } else {
      var index = Game.playerStartIndex(player)
      val maxIndex = index + 16
      val result = new ArrayBuilder.ofInt()
      result.sizeHint(fields.length / 2)
      while (index < maxIndex) {
        if (fields(index) >= 2) {
          result.addOne(index)
        }
        index += 1
      }
      result.result()
    }
  }

  private def addToField(array: Array[Byte], index: Int, add: Int): Int = {
    val sumIndex = 32 + index / 8
    array(sumIndex) = (array(sumIndex) + add).toByte
    val value = array(index) + add
    array(index) = value.toByte
    value
  }

  def playField(fieldIndex: Int): Game = {
    require(fields(fieldIndex) >= 2, "played field needs at least 2 seeds")
    require(fieldIndex / 16 == player, "fieldIndex needs to be within player range")

    val nextGame = copy(player = nextPlayer, moveCount = moveCount + 1)
    val nextFields = nextGame.fields
    var currentField = fieldIndex
    var iteration = 0
    var currentFieldValue: Int = nextFields(currentField)

    while (currentFieldValue >= 2 && !nextGame.isGameOver) {

      // take the seeds
      var takenCount: Int = currentFieldValue
      addToField(nextFields, currentField, -currentFieldValue)

      // take opponents seeds
      if (iteration >= 1 && currentFieldValue >= 3 && currentField >= 8 && currentField < 24) {
        val opponentField = currentField % 16 + 8
        val opponentValue = nextFields(opponentField)
        takenCount += opponentValue
        addToField(nextFields, opponentField, -opponentValue)
      }

      // distribute the seeds
      while(takenCount > 0) {
        currentField = Game.nextFieldIndex(currentField)
        currentFieldValue = addToField(nextFields, currentField, 1)
        takenCount -= 1
      }

      iteration += 1
      if (iteration >= 1000) {
        println(nextGame)
        throw new IllegalArgumentException(s"Infinite iterations fieldIndex=$fieldIndex\r\n$this")
      }
    }

    nextGame
  }

  def copy(player: Int = player, moveCount: Int = moveCount): Game = {
    val copy = new Array[Byte](fields.length)
    System.arraycopy(fields, 0, copy, 0, fields.length)
    new Game(copy, player, moveCount)
  }

  override def toString: String = {
    fields.grouped(8).map(line => line.mkString(" ")).mkString("\r\n") + s" moveCount=$moveCount"
  }

}


object Game {

  def playerStartIndex(player: Int): Int = player * 16

  def nextFieldIndex(fieldIndex: Int): Int = {
    val column = fieldIndex % 8
    val lineIndex = fieldIndex / 8 // 0-3
    val lineDirection = (lineIndex % 2) * 2 - 1
    val nextColumn = column + lineDirection // -1 - 8
    if (nextColumn == -1 || nextColumn == 8) {
      val player = fieldIndex / 16
      val otherLine = player * 2 + (lineIndex + 1) % 2
      otherLine * 8 + column // line wrap
    } else {
      fieldIndex + lineDirection // no line wrap
    }
  }

  val initial: Game = {
    val game = new Game(Array.fill(36)(2), player = 0, moveCount = 0)
    game.fields(32) = 16
    game.fields(33) = 16
    game.fields(34) = 16
    game.fields(35) = 16
    game
  }

}
