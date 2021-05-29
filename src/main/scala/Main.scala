import cats.effect.kernel.Resource
import cats.effect.{IO, IOApp, Sync}
import fs2.Stream

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
      _  <- isLessThanTwo
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
}
