import org.scalajs.dom
import org.scalajs.dom.html.{Button, Div, Span}

object View {

  def createSpan(className: String): Span = {
    val element = dom.document.createElement("span").asInstanceOf[Span]
    element.classList.add(className)
    element
  }

  def createDiv(className: String): Div = {
    val element = dom.document.createElement("div").asInstanceOf[Div]
    element.classList.add(className)
    element
  }

  def createButton(text: String): Button = {
    val element = dom.document.createElement("button").asInstanceOf[Button]
    element.textContent = text
    element.classList.add("game-button")
    element
  }

}
