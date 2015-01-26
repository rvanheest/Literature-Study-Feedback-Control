package nl.tudelft.rvh.simulation

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

class BoilerSim(dt: Double = 1.0) extends ChartTab("Boiler", "Boiler simulation", "time", "temperature")(dt) {
	
	implicit val DT = dt
	var kp: Double = 0.45
	var ki: Double = 0.01
	var kd: Double = 0.0

	override def bottomBox(): HBox = {
		this.kp = 0.45
		this.ki = 0.01
		this.kd = 0.0
		
		val box = super.bottomBox
		val kpTF = new TextField(this.kp.toString)
		val kiTF = new TextField(this.ki.toString)
		val kdTF = new TextField(this.kd.toString)
		
		box.getChildren.addAll(new VBox(kpTF, kiTF, kdTF))
		
		Observables.fromNodeEvents(kpTF, ActionEvent.ACTION)
			.map { _ => kpTF.getText }
			.map { _.toDouble }
			.subscribe(i => this.kp = i)
		
		Observables.fromNodeEvents(kiTF, ActionEvent.ACTION)
			.map { _ => kiTF.getText }
			.map { _.toDouble }
			.subscribe(i => this.ki = i)

		Observables.fromNodeEvents(kdTF, ActionEvent.ACTION)
			.map { _ => kdTF.getText }
			.map { _.toDouble }
			.subscribe(i => this.kd = i)
		
		box
	}

	def seriesName = "Boiler simulation"
	
	override def time: Observable[Long] = super.time take 150
	
	def setpoint(t: Long): Double = 10 * Setpoint.doubleStep(t, 10, 60)
	
	def simulation(): Observable[Double] = {
		val p = new Boiler
		val c = new PIDController(kp, ki)

		Loops.closedLoop(time, setpoint, c, p)
	}

	def simulationForGithub(): Observable[Double] = {
		implicit val DT = 1.0
		def setpoint(t: Long) = 10 * Setpoint.doubleStep(t, 10, 60)
		
		val time = Observable from (0 until 150)
		val p = new Boiler
		val c = new PIDController(0.45, 0.01)
		
		Loops.closedLoop(time map (_ toLong), setpoint, c, p)
	}
}

object CacheSimulation {
	
	def gaussian(mean: Int, stdDev: Int) = new Random().nextGaussian() * stdDev + mean

	class CacheStaticTest extends StaticTestTab("Cache Static Test", "Cache Static Test", "time", "hitrate") {
		
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
			def demand(t: Long) = math floor gaussian (0, demandWidth) toInt
			
			val p = new Cache(0, demand) ++ new FixedFilter(100)
			Loops.staticTest(p, 150, 100, 5, 3000)
		}
	}

	class CacheStepResponse extends StepTestTab("Cache Step Response", "Cache Step Response", "time", "hitrate") {
		
		def seriesName: String = "Cache step response"

		override def time = super.time take 500
	
		def simulation: Observable[Double] = {
			def demand(t: Long) = math floor gaussian (0, 15) toInt
			def setpoint(time: Long): Double = 40
			
			val p = new Cache(0, demand) ++ new FixedFilter(100)
			Loops.stepResponse(time map (_ toInt), setpoint, p)
		}
	}

	class CacheClosedLoop(dt: Double = 1.0) extends ChartTab("Cache Closed Loop", "Cache simulation", "time", "hitrate")(dt) {
	
		implicit val DT = dt
		
		def seriesName = "Cache simulation"
		
		override def time: Observable[Long] = Observable interval (1 milliseconds, ComputationScheduler()) take 10000
		
		def setpoint(t: Long): Double = if (t > 5000) 0.5 else 0.7
		
		def simulation(): Observable[Double] = {
			def demand(t: Long) = math floor gaussian (0, 15) toInt
			
			val p = new Cache(0, demand) ++ new FixedFilter(100)
			val c = new PIDController(100, 2.50)
	
			Loops.closedLoop(time map (_ toInt), setpoint, c, p)
		}
	}

	class CacheClosedLoopJumps(dt: Double = 1.0) extends ChartTab("Cache Closed Loop Jumps", "Cache simulation", "time", "hitrate")(dt) {
	
		implicit val DT = dt
		
		def seriesName = "Cache simulation"
		
		override def time: Observable[Long] = Observable interval (1 milliseconds, ComputationScheduler()) take 10000
		
		def setpoint(t: Long): Double = 0.7
		
		def simulation(): Observable[Double] = {
			def gaus2(tuple: (Int, Int)) = math floor gaussian (tuple._1, tuple._2) toInt
			def demand(t: Long) = gaus2(if (t < 3000) (0, 15) else if (t < 5000) (0, 35) else (100, 15))
			
			val p = new Cache(0, demand) ++ new FixedFilter(100)
//			val c = new PIDController(270, 7.5)
//			val c = new PIDController(100, 4.3)
//			val c = new PIDController(80, 2.0)
			val c = new PIDController(150, 2)
	
			Loops.closedLoop(time map (_ toInt), setpoint, c, p)
		}
	}
}