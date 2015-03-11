package nl.tudelft.rvh.simulation.simulations

import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import nl.tudelft.rvh.SimulationTab
import nl.tudelft.rvh.StaticTestTab
import nl.tudelft.rvh.StepTestTab
import nl.tudelft.rvh.rxscalafx.Observables
import nl.tudelft.rvh.simulation.Cache
import nl.tudelft.rvh.simulation.FixedFilter
import nl.tudelft.rvh.simulation.Loops
import nl.tudelft.rvh.simulation.PIDController
import nl.tudelft.rvh.simulation.Randomizers
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import rx.lang.scala.schedulers.ComputationScheduler
import nl.tudelft.rvh.ChartData

object CacheSimulation {

	class CacheStaticTest extends StaticTestTab("Cache Static Test", "Cache Static Test", "cache size", "hitrate") {

		var demandWidth = 35

		override def bottomBox(): HBox = {
			this.demandWidth = 35

			val box = super.bottomBox
			val kpTF = new TextField(this.demandWidth.toString)

			box.getChildren.addAll(kpTF)

			Observables.fromNodeEvents(kpTF, ActionEvent.ACTION)
				.map { _ => kpTF.getText }
				.map { _ toInt }
				.subscribe(this.demandWidth = _)

			box
		}

		def seriesName = s"Demand width = $demandWidth"

		def simulation(): Observable[(Double, Double)] = {
			def demand(t: Long) = math floor Randomizers.gaussian(0, demandWidth) toInt

			val p = new Cache(0, demand) map(if (_) 1.0 else 0.0)
			val f = new FixedFilter(100)
			Loops.staticTest(p ++ f, 150, 100, 5, 3000)
		}
	}

	class CacheStepResponse extends StepTestTab("Cache Dynamic Response", "Cache Dynamic Response", "time", "hitrate") {

		def seriesName: String = "Cache dynamic response"

		override def time = (0L until 500).toObservable observeOn ComputationScheduler()

		def simulation: Observable[Double] = {
			def demand(t: Long) = math floor Randomizers.gaussian(0, 15) toInt
			def setpoint(time: Long): Double = 40

			val p = new Cache(0, demand) map(if (_) 1.0 else 0.0)
			val f = new FixedFilter(100)
			Loops.stepResponse(time, setpoint, p ++ f)
		}
	}

	class CacheClosedLoop(implicit dt: Double = 1.0) extends SimulationTab("Cache Closed Loop", "Time", "Hitrate", "Cache size") {

		override def time: Observable[Long] = (0L until 10000L).toObservable observeOn ComputationScheduler()

		def setpoint(t: Long): Double = 0.7

		def simulation: ChartData[AnyVal] = {
			def simul: Observable[Map[String, AnyVal]] = {
				def gaus(tuple: (Int, Int)) = math floor Randomizers.gaussian(tuple._1, tuple._2) toInt
				def demand(t: Long) = gaus(if (t < 3000) (0, 15) else if (t < 5000) (0, 35) else (100, 15))
		
//				val c = new PIDController(192.7, 4.3) // Ziegler-Nichols
//				val c = new PIDController(239.3, 7.5) // Cohen-Coon
//				val c = new PIDController(48, 1)      // AMIGO
				val c = new PIDController(200, 2)
				val p = new Cache(0, demand) map(if (_) 1.0 else 0.0)
				val f = new FixedFilter(100)
				val plant = p ++ f
				
				Loops.closedLoop1(time map setpoint, 0.0, c ++ plant)
			}
		
			val sim = simul.publish
			new ChartData(() => sim.connect, sim map (_("Fixed filter")), sim map (_("Cache size")))
		}

		def simulationForGitHub(): Observable[Double] = {
			def time: Observable[Long] = (0L until 10000L).toObservable observeOn ComputationScheduler()
			def setpoint(t: Long): Double = 0.7

			def gaus(tuple: (Int, Int)) = math floor Randomizers.gaussian(tuple _1, tuple _2) toInt
			def demand(t: Long) = gaus(if (t < 3000) (0, 15) else if (t < 5000) (0, 35) else (100, 15))

//			val c = new PIDController(192.7, 4.3) // Ziegler-Nichols
//			val c = new PIDController(239.3, 7.5) // Cohen-Coon
//			val c = new PIDController(48, 1)      // AMIGO
			val c = new PIDController(200, 2)
			val p = new Cache(0, demand) map(if (_) 1.0 else 0.0)
			val f = new FixedFilter(100)
			val plant = p ++ f

			Loops.closedLoop(time map setpoint, 0.0, c ++ plant)
		}
	}
}