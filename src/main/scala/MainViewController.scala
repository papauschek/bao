import org.scalajs.dom
import org.scalajs.dom.{Element, window}
import org.scalajs.dom.html.{Button, Div, Span}

class MainViewController() {

  val wrapper: Element = View.createDiv("wrapper")
  val restartButton: Button = View.createButton("Restart", "menu-button")
  val nextButton: Button = View.createButton("Next", "menu-button")
  val evaluationButton: Button = View.createButton("Eval", "menu-button")
  val computerButton: Button = View.createButton("CPU Player", "menu-button")
  val difficultyButton: Button = View.createButton("CPU Difficulty 1", "menu-button")
  val speedButton: Button = View.createButton("2x Speed", "menu-button")
  val gameWrapper: Element = View.createDiv("game-wrapper")

  var game: Game = Game.initial
  var maybeStep: Option[Step] = None
  var moves: Seq[EvaluatedMove] = Nil

  var computerEnabled: Boolean = false
  var evaluationEnabled: Boolean = false
  var speed: Int = 2
  var iteration: Int = 0
  var difficulty = 1

  val isLocal: Boolean = window.location.hostname.isEmpty

  def init(): Unit = {
    dom.document.body.appendChild(wrapper)
    wrapper.appendChild(restartButton)

    if (isLocal) {
      wrapper.appendChild(evaluationButton)
    }

    wrapper.appendChild(computerButton)
    wrapper.appendChild(difficultyButton)
    wrapper.appendChild(speedButton)
    wrapper.appendChild(gameWrapper)

    // menu
    restartButton.onclick = { _ => clickRestart() }
    nextButton.onclick = { _ => clickNext() }
    evaluationButton.onclick = { _ => toggleEvaluation() }
    computerButton.onclick = { _ => toggleComputer() }
    speedButton.onclick = { _ => toggleSpeed() }
    difficultyButton.onclick = { _ => toggleDifficulty() }

    renderGame()

    dom.window.setInterval(() => tick(), 200)
  }

  private def tick(): Unit = {

    iteration += 1

    if (iteration % (4 / speed) == 0) {
      clickNext()
      computerPlay()
    }

  }

  private def evalMoves(): Seq[EvaluatedMove] = {
    if (moves.isEmpty) {
      moves = ComputerPlayer.evaluateMoves(game, maxDepth = difficulty - 1)
    }
    moves
  }

  private def computerPlay(): Unit = {
    if (computerEnabled && game.player == 1 && maybeStep.isEmpty) {
      if (moves.isEmpty) {
        evalMoves()
        iteration = 0
      } else {
        moves.maxByOption(_.value).foreach {
          bestMove => clickField(bestMove.field)
        }
      }
    }
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
    maybeStep match {
      case Some(_) =>
        clickNext()
      case _ =>
        val step = Step.play(game, field)
        maybeStep = Some(step)
        iteration = 0
        updateGame(step.game)
    }
  }

  private def toggleComputer(): Unit ={
    computerEnabled = !computerEnabled
    moves = Nil
    renderGame()
  }

  private def toggleSpeed(): Unit ={
    speed = if (speed <= 2) speed * 2 else 1
    speedButton.innerHTML = s"${speed}x Speed"
  }

  private def toggleDifficulty(): Unit ={
    difficulty = difficulty % 6 + 1
    difficultyButton.innerHTML = s"CPU Difficulty $difficulty"
    moves = Nil
    renderGame()
  }


  private def toggleEvaluation(): Unit ={
    evaluationEnabled = !evaluationEnabled
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

    if (computerEnabled) {
      computerButton.style.backgroundColor = "lightgreen"
    } else {
      computerButton.style.backgroundColor = "white"
    }

    if (evaluationEnabled) {
      evaluationButton.style.backgroundColor = "lightgreen"
    } else {
      evaluationButton.style.backgroundColor = "white"
    }

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

      val fieldButton = View.createButton(fieldValue.toString, "field-button")

      // visualize evaluation
      if (evaluationEnabled && maybeStep.isEmpty) {
        val moveValue = evalMoves().find(_.field == field).get.value
        val color = (moveValue * 4).min(255)
        val (r, g, b) = (255 - color, color, 0)
        fieldButton.style.backgroundColor = s"rgb($r,$g,$b)"
      } else {
        fieldButton.style.backgroundColor = s"#5a1b82"
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

      if (field / 16 == game.player) {
        fieldElement.style.backgroundColor = "#230a33"
      }
    }

    maybeStep match {
      case Some(step) if step.actionField == field =>
        fieldElement.classList.add("active")
        val hands = View.createDiv("hands")
        hands.innerHTML = step.taken.toString
        fieldElement.appendChild(hands)

      case _ =>
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
