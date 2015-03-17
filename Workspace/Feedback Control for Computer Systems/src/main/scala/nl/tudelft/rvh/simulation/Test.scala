package nl.tudelft.rvh.simulation

import scala.concurrent.duration.DurationDouble
import scala.io.StdIn

import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ComputationScheduler

object Test extends App {
	
	implicit val DT = 1.0
	def setpoint(t: Long): Double = 10 * Setpoint.doubleStep(t, 10, 60)

	val time = Observable interval (DT milliseconds, ComputationScheduler()) take 150
	val p = new Boiler
	val c = new PIDController(0.45, 0.01)

	val res = Loops.closedLoop(time map setpoint, 0.0, c ++ p)
	time.zipWith(res)((t, r) => s"$t\t$r").subscribe(println(_), println(_))

	StdIn.readLine()
}