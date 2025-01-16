import Logic.{buttonPressed, keyHandled, pressedN, pressedY}
import hevs.graphics.FunGraphics



object Play extends App {
  val fg: FunGraphics = new FunGraphics(800, 800,"ExitCar")

  var answeredReplay: Boolean = false

  var currentLevel: Int = 0
  var finishedLevel: Boolean = true //true pour lancer la première fois
  var finishedGame: Boolean = false
  val finalLevel: Int = 3 //Nb de niveau pour finir le jeu
  var quit: Boolean = false
  var play: Boolean = true
  var playground: Array[Array[Int]] = Array.ofDim(8, 8)
  val nbRow: Int = playground.length
  val nbCol: Int = playground(0).length


  //Le Jeu
  Draw.msgWelcome()
  while (play) {
    do {
      Logic.beginLevel()

      //Une partie parmi nb finalLevel de parties
      while (!finishedLevel && !quit) {

        // Logic déplacement + quitter
        Logic.buttonPressed()
        Logic.checkLevelFinished()

        // Dessine le niveau
        level()

        //refresh the screen at 60 FPS
        fg.syncGameLogic(60)
      }
      playground = Array.ofDim(8, 8)

      //passe au niveau suivant si fini level < level final
      if (!quit && finishedLevel && currentLevel < finalLevel) {
        Thread.sleep(1000)
        Draw.msgYoupi()
      }
      //fini le jeu si fini le level final
      if (!quit && finishedLevel && currentLevel >= finalLevel) {
        finishedGame = true
      }
      if (quit) {
        play = false
      }

      Logic.checkGameFinished()
      Thread.sleep(10)
    } while (!quit && !finishedGame)

    //fin du jeu puis si recommencer ou non
    if (finishedGame) {
      Draw.msgSuuuper()
      Thread.sleep(3000)
      Draw.msgReplay()
      while (!answeredReplay) {
        Thread.sleep(10)
        wannaReplay()
      }
    }
      answeredReplay = false

  }

  //A fini le jeu et ne recommence pas
  if (!play && !quit && finishedGame) {
    Draw.msgByebye()
    Thread.sleep(1000)
    System.exit(-1)
  }

  //A quitté le jeu avant la fin
  if (quit) {
    Draw.msgQuit()
    Thread.sleep(5000)
    System.exit(-1)
  }

  //Gere l'affichage en fonction du tableau
  def level(): Unit = {
    fg.frontBuffer.synchronized {
      fg.clear()

      Draw.ground()
      Draw.borders()

      //Affiche voitures
      for (i <- Logic.voituresLa.indices) {
        val id: Int = Logic.voituresLa(i)
        val row: Int = Logic.findCar(id)(1)
        val col: Int = Logic.findCar(id)(0)
        Draw.bigCars(row, col)
      }
      Thread.sleep(10)
    }
  }

      /** Early optimization is the root of all evil */ //Paroles d'un sage...

      //Vérifie qu'il y a une réponse a la question rejouer
      def wannaReplay(): Unit = {
        Thread.sleep(10)
        if (pressedY && !keyHandled) {
          Thread.sleep(10)
          println("Y")
          currentLevel = 0
          finishedLevel = true
          finishedGame = false
          play = true
          keyHandled = true
          answeredReplay = true
        }
        if (pressedN && !keyHandled) {
          println("N")
          Thread.sleep(10)
          play = false
          keyHandled = true
          answeredReplay = true
        }
        buttonPressed()
      }
  }


