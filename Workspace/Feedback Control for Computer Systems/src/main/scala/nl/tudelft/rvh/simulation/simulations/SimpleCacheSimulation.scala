package nl.tudelft.rvh.simulation.simulations

import rx.lang.scala.ObservableExtensions
import nl.tudelft.rvh.simulation.SimpleCache
import nl.tudelft.rvh.simulation.PIDController
import nl.tudelft.rvh.simulation.Loops
import nl.tudelft.rvh.ChartData
import nl.tudelft.rvh.SimulationTab
import javafx.scene.layout.HBox
import nl.tudelft.rvh.StaticTestTab
import javafx.scene.control.TextField
import javafx.event.ActionEvent
import nl.tudelft.rvh.rxscalafx.Observables
import rx.lang.scala.Observable
import nl.tudelft.rvh.simulation.Randomizers
import nl.tudelft.rvh.simulation.FixedFilter
import nl.tudelft.rvh.StepTestTab
import rx.lang.scala.schedulers.ComputationScheduler

class SimpleCacheStaticTest extends StaticTestTab("Simple cache static test", "Simple cache static test", "cache size", "hit rate") {
	
	def seriesName = "Static test"

	def simulation(): Observable[(Double, Double)] = {
		val p = new SimpleCache
		Loops.staticTest(p, 150, 100, 5, 3000)
	}
}

class SimpleCacheStepResponse extends StepTestTab("Simple Cache Dynamic Response", "Simple Cache Dynamic Response", "time", "hitrate") {

	def seriesName: String = "Cache dynamic response"

	override def time = (0L until 500).toObservable observeOn ComputationScheduler()

	def simulation: Observable[Double] = {
		def setpoint(time: Long): Double = 60

		val p = new SimpleCache
		Loops.stepResponse(time, setpoint, p)
	}
}

class SimpleCacheSimulation(implicit dt: Double = 1.0) extends SimulationTab("Simple Cache", "Time", "Hit rate", "Size") {

	def time = (0L until 120L).toObservable
	def setpoint(time: Long) = if (time < 30) 0.6 else if (time < 60) 0.8 else if (time < 90) 0.1 else 0.4

	def simulation: ChartData[AnyVal] = {
		def simul: Observable[Map[String, AnyVal]] = {
			val p = new SimpleCache
			val c = new PIDController(0, 70)
			
			Loops.closedLoop1(time map setpoint, 0.0, c ++ p)
		}
		
		val sim = simul.publish
		
		new ChartData(() => sim.connect, sim map (_("Cache hit rate")), sim map(_("Cache size")))
	}
}