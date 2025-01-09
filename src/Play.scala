import hevs.graphics.FunGraphics
import math.round

import java.awt.{Color, Rectangle}
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}

object Play extends App {
  val fg: FunGraphics = new FunGraphics(800, 800)

  //val width and weight en fonctipon de taille et prientation
  val widthV = 80
  val height2V = 180
  val height3V = 280
  val heightH = 80
  val width2H = 180
  val width3H = 280

  //gerer blink
  var pressedUp = false
  var pressedDown = false
  var pressedRight = false
  var pressedLeft = false
  var pressedQ = false
  var direction = 100
  var offsetH = 0
  var offsetV = 0
  var step = 1

  var currentLevel: Int = 1
  var finishedLevel: Boolean = false
  var finishedGame: Boolean = false
  var quit: Boolean = false
  var playground: Array[Array[Int]] = Array.ofDim(8, 8)
  val nbRow : Int = playground.length
  val nbCol : Int = playground(0).length

  var selectedCar: Int = selectCar()
  var keyHandled: Boolean = false

  //this handle the clavier
  val keyAdapter = new KeyAdapter() { // Will be called when a key has been pressed

    override def keyPressed(e: KeyEvent): Unit = {
      if (e.getKeyChar == 'a') println("The key 'A' was pressed")
      if (e.getKeyCode == KeyEvent.VK_RIGHT) pressedRight = true
      if (e.getKeyCode == KeyEvent.VK_LEFT) pressedLeft = true
      if (e.getKeyCode == KeyEvent.VK_UP) pressedUp = true
      if (e.getKeyCode == KeyEvent.VK_DOWN) pressedDown = true
      if (e.getKeyChar == 's') pressedQ = true
    }

    override def keyReleased(e: KeyEvent): Unit = {
      if (e.getKeyChar == 'a') println("The key 'A' was pressed")
      if (e.getKeyCode == KeyEvent.VK_RIGHT) pressedRight = false
      if (e.getKeyCode == KeyEvent.VK_LEFT) pressedLeft = false
      if (e.getKeyCode == KeyEvent.VK_UP) pressedUp = false
      if (e.getKeyCode == KeyEvent.VK_DOWN) pressedDown = false
      if (e.getKeyChar == 's') pressedQ = false
      keyHandled = false
    }
  }
  fg.setKeyManager(keyAdapter)

  var posYMouse : Int = 0
  var posXMouse : Int = 0

  //this handle the click of the mouse
  val mouseAdapter = new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      val event = e
      // Get the mouse position from the event
      val posx = event.getX
      val posy = event.getY
      posYMouse = posy
      posXMouse = posx
      selectedCar = selectCar()
      println(s"Mouse position $posx - $posy")
    }
  }
  fg.addMouseListener(mouseAdapter)

  //Get the ID of the car selected (from mouse click)
  def selectCar(): Int = {
    val posYSelect: Int = posYMouse / 100
    val posXSelect: Int = posXMouse / 100
    playground(posYSelect)(posXSelect)
  }

  def Car(ID: Int, width: Int, height: Int, posX: Int, posY: Int): Unit = {
    val posXInGraph: Int = posX * 100 +10
    val posYInGraph: Int = posY * 100 +10

    //Voitures verticales en fonction de l'ID
    if (ID >= 2 && ID <= 50) {
      var nbCasesV: Int = (height + 20) / 100
      for (i <- 0 until nbCasesV) {
        playground(posX + i)(posY) = ID
      }
      //fg.drawFillRect(posXInGraph, posYInGraph, width, height)
    }

    //Voitures horizontales en fonction de l'ID
    else if (ID >= 51 && ID <= 100) {
      var nbCasesH: Int = (width + 20) / 100
      for (j <- 0 until nbCasesH) {
        playground(posX)(posY + j) = ID
      }
      //fg.drawFillRect(posXInGraph, posYInGraph, width, height)
    }
    //fg.drawFillRect()
  }

  //Find car
  def findCar(id : Int): Array[Int]={
    // Trouver la voiture, la déplacer dans le sens dx,dy
    var startX: Int = 0
    var startY: Int = 0
    var found: Boolean = false
    val coordCar : Array[Int] = new Array[Int](2)

    // Moche mais ça marche : trouve le début de la voiture id
    for (row <- 0 until playground.length) {
      for (col <- 0 until playground(0).length) {
        if (playground(row)(col) == id && found == false) {
          startY = row
          startX = col
          found = true
        }
      }
    }
    coordCar(0) = startX
    coordCar(1) = startY
    coordCar
  }

  // Find height of car
  def getHeight(id: Int): Int = {
    val startX = findCar(id)(0)
    val startY = findCar(id)(1)
    var finished: Boolean = false
    var height: Int = 1
    for (row <- startY + 1 until playground.length) {
      if (playground(row)(startX) == id && finished == false)
        height += 1
      else
        finished = true
    }
    println(height)
    height
  }
  // Find width of car
  def getWidth(id: Int): Int = {
    val startX = findCar(id)(0)
    val startY = findCar(id)(1)
    var finished: Boolean = false
    var width: Int = 1
    for (col <- startX + 1 until playground(0).length) {
      if (playground(startY)(col) == id && finished == false)
        width += 1
      else
        finished = true
    }
    println(width)
    width
  }

  //Gere le déplacement de la voiture
  def moveCar(id: Int, dx: Int, dy: Int): Unit = {
    val height : Int = getHeight(id)
    val width : Int = getWidth(id)
    val startX = findCar(id)(0)
    val startY = findCar(id)(1)

    //Contrainte déplacement soit horizontal soit vertical en fonction de l'id
    var moveH : Boolean = false
    var moveV : Boolean = false
    if (id >= 2 && id <= 50){
      moveV = true
    }
    else if (id >= 51 && id <= 100){
      moveH = true
    }


    // Déplacement UP
    if (dy == 1 && isEmpty(startY - 1,startX) && moveV) {
      playground(startY - 1)(startX) = id
      playground(startY + height - 1)(startX) = 0
    }
    //Déplacement DOWN
    else if (dy == -1 && isEmpty(startY+height,startX) && moveV){
      playground(startY)(startX) = 0
      playground(startY+height)(startX) = id
    }
    //Déplacement RIGHT
    else if(dx == 1 && isEmpty(startY,startX +width) && moveH){
      playground(startY)(startX) = 0
      playground(startY)(startX +width) = id
    }
    //Déplacement LEFT
    else if (dx == -1 && isEmpty(startY,startX-1) && moveH){
      playground(startY)(startX-1) = id
      playground(startY)(startX + width-1) = 0
    }

    println(playGround2Text())
    println(s"x:${findCar(id)(0)} y:${findCar(id)(1)} ")
  }

    //vérifie que la prochaine cellule est disponible
  def isEmpty(nextY : Int, nextX : Int): Boolean ={
    var empty : Boolean = true
    if (playground(nextY)(nextX) != 0){
      empty = false
    }
    empty
  }

  //Affiche le playground sur la console
  def playGround2Text(): String = {
    var temp: String = ""
    for (i <- playground.indices) {
      temp += playground(i).mkString(",")
      temp = temp + "\n"
    }
    temp
  }


  //met une bordure au tableau
  for (col<- 0 until nbCol){
    playground(0)(col) = -1
    playground(nbRow-1)(col) = -1
  }
  for (row <-0 until  nbRow){
    playground(row)(0) = -1
    playground(row)(nbCol-1) = -1
  }


  //Test
  //Car(3, widthV, height3V, 1, 1)
  //Car(53, width3H, heightH, 3, 3)
  println(playGround2Text())
  //println(selectedCar)

  def initLevel(level : Int) : Unit={
    if (level == 1){
      Car(3, widthV, height3V, 1, 1)
      Car(53, width2H, heightH, 3, 3)
    }
    else {
      Car(4,widthV, height2V, 2, 2)
    }
  }


  initLevel(currentLevel)
  do {
    // Logic déplacement + quitter
    if (pressedRight && keyHandled == false) {
      offsetH += step*100
      // Pile l'endroit pour changer l'état de la voiture
      keyHandled = true
      moveCar(selectedCar, 1, 0)
    }
    if (pressedLeft && keyHandled == false) {
      offsetH -= step*100      // Pile l'endroit pour changer l'état de la voiture
      keyHandled = true
      moveCar(selectedCar, -1, 0)
    }
    if (pressedUp && keyHandled == false) {
      offsetV -= step*100
      // Pile l'endroit pour changer l'état de la voiture
      keyHandled = true
      moveCar(selectedCar, 0, 1)
    }
    if (pressedDown && keyHandled == false) {
      offsetV += step*100
      // Pile l'endroit pour changer l'état de la voiture
      keyHandled = true
      moveCar(selectedCar, 0, -1)
    }
    if (pressedQ && keyHandled == false) {
      quit = true
      // Pile l'endroit pour changer l'état de la voiture
      keyHandled = true
    }

    Level1()

    //refresh the screen at 60 FPS
    fg.syncGameLogic(60)
  } while (!quit)

  fg.clear()
  //System.exit(-1)



  def Level1(): Unit = {
    fg.clear()
    //draw our object
    //fg.drawFillRect(110 + offsetH, 110 + offsetV, widthV, height3V)


    //draw grille
    for (row <- 0 until  nbRow) {
      for (col <- 0 until nbCol) {
        if (playground(row)(col) == -1){
          fg.drawFillRect(row*100,col*100 , 100, 100)
        }
        else if(playground(row)(col) != 0){
          drawColorRect(row*100,col*100 , 100, 100, Color.BLUE)
        }

        else{
          fg.drawRect(row*100,col*100 , 100, 100)
        }

      }
    }


    def drawColorRect(posX: Int, posY: Int, width: Int, height: Int,color: Color): Unit ={
      for (row <- 0 until width){
        for (col <- 0 until height){
          fg.setPixel(posY+row,posX+col,color)
        }
      }
    }

  }
}
