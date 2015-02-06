package nl.tudelft.rvh.simulation.simulations

import scala.concurrent.duration.DurationDouble
import scala.util.Random
import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import nl.tudelft.rvh.ChartTab
import nl.tudelft.rvh.StaticTestTab
import nl.tudelft.rvh.StepTestTab
import nl.tudelft.rvh.rxscalafx.Observables
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ComputationScheduler
import nl.tudelft.rvh.simulation.Randomizers
import nl.tudelft.rvh.simulation.FixedFilter
import nl.tudelft.rvh.simulation.Loops
import nl.tudelft.rvh.simulation.Cache
import nl.tudelft.rvh.simulation.PIDController

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

	class CacheStepResponse extends StepTestTab("Cache Step Response", "Cache Step Response", "time", "hitrate") {

		def seriesName: String = "Cache step response"

		override def time = super.time take 500

		def simulation: Observable[Double] = {
			def demand(t: Long) = math floor Randomizers.gaussian(0, 15) toInt
			def setpoint(time: Long): Double = 40

			val p = new Cache(0, demand) map(if (_) 1.0 else 0.0)
			val f = new FixedFilter(100)
			Loops.stepResponse(time, setpoint, p ++ f)
		}
	}

	class CacheClosedLoop(dt: Double = 1.0) extends ChartTab("Cache Closed Loop", "Cache simulation", "time", "hitrate")(dt) {

		implicit val DT = dt

		def seriesName = "Cache simulation"

		override def time: Observable[Long] = Observable interval (1 milliseconds, ComputationScheduler()) take 10000

		def setpoint(t: Long): Double = if (t > 5000) 0.5 else 0.7

		def simulation(): Observable[Double] = {
			def demand(t: Long) = math floor Randomizers.gaussian(0, 15) toInt

			val c = new PIDController(100, 2.50)
			val p = new Cache(0, demand) map(if (_) 1.0 else 0.0)
			val f = new FixedFilter(100)
			val plant = p ++ f
			Loops.closedLoop(time, setpoint, 0.0, c ++ plant)
		}
	}

	class CacheClosedLoopJumps(dt: Double = 1.0) extends ChartTab("Cache Closed Loop Jumps", "Cache simulation", "time", "hitrate")(dt) {

		implicit val DT = dt

		def seriesName = "Cache simulation"

		override def time: Observable[Long] = Observable interval (2 milliseconds, ComputationScheduler()) take 10000

		def setpoint(t: Long): Double = 0.7

		def simulation(): Observable[Double] = {
			def gaus2(tuple: (Int, Int)) = math floor Randomizers.gaussian(tuple._1, tuple._2) toInt
			def demand(t: Long) = gaus2(if (t < 3000) (0, 15) else if (t < 5000) (0, 35) else (100, 15))

//			val c = new PIDController(270, 7.5)
//			val c = new PIDController(100, 4.3)
//			val c = new PIDController(80, 2.0)
			val c = new PIDController(150, 2)
			val p = new Cache(0, demand) map(if (_) 1.0 else 0.0)
			val f = new FixedFilter(100)
			val plant = p ++ f

			Loops.closedLoop(time, setpoint, 0.0, c ++ plant)
		}
	}
}