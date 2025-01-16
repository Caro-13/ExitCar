import Play.{currentLevel, fg, nbCol, nbRow, playground}
import Logic.{getHeight, getWidth, selectedCar}
import hevs.graphics.utils.GraphicsBitmap

import java.awt.Color

object Draw {
  var arrayToDisplay = 100

//permet d'optimiser l'affichage --> on n'ouvre qu'une fois l'image
  val imgSol : GraphicsBitmap = new GraphicsBitmap("/res/sol.png")
  val imgSortie : GraphicsBitmap = new GraphicsBitmap("/res/ISC_Logo.png")
  val imgBorder : GraphicsBitmap = new GraphicsBitmap("/res/aleph_invert.png")
  val imgRedCarV : GraphicsBitmap = new GraphicsBitmap("/res/RedCarV_Mudry.png")
  val imgRedCarH : GraphicsBitmap = new GraphicsBitmap("/res/RedCarH_Mudry.png")
  val imgBusV: GraphicsBitmap = new GraphicsBitmap("/res/BusV.png")
  val imgBusH : GraphicsBitmap = new GraphicsBitmap("/res/BusH.png")
  val imgGreenV : GraphicsBitmap = new GraphicsBitmap("/res/Car_PurpleV_X.png")
  val imgBlueH : GraphicsBitmap = new GraphicsBitmap("/res/Car_yellowH.png")

  val imgWelcome : GraphicsBitmap = new GraphicsBitmap("/res/welcome.png")
  val imgYoupi : GraphicsBitmap = new GraphicsBitmap("/res/confettis.png")
  val imgSuper : GraphicsBitmap = new GraphicsBitmap("/res/success.png")
  val imgReplay: GraphicsBitmap = new GraphicsBitmap("/res/replay.png")
  val imgBye : GraphicsBitmap = new GraphicsBitmap("/res/byebye.png")
  val imgQuit : GraphicsBitmap = new GraphicsBitmap("/res/poop.png")


  def msgWelcome():Unit={
    fg.drawString(250, 80, "Let's play ! !", Color.BLACK, 50)
    fg.drawTransformedPicture(400, 400, 0, 1.5, imgWelcome)
    Thread.sleep(2000)
    fg.clear()
  }

  //Affiche le sol
  def ground():Unit={
    fg.drawTransformedPicture(1 * 100 + 300, 1 * 100 + 300, 0, 1, imgSol)
  }

  //Affiche les bordures
  def borders(): Unit = {
    for (row <- 0 until nbRow) {
      for (col <- 0 until nbCol) {
        playground(row)(col) match {
          case -2 => //Sortie
            fg.drawTransformedPicture(col * 100 + 50, row * 100 + 50, 0, 0.5, imgSortie)

          case -1 => //Murs
            fg.drawTransformedPicture(col * 100 + 50, row * 100 + 50, 0, 0.5, imgBorder)

          case _ =>
        }
      }
    }
  }

  //Affiche les voitures et un petit cercle noir sur la voiture sélectionnée
  def bigCars(row: Int, col: Int): Unit = {
    val ID: Int = playground(row)(col)
    val offset: Int = 50
    val height: Int = getHeight(ID) * offset
    val width: Int = getWidth(ID) * offset

    //Pour que le cercle sur la voiture sélectionnée soit noir à coup sûr
    fg.setColor(Color.BLACK)

    ID match {
      case 1 => //RedCar Horizontal
        fg.drawTransformedPicture(col * arrayToDisplay + width, row * arrayToDisplay + height, 0, 0.08, imgRedCarH)
        if (ID == selectedCar) {
          fg.drawCircle(col * arrayToDisplay + width, row * arrayToDisplay + height - 5, 10)
        }

      case 2 => //RedCar Vertical
        fg.drawTransformedPicture(col * arrayToDisplay + width, row * arrayToDisplay + height, 0, 0.08, imgRedCarV)
        if (ID == selectedCar) {
          fg.drawCircle(col * arrayToDisplay + width - 5, row * arrayToDisplay + height - 10, 10)
        }


      case 20 | 21 | 22 | 23 | 24 | 25 | 26 | 27 | 28 | 29 => //Voitures Verticales taille 2
        fg.drawTransformedPicture(col * arrayToDisplay + width, row * arrayToDisplay + height, 0, 0.08, imgGreenV)
        if (ID == selectedCar) {
          fg.drawCircle(col * arrayToDisplay + width - 5, row * arrayToDisplay + height - 10, 10)
        }

      case 30 | 31 | 32 | 33 | 34 | 35 | 36 | 37 | 38 | 39 => //Voitures Verticales taille 3
        fg.drawTransformedPicture(col * arrayToDisplay + width, row * arrayToDisplay + height, 0, 1.6, imgBusV)
        if (ID == selectedCar) {
          fg.drawCircle(col * arrayToDisplay + width - 5, row * arrayToDisplay + height - 10, 10)
        }


      case 60 | 61 | 62 | 63 | 64 | 65 | 66 | 67 | 68 | 69 => //Voitures Horizontales taille 2
        fg.drawTransformedPicture(col * arrayToDisplay + width, row * arrayToDisplay + height, 0, 0.08, imgBlueH)
        if (ID == selectedCar) {
          fg.drawCircle(col * arrayToDisplay + width, row * arrayToDisplay + height - 5, 10)
        }

      case 70 | 71 | 72 | 73 | 74 | 75 | 76 | 77 | 78 | 79 => //Voitures Horizontales taille 3
        fg.drawTransformedPicture(col * arrayToDisplay + width, row * arrayToDisplay + height, 0, 1.6, imgBusH)
        if (ID == selectedCar) {
          fg.drawCircle(col * arrayToDisplay + width, row * arrayToDisplay + height - 5, 10)
        }

      case _ =>
    }
  }

  //Message de réussite de niveau
  def msgYoupi():Unit={
    fg.clear()
    fg.drawTransformedPicture(200, 500, 0, 2, imgYoupi)
    fg.drawString(180, 400, s"Youpi ! Tu as fini le level $currentLevel !", Color.BLACK, 40)
    Thread.sleep(2000)
    fg.clear()
  }
  //Message de réeussite du jeu
  def msgSuuuper():Unit ={
    fg.clear()
    fg.drawTransformedPicture(400, 400, 0, 1, imgSuper)
    fg.drawString(200, 200, s"Suuuper ! Tu as fini le jeu !", Color.BLACK, 25)
  }
  //Message pour rejouer
  def msgReplay():Unit={
    fg.clear()
    fg.drawTransformedPicture(400, 400, 0, 2, imgReplay)
    fg.drawString(100, 200, s"Tu veux rejouer ?", Color.BLACK, 25)
  }
  //Message au revoir si réussite et ne rejoue pas
  def msgByebye():Unit={
    fg.clear()
    fg.drawTransformedPicture(550, 300, 0, 2, imgBye)
    fg.drawString(150, 600, "Merci d'avoir joué, à bientôt !", Color.BLACK, 40)
  }
  //Message si abandonne le jeu avant la fin
  def msgQuit():Unit={
    fg.clear()
    fg.drawFancyString(150, 200, "T'es une grosse merde !", Color.RED, 40)
    fg.drawTransformedPicture(400, 450, 0, 2, imgQuit)
  }

}
