import cats.effect.{IO, IOApp, Resource, Sync}
import fs2.Stream

import scala.concurrent.duration._
import scala.io.Source

object Main extends IOApp.Simple {

  override def run: IO[Unit] = {
    val firstStream = Stream(1,2,3).toList
    val emitStream = Stream.emit(1, 1, 3, 4).toList
    val repeatedStream = Stream.range(1, 4).repeat.take(6).toVector
    val evalStream = Stream.eval[IO, String](IO("Hello"))

    for {
      _  <- IO.println(s"I am just a stream ${firstStream}")
      _  <- IO.println(s"I emit some stream ${emitStream}")
      _  <- IO.println(s"The type of the first Stream is ${firstStream.getClass}")
      _  <- IO.println(s"The type of the second Stream is ${emitStream.getClass}")
      _  <- IO.println(s"This is a repeating Stream ${repeatedStream}")
      _  <- IO.println(evalStream)
//      _  <- isLessThanTwo
//      _  <- printTimeEverySeconds
      _  <- learningHowToUserResource.use(whatIsThis => IO.println(whatIsThis))
      _    <- useResourceToReadFileContent("./src/main/resources/names.txt").use(theLines => for {
        lines <- Sync[IO].delay(theLines.getLines())
       _  <- Sync[IO].delay(lines.foreach(println))
      } yield ())
    } yield ()
  }

  def generateRandomNumber : IO[Double] = {
    IO.pure(math.random() * 4)
  }

  def isLessThanTwo: IO[Unit] = {
    generateRandomNumber.flatMap {
      case value if value < 2 => Sync[IO].raiseError(new Exception(s"$value was less than two "))
      case rest => IO.println(rest)
    }
  }

  def tickingClock: IO[Unit] = {
    IO.println(
      System.currentTimeMillis()
    )
  }

  def printTimeEverySeconds: IO[Unit] = {
    Sync[IO].sleep(1.seconds) >> tickingClock >> printTimeEverySeconds
  }

  def hmmClock: IO[Unit] = {
    for {
      _ <- Sync[IO].sleep(1.seconds)
      _ <- IO.println(System.currentTimeMillis())
      _ <- tickingClock
    } yield ()
  }

  def learningHowToUserResource: Resource[IO, String] = {
    Resource.make[IO, String](IO.println("This is the acquiring stage") *> IO("String"))(released => {
      IO.println(s"This is what was released ${released}")
    })
  }

  def useResourceToReadFileContent(path: String) = {
    Resource.make(IO.println(s"Acquiring resource from ${path}") *> IO.blocking(Source.fromFile(path)).
      onError(errorMessage =>
        Sync[IO].delay(println(s"An error occurred and this is the message ${errorMessage}"))))(cleanUp =>
      IO.println("Closing resource") *> Sync[IO].delay(cleanUp.close())
    )
  }
}
