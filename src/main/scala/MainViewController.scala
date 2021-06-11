import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Button, Div, Span}

class MainViewController() {

  val wrapper: Element = View.createDiv("wrapper")
  val restartButton: Button = View.createButton("Restart")
  val gameWrapper: Element = View.createDiv("game-wrapper")

  var game: Game = Game.initial
  var moves: Seq[EvaluatedMove] = ComputerPlayer.evaluateMoves(game)

  def init(): Unit = {
    dom.document.body.appendChild(wrapper)
    wrapper.appendChild(restartButton)
    wrapper.appendChild(gameWrapper)

    // menu
    restartButton.onclick = { _ => clickRestart() }
    //restartButton.onmouseover = { _ => println("mouseover") }

    renderGame()
  }

  private def renderGame(): Unit = {
    gameWrapper.innerHTML = ""
    for {
      line <- 0 to 3
    } yield {
      val lineDiv = View.createDiv("line")
      gameWrapper.appendChild(lineDiv)
      for {
        column <- 0 to 7
      } yield {
        val field = line * 8 + column
        val fieldDiv = createField(field)
        lineDiv.appendChild(fieldDiv)
      }
    }

    val summary = View.createDiv("summary")
    summary.innerHTML = s"${game.playerSeedCount(0)} vs ${game.playerSeedCount(1)}"
    gameWrapper.appendChild(summary)
  }

  private def createField(field: Int): Span = {
    val fieldElement = View.createSpan("field")

    val fieldValue = game.fields(field)
    if (fieldValue >= 1) {
      fieldElement.classList.add("nonempty")
      if (game.canPlayField(field)) {
        val fieldButton = View.createButton(fieldValue.toString)
        val moveValue = moves.find(_.field == field).get.value
        val color = (moveValue * 4).min(255)
        val (r, g, b) = (255 - color, color, 0)
        fieldButton.style.backgroundColor = s"rgb($r,$g,$b)"
        fieldButton.onclick = { _ => clickField(field) }
        fieldElement.appendChild(fieldButton)
      } else {
        val oneSpan = View.createSpan("number")
        oneSpan.innerHTML = fieldValue.toString
        fieldElement.appendChild(oneSpan)
        fieldElement.classList.add("noaction")
      }
    } else {
      val oneSpan = View.createSpan("number")
      oneSpan.innerHTML = "."
      fieldElement.appendChild(oneSpan)
      fieldElement.classList.add("empty")
      fieldElement.classList.add("noaction")
    }

    fieldElement
  }

  private def clickField(field: Int): Unit = {
    updateGame(game.playField(field))
  }

  private def clickRestart(): Unit = {
    updateGame(Game.initial)
  }

  private def updateGame(newGame: Game): Unit = {
    game = newGame
    moves = ComputerPlayer.evaluateMoves(game)
    renderGame()
  }

}
