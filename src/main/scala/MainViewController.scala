import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Button, Div, Span}

class MainViewController() {

  val wrapper: Element = View.createDiv("wrapper")
  val restartButton: Button = View.createButton("Restart")
  val nextButton: Button = View.createButton("Next")
  val computerButton: Button = View.createButton("CPU")
  val gameWrapper: Element = View.createDiv("game-wrapper")

  var game: Game = Game.initial
  var maybeStep: Option[Step] = None
  var moves: Seq[EvaluatedMove] = Nil

  var computerEnabled: Boolean = false

  def init(): Unit = {
    dom.document.body.appendChild(wrapper)
    wrapper.appendChild(restartButton)
    wrapper.appendChild(computerButton)
    wrapper.appendChild(gameWrapper)
    wrapper.appendChild(nextButton)

    // menu
    restartButton.onclick = { _ => clickRestart() }
    nextButton.onclick = { _ => clickNext() }
    computerButton.onclick = { _ => toggleComputer() }

    renderGame()
  }

  def clickNext(): Unit = {
    maybeStep.foreach {
      step =>
        step.next() match {
          case Left(nextStep) =>
            maybeStep = Some(nextStep)
            updateGame(nextStep.game)
          case Right(nextGame) =>
            maybeStep = None
            updateGame(nextGame)
        }
    }
  }

  private def clickField(field: Int): Unit = {
    //updateGame(game.playField(field))
    maybeStep match {
      case Some(_) =>
        clickNext()
      case _ =>
        val step = Step.play(game, field)
        maybeStep = Some(step)
        updateGame(step.game)
    }
  }


  private def toggleComputer(): Unit ={
    computerEnabled = !computerEnabled
    moves = Nil
    renderGame()
  }

  private def renderGame(): Unit = {
    gameWrapper.innerHTML = ""
    for {
      line <- 0 to 3
    } yield {
      val lineDiv = View.createDiv("line")
      lineDiv.classList.add("line" + line)
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

    nextButton.innerHTML = maybeStep.map(step => s"Next (${step.taken})").mkString

    gameWrapper.appendChild(summary)
  }

  private def createField(field: Int): Span = {
    val fieldElement = View.createSpan("field")

    val fieldValue = game.fields(field)

    val showButton = maybeStep match {
      case Some(step) => step.actionField == field
      case _ => game.canPlayField(field)
    }

    if (showButton) {

      val fieldButton = View.createButton(fieldValue.toString)

      // visualize evaluation
      if (computerEnabled && maybeStep.isEmpty) {
        if (moves.isEmpty) {
          moves = ComputerPlayer.evaluateMoves(game)
        }
        val moveValue = moves.find(_.field == field).get.value
        val color = (moveValue * 4).min(255)
        val (r, g, b) = (255 - color, color, 0)
        fieldButton.style.backgroundColor = s"rgb($r,$g,$b)"
      } else {
        fieldButton.style.backgroundColor = s"darkgreen"
      }

      fieldButton.onclick = { _ => clickField(field) }
      fieldElement.appendChild(fieldButton)
    } else {
      val oneSpan = View.createSpan("number")
      if (fieldValue >= 1) {
        oneSpan.innerHTML = fieldValue.toString
        fieldElement.classList.add("nonempty")
      } else {
        oneSpan.innerHTML = "."
        fieldElement.classList.add("empty")
      }
      fieldElement.appendChild(oneSpan)
      fieldElement.classList.add("noaction")
    }

    if (maybeStep.exists(_.actionField == field)) {
      fieldElement.classList.add("active")
    }

    fieldElement
  }

  private def clickRestart(): Unit = {
    maybeStep = None
    updateGame(Game.initial)
  }

  private def updateGame(newGame: Game): Unit = {
    game = newGame
    moves = Nil
    renderGame()
  }

}
