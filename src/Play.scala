import hevs.graphics.FunGraphics

import java.awt.{Color, Rectangle}
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}
import java.time.LocalDateTime


object Play extends App {
  val fg: FunGraphics = new FunGraphics(800, 800)

  //val width and weight en fonctipon de taille et prientation
  val widthV = 80
  val height2V = 180
  val height3V = 280
  val heightH = 80
  val width2H = 180
  val width3H = 280

  //gerer key pressed only once and effect
  var pressedUp = false
  var pressedDown = false
  var pressedRight = false
  var pressedLeft = false
  var pressedQ = false
  var offsetH = 0
  var offsetV = 0
  var step = 1
  var keyHandled: Boolean = false

  var currentLevel: Int = 1
  var finishedLevel: Boolean = true //true pour lancer la première fois
  var finishedGame: Boolean = false
  var quit: Boolean = false
  var playground: Array[Array[Int]] = Array.ofDim(8, 8)
  val nbRow : Int = playground.length
  val nbCol : Int = playground(0).length


  //this handle the clavier
  val keyAdapter = new KeyAdapter() { // Will be called when a key has been pressed

    override def keyPressed(e: KeyEvent): Unit = {
      if (e.getKeyCode == KeyEvent.VK_RIGHT) pressedRight = true
      if (e.getKeyCode == KeyEvent.VK_LEFT) pressedLeft = true
      if (e.getKeyCode == KeyEvent.VK_UP) pressedUp = true
      if (e.getKeyCode == KeyEvent.VK_DOWN) pressedDown = true
      if (e.getKeyChar == 'q') pressedQ = true
    }

    override def keyReleased(e: KeyEvent): Unit = {
      if (e.getKeyCode == KeyEvent.VK_RIGHT) pressedRight = false
      if (e.getKeyCode == KeyEvent.VK_LEFT) pressedLeft = false
      if (e.getKeyCode == KeyEvent.VK_UP) pressedUp = false
      if (e.getKeyCode == KeyEvent.VK_DOWN) pressedDown = false
      if (e.getKeyChar == 'q') pressedQ = false
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

  def Car(ID: Int, width: Int, height: Int, posX: Int, posY: Int): Unit = {

    //Voitures verticales en fonction de l'ID
    if (ID >= 2 && ID <= 50) {
      var nbCasesV: Int = (height + 20) / 100
      for (i <- 0 until nbCasesV) {
        playground(posY+i)(posX) = ID
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
    else if (id >= 51 && id <= 100 || id == 1){
      moveH = true
    }


    // Déplacement UP
    if (dy == 1 && isEmpty(id,startY - 1,startX) && moveV) {
      playground(startY - 1)(startX) = id
      playground(startY + height - 1)(startX) = 0
    }
    //Déplacement DOWN
    else if (dy == -1 && isEmpty(id,startY+height,startX) && moveV){
      playground(startY)(startX) = 0
      playground(startY+height)(startX) = id
    }
    //Déplacement RIGHT
    else if(dx == 1 && isEmpty(id,startY,startX +width) && moveH){
      playground(startY)(startX) = 0
      playground(startY)(startX +width) = id
    }
    //Déplacement LEFT
    else if (dx == -1 && isEmpty(id,startY,startX-1) && moveH){
      playground(startY)(startX-1) = id
      playground(startY)(startX + width-1) = 0
    }

    println(playGround2Text())
    Level()
  }

  //vérifie que la prochaine cellule est disponible, et si RedCar que la sortie c'est ok
  def isEmpty(id:Int, nextY : Int, nextX : Int): Boolean ={
    var empty : Boolean = true
    val nextPos : Int = playground(nextY)(nextX)
    //Permet a la voiture rouge de passer par la sortie, et vides, mais pas murs ou autres voitures
    if (id == 1 || id == 2) {
      nextPos match {
        case 0 => empty = true
        case -2 => empty = true
        case _ => empty = false
      }
    }
      //Empeche de traverser les murs ou autres voitures
    else{
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


  def drawColorRect(posX: Int, posY: Int, width: Int, height: Int, color: Color): Unit = {
    fg.setColor(color)
    fg.drawFillRect(posX, posY, width, height)
  }

  //Affichage initial de chaque level
  def initLevel(level : Int) : Unit={
    //met une bordure au tableau
    for (col <- 0 until nbCol) {
      playground(0)(col) = -1
      playground(nbRow - 1)(col) = -1
    }
    for (row <- 0 until nbRow) {
      playground(row)(0) = -1
      playground(row)(nbCol - 1) = -1
    }
    level match {
      case 1 =>
        Car(3, widthV, height3V, 1, 3)
        Car(4, widthV, height2V, 2, 5)
        Car(5, widthV, height2V, 6, 1)
        Car(53, width2H, heightH, 1, 1)
        Car(54, width3H, heightH, 3, 1)
        //Car(55, width3H, heightH, 3, 4)
        //Car(56, width3H, heightH, 4, 6)
        //Voiture rouge
        Car(2, widthV, height2V, 4, 2)

        //sortie
        playground(7)(4) = -2

      case 2 =>
        Car(3,widthV,height2V,1,1)
        Car(4,widthV,height2V,3,5)
        //Car(5,widthV,height2V,4,2)
        //Car(6,widthV,height2V,5,3)
        //Car(7,widthV,height3V,6,2)
        Car(52, width3H,heightH,1,4)
        Car(53,width2H,heightH,1,6)
        Car(54,width3H,heightH,4,1)
        Car(55,width2H,heightH,5,5)
        Car(56,width2H,heightH,4,6)
        //voiture rouge
        Car(1,width2H,heightH,1,3)

        //sortie
        playground(3)(7) = -2
    }
  }

  //var changeLevel : Boolean = true


  do {
    if (finishedLevel){
      initLevel(currentLevel)
      finishedLevel = false
    }

    while (!finishedLevel && !quit) {
      // Logic déplacement + quitter
      if (pressedRight && keyHandled == false) {
        offsetH += step * 100
        // Pile l'endroit pour changer l'état de la voiture
        keyHandled = true
        moveCar(selectedCar, 1, 0)
      }
      if (pressedLeft && keyHandled == false) {
        offsetH -= step * 100 // Pile l'endroit pour changer l'état de la voiture
        keyHandled = true
        moveCar(selectedCar, -1, 0)
      }
      if (pressedUp && keyHandled == false) {
        offsetV -= step * 100
        // Pile l'endroit pour changer l'état de la voiture
        keyHandled = true
        moveCar(selectedCar, 0, 1)
      }
      if (pressedDown && keyHandled == false) {
        offsetV += step * 100
        // Pile l'endroit pour changer l'état de la voiture
        keyHandled = true
        moveCar(selectedCar, 0, -1)
      }
      if (pressedQ && keyHandled == false) {
        quit = true
        // Pile l'endroit pour changer l'état de la voiture
        keyHandled = true
      }

      //Gere quel level afficher
      Level()

      //refresh the screen at 60 FPS
      fg.syncGameLogic(120)
    }

    if (!quit && finishedLevel && currentLevel < 2){
      Thread.sleep(1000)
      fg.clear()
      fg.drawString(100, 200, s"Youpi ! Tu as fini le level $currentLevel !", Color.BLACK, 25)
      Thread.sleep(2000)
    }
    if (!quit && finishedLevel && currentLevel >= 2){
      finishedGame = true
    }
    currentLevel += 1
    playground = Array.ofDim(8, 8)
    //refresh the screen at 60 FPS
    //fg.syncGameLogic(60)
  } while (!quit && !finishedGame)

  if (finishedGame){
    fg.clear()
    fg.drawString(100,200,s"Suuuper ! Tu as fini le jeu !",Color.BLACK,25)
    fg.drawTransformedPicture(200,500,0,2,"/ISC_Logo2.png")
  }
  if (quit) {
    fg.clear()
    fg.drawFancyString(150, 200, "T'es une grosse merde !",Color.RED,40)
    fg.drawTransformedPicture(400, 500, 0, 2, "/ISC_Logo2.png")
    Thread.sleep(5000)
    System.exit(-1)
  }



  def Level(): Unit = {
    fg.frontBuffer.synchronized {
      fg.clear()

      //draw grille
      for (row <- 0 until nbRow) {
        for (col <- 0 until nbCol) {
          /** LA fonction drawRect prend d'abord X puis Y --> ça change l'orientation des voitures */

          /***///faire un match case pour chaque voiture

          //Murs noirs
          if (playground(row)(col) == -1) {
            //drawColorRect(col * 100, row * 100, 100, 100,Color.BLACK)
            fg.drawTransformedPicture(col*100+50,row*100+50,0,0.5,"/aleph_invert.png")
          }
          //Voitures de couleur
          else if (playground(row)(col) != 0) {
            //if vertical --> blue
            if (playground(row)(col) >= 2 && playground(row)(col) <= 50) {
              drawColorRect(col * 100, row * 100, 100, 100, Color.BLUE)
              //fg.drawTransformedPicture(col*100+50,row*100+50,0,0.5,"/Jacquemet3.png")
            }
            //if horizontal --> green
            if (playground(row)(col) >= 51 && playground(row)(col) <= 100) {
              drawColorRect(col * 100, row * 100, 100, 100, Color.GREEN)
              //fg.drawTransformedPicture(col*100+50,row*100+50,0,0.5,"/Jacquemet2_bis.png")
            }
            //RedCar
            if (playground(row)(col) == 2 || playground(row)(col) == 1) {
              drawColorRect(col * 100, row * 100, 100, 100, Color.RED)
              //fg.drawTransformedPicture(col*100+50,row*100+50,0,1,"/Mudry3.png")
            }
            //sortie
            if (playground(row)(col) == -2){
              fg.drawTransformedPicture(col*100+50,row*100+50,0,0.5,"/ISC_Logo2.png")
            }
          }

            //Dessine le fond des cases vides
          else {
            //fg.drawRect(row*100,col*100 , 100, 100)
          }

        }
      }

      if (currentLevel == 1 && playground(7)(4) == 2) {
        finishedLevel = true
      }

      if (currentLevel ==2 && playground(3)(7) == 1) {
        finishedLevel = true
      }
      if (currentLevel == 3 && playground(7)(4) == 2) {
        //finishLevel()
      }
    }
  }

}
