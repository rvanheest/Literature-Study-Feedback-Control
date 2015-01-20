package nl.tudelft.rvh.simulation

import scala.concurrent.duration.DurationDouble
import nl.tudelft.rvh.simulation.controller.PIDController
import nl.tudelft.rvh.simulation.plant.Boiler
import rx.lang.scala.Observable
import nl.tudelft.rvh.ChartTab

class BoilerSim(implicit DT: Double = 1.0) extends ChartTab("Boiler", "Boiler simulation", "time", "temperature") {

	def seriesName = "Boiler simulation"
	
	override def time: Observable[Long] = Observable interval (DT milliseconds) take 15000
	
	def setpoint(t: Long): Double = 10 * Setpoint.doubleStep(t, 1000, 6000)

	def simulation(): Observable[(Number, Number)] = {
		val p = new Boiler
		val c = new PIDController(0.45, 0.01)

		val res = Loops.closedLoop(time map (_ toInt), setpoint, c, p)
		time.zipWith(res)((_, _))
	}
}