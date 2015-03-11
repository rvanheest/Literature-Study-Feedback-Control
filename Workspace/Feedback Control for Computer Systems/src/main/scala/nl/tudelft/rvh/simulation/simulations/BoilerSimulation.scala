package nl.tudelft.rvh.simulation.simulations

import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import nl.tudelft.rvh.SimulationTab
import nl.tudelft.rvh.rxscalafx.Observables
import nl.tudelft.rvh.simulation.Boiler
import nl.tudelft.rvh.simulation.Loops
import nl.tudelft.rvh.simulation.PIDController
import nl.tudelft.rvh.simulation.Setpoint
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import nl.tudelft.rvh.ChartData

class BoilerSimulation(implicit dt: Double = 1.0) extends SimulationTab("Boiler", "Time", "Temperature") {

	def time: Observable[Long] = (0L until 150L).toObservable

	def setpoint(t: Long): Double = 10 * Setpoint.doubleStep(t, 10, 60)

	def simulation: ChartData[AnyVal] = {
		def simul: Observable[Map[String, AnyVal]] = {
			val p = new Boiler
			val c = new PIDController(0.45, 0.01)
	
			Loops.closedLoop1(time.map(setpoint), 0.0, c ++ p)
		}
		
		val sim = simul.publish
		
		new ChartData(() => sim.connect, sim map (_ ("Boiler")))
	}

	def simulationForGithub(): Observable[Double] = {
		def setpoint(t: Long): Double = 10 * Setpoint.doubleStep(t, 10, 60)

		val time = Observable from (0 until 150)
		val p = new Boiler
		val c = new PIDController(0.45, 0.01)

		Loops.closedLoop(time map (_ toLong) map setpoint, 0.0, c ++ p)
	}
}