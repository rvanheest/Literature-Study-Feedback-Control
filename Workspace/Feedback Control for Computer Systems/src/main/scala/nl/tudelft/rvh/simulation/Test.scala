package nl.tudelft.rvh.simulation

import scala.concurrent.duration.DurationDouble
import scala.io.StdIn
import nl.tudelft.rvh.simulation.controller.PIDController
import nl.tudelft.rvh.simulation.plant.Boiler
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ComputationScheduler

object Test extends App {

	implicit val DT = 1.0
	def setpoint(t: Long) = 10 * Setpoint.doubleStep(t, 1000, 6000)

	val time = Observable interval (DT milliseconds, ComputationScheduler()) take 15000
	val p = new Boiler
	val c = new PIDController(0.45, 0.01)

	val res = Loops.closedLoop(time, setpoint, c, p)
	time.zipWith(res)((t, r) => s"$t\t$r").subscribe(println(_))

	StdIn.readLine()
}