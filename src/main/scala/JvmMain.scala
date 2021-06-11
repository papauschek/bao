import scala.util.Random

object JvmMain {

  def main(args: Array[String]): Unit = {

  }

  private def performanceTest(): Unit = {
    var iterations = 0
    var moveCount = 0
    while(true) {
      moveCount += playRandomGame()
      iterations += 1
      if (iterations % 100000 == 0) println(iterations, moveCount / iterations)
    }
  }

  private def playRandomGame(): Int = {
    var game = Game.initial
    var canPlay = true
    while (canPlay) {
      val fields = game.playableFields()
      //println(game)
      //println(fields.length)
      if (fields.length > 0) {
        game = game.playField(fields(Random.nextInt(fields.length)))
      } else {
        canPlay = false
      }
    }
    //println(game)
    game.moveCount
  }

}
