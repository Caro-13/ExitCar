import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}
import Play.{fg,playground,currentLevel,finishedLevel,finishedGame,finalLevel,nbRow,nbCol,play,quit}
import java.awt.{Color}

object Logic {
  //val width and height en fonction de taille et orientation
  val widthV = 100
  val height2V = 200
  val height3V = 300
  val heightH = 100
  val width2H = 200
  val width3H = 300

  //Handles key pressed only once and effect
  var pressedUp = false
  var pressedDown = false
  var pressedRight = false
  var pressedLeft = false
  var pressedQ = false
  var pressedY = false
  var pressedN = false
  var offsetH = 0
  var offsetV = 0
  var step = 1
  var keyHandled: Boolean = false
  var answeredReplay: Boolean = false

  //Pour afficher la voiture en grand
  var voituresLa: Array[Int] = Array(14)



  //This handles the clavier
  val keyAdapter = new KeyAdapter() { // Will be called when a key has been pressed

    override def keyPressed(e: KeyEvent): Unit = {
      if (e.getKeyCode == KeyEvent.VK_RIGHT) pressedRight = true
      if (e.getKeyCode == KeyEvent.VK_LEFT) pressedLeft = true
      if (e.getKeyCode == KeyEvent.VK_UP) pressedUp = true
      if (e.getKeyCode == KeyEvent.VK_DOWN) pressedDown = true
      if (e.getKeyChar == 'q') pressedQ = true
      if (e.getKeyChar == 'y') pressedY = true
      if (e.getKeyChar == 'n') pressedN = true
    }

    override def keyReleased(e: KeyEvent): Unit = {
      if (e.getKeyCode == KeyEvent.VK_RIGHT) pressedRight = false
      if (e.getKeyCode == KeyEvent.VK_LEFT) pressedLeft = false
      if (e.getKeyCode == KeyEvent.VK_UP) pressedUp = false
      if (e.getKeyCode == KeyEvent.VK_DOWN) pressedDown = false
      if (e.getKeyChar == 'q') pressedQ = false
      if (e.getKeyChar == 'y') pressedY = false
      if (e.getKeyChar == 'n') pressedN = false
      keyHandled = false
    }
  }
  fg.setKeyManager(keyAdapter)

  //This handles the effects of the clavier use
  def buttonPressed(): Unit = {
    if (pressedRight && keyHandled == false) {
      offsetH += step * 100
      keyHandled = true
      moveCar(selectedCar, 1, 0)
    }
    if (pressedLeft && keyHandled == false) {
      offsetH -= step * 100
      keyHandled = true
      moveCar(selectedCar, -1, 0)
    }
    if (pressedUp && keyHandled == false) {
      offsetV -= step * 100
      keyHandled = true
      moveCar(selectedCar, 0, 1)
    }
    if (pressedDown && keyHandled == false) {
      offsetV += step * 100
      keyHandled = true
      moveCar(selectedCar, 0, -1)
    }
    if (pressedQ && keyHandled == false) {
      Thread.sleep(10)
      quit = true
      keyHandled = true
    }

  }


  var posYMouse: Int = 0
  var posXMouse: Int = 0

  //This handles the mouse
  val mouseAdapter: MouseAdapter = new MouseAdapter {
    var i: Int = 0;

    override def mousePressed(e: MouseEvent): Unit = {
      val event = e
      // Get the mouse position from the event
      val posx = event.getX
      val posy = event.getY
      posYMouse = posy
      posXMouse = posx
      selectedCar = selectCar()
    }
  }
  fg.addMouseListener(mouseAdapter)

  var selectedCar: Int = selectCar()

  //Get the ID of the car selected (from mouse click)
  def selectCar(): Int = {
    val posYSelect: Int = posYMouse / 100
    val posXSelect: Int = posXMouse / 100
    playground(posYSelect)(posXSelect)
  }

  //Déclaration de la voiture et mise dans le tableau playground
  def car(ID: Int, width: Int, height: Int, posX: Int, posY: Int): Unit = {

    //Voitures verticales en fonction de l'ID
    if (ID >= 2 && ID <= 50) {
      var nbCasesV: Int = (height + 20) / 100
      for (i <- 0 until nbCasesV) {
        playground(posY + i)(posX) = ID
      }
    }

    //Voitures horizontales en fonction de l'ID
    else if ((ID == 1) || ID >= 51 && ID <= 100) {
      var nbCasesH: Int = (width + 20) / 100
      for (j <- 0 until nbCasesH) {
        playground(posY)(posX + j) = ID
      }
    }

    //Voiture Rouge (deux ID : 2 = vertical, ID = 1 horizontal)
  }

  //Trouver la voiture, la déplacer dans le sens dx,dy
  def findCar(id: Int): Array[Int] = {
    var startX: Int = 0
    var startY: Int = 0
    var found: Boolean = false
    val coordCar: Array[Int] = new Array[Int](2)

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

  // Calculate height of car
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
    height
  }

  // Calculate width of car
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
    width
  }

  //Gere le déplacement de la voiture
  def moveCar(id: Int, dx: Int, dy: Int): Unit = {
    val height: Int = getHeight(id)
    val width: Int = getWidth(id)
    val startX = findCar(id)(0)
    val startY = findCar(id)(1)

    //Contrainte déplacement soit horizontal soit vertical en fonction de l'id
    var moveH: Boolean = false
    var moveV: Boolean = false
    if (id >= 2 && id <= 50) {
      moveV = true
    }
    else if (id >= 51 && id <= 100 || id == 1) {
      moveH = true
    }

    // Déplacement UP
    if (dy == 1 && isEmpty(id, startY - 1, startX) && moveV) {
      playground(startY - 1)(startX) = id
      playground(startY + height - 1)(startX) = 0
    }
    //Déplacement DOWN
    else if (dy == -1 && isEmpty(id, startY + height, startX) && moveV) {
      playground(startY)(startX) = 0
      playground(startY + height)(startX) = id
    }
    //Déplacement RIGHT
    else if (dx == 1 && isEmpty(id, startY, startX + width) && moveH) {
      playground(startY)(startX) = 0
      playground(startY)(startX + width) = id
    }
    //Déplacement LEFT
    else if (dx == -1 && isEmpty(id, startY, startX - 1) && moveH) {
      playground(startY)(startX - 1) = id
      playground(startY)(startX + width - 1) = 0
    }

    println(playGround2Text())
  }

  //vérifie que la prochaine cellule est disponible, et si RedCar que la sortie c'est ok
  def isEmpty(id: Int, nextY: Int, nextX: Int): Boolean = {
    var empty: Boolean = true
    val nextPos: Int = playground(nextY)(nextX)
    //Permet a la voiture rouge de passer par la sortie, et vides, mais pas murs ou autres voitures
    if (id == 1 || id == 2) {
      nextPos match {
        case 0 => empty = true
        case -2 => empty = true
        case _ => empty = false
      }
    }
    //Empeche de traverser les murs ou autres voitures
    else {
      if (nextPos != 0) {
        empty = false
      }
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

  //Affichage initial de chaque level
  def initLevel(level: Int): Unit = {
    //met une bordure au tableau
    for (col <- 0 until nbCol) {
      playground(0)(col) = -1
      playground(nbRow - 1)(col) = -1
    }
    for (row <- 0 until nbRow) {
      playground(row)(0) = -1
      playground(row)(nbCol - 1) = -1
    }

    /** Pour faciliter gestion humaine des voitures :
     * 1 : RedCar Horizontale
     * 2 : RedCar Verticale
     * 20-29 : Verticales - taille 2
     * 30-39 : Verticales - taille 3
     * 60-69 : Horizontales - taille 2
     * 70-79 : Horizontales - taille 3
     */
    level match {
      case 1 =>
        car(30, widthV, height3V, 1, 3)
        car(20, widthV, height2V, 2, 5)
        car(21, widthV, height2V, 6, 1)
        car(60, width2H, heightH, 1, 1)
        car(71, width3H, heightH, 3, 1)
        car(72, width3H, heightH, 3, 4)
        car(73, width3H, heightH, 4, 6)
        //RedCar
        car(2, widthV, height2V, 4, 2)
        //sortie
        playground(7)(4) = -2

        voituresLa = Array(30, 20, 21, 60, 71, 72, 73, 2)

      case 2 =>
        car(20, widthV, height2V, 1, 1)
        car(21, widthV, height2V, 3, 5)
        car(22, widthV, height2V, 4, 2)
        car(23, widthV, height2V, 5, 3)
        car(34, widthV, height3V, 6, 2)
        car(70, width3H, heightH, 1, 4)
        car(60, width2H, heightH, 1, 6)
        car(71, width3H, heightH, 4, 1)
        car(61, width2H, heightH, 5, 5)
        car(62, width2H, heightH, 4, 6)
        //RedCar
        car(1, width2H, heightH, 1, 3)
        //sortie
        playground(3)(7) = -2
        voituresLa = Array(20, 21, 22, 23, 34, 70, 60, 71, 61, 62, 1)

      case 3 =>
        car(20, widthV, height2V, 1, 3)
        car(21, widthV, height2V, 3, 2)
        car(60, width2H, heightH, 1, 2)
        car(70, width3H, heightH, 2, 4)
        car(71, width3H, heightH, 1, 6)
        //RedCar
        car(2, widthV, height2V, 4, 2)
        //sortie
        playground(7)(4) = -2
        voituresLa = Array(20, 21, 60, 70, 71, 2)
      case _ =>
        System.exit(-1)

    }
  }

  //Gère quel niveau afficher et affiche
  def beginLevel():Unit={
    currentLevel += 1
    //Affiche le début du niveau
    if (finishedLevel) {
      Logic.initLevel(currentLevel)
      finishedLevel = false
    }
  }

  //Gere la réussite du level
  def checkLevelFinished(): Boolean = {
    if (currentLevel == 1 && playground(7)(4) == 2) {
      finishedLevel = true
    }
    if (currentLevel == 2 && playground(3)(7) == 1) {
      finishedLevel = true
    }
    if (currentLevel == 3 && playground(7)(4) == 2) {
      finishedLevel = true
    }
    false
  }

  //Gere la réussite du jeu
  def checkGameFinished(): Boolean = {
    if (currentLevel == finalLevel && finishedLevel) finishedGame = true
    false
  }



}
