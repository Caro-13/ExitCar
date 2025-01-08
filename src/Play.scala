import hevs.graphics.FunGraphics
import java.awt.event.{KeyAdapter, KeyEvent}
object Play extends App {
  val fg: FunGraphics = new FunGraphics(800, 800)
  var offset = 0
  var test = true

  var currentLevel : Int = 1
  var finishedLevel : Boolean = false
  var finishedGame : Boolean = false
  var quit : Boolean = false
  var playground : Array[Array[Int]] =  Array.ofDim(8,8)

  val keyAdapter = new KeyAdapter() { // Will be called when a key has been pressed
    override def keyPressed(e: KeyEvent): Unit = {
      if (e.getKeyChar == 'a') println("The key 'A' was pressed")
      if (e.getKeyCode == KeyEvent.VK_RIGHT) offset += 1
      if (e.getKeyCode == KeyEvent.VK_LEFT) offset -= 1
      if (e.getKeyChar == 's') test = false
    }
  }
  fg.setKeyManager(keyAdapter)


  while (test) {
    fg.clear()
    //draw our object
    fg.drawRect(50 + offset * 2, 50 , 75, 75)
    //refresh the screen at 60 FPS
    fg.syncGameLogic(60)

  }

  do {

  }while (!quit)


}
