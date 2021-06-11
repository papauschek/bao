import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.Button

class MainViewController() {

  val wrapper: Element = View.createDiv("wrapper")
  val restartButton: Button = View.createButton("Restart")

  def init(): Unit = {
    dom.document.body.appendChild(wrapper)
    wrapper.appendChild(restartButton)

    // menu
    restartButton.onclick = { _ => clickRestart() }
    restartButton.onmouseover = { _ => println("mouseover") }
  }

  private def clickRestart(): Unit = {
    dom.window.alert("restart")
  }


}
