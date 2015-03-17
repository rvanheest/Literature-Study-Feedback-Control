package nl.tudelft.rvh.simulation.simulations

import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import nl.tudelft.rvh.ChartData
import nl.tudelft.rvh.SimulationTab
import nl.tudelft.rvh.StaticTestTab
import nl.tudelft.rvh.rxscalafx.Observables
import nl.tudelft.rvh.simulation.AsymmetricPIDController
import nl.tudelft.rvh.simulation.Integrator
import nl.tudelft.rvh.simulation.Loops
import nl.tudelft.rvh.simulation.PIDController
import nl.tudelft.rvh.simulation.Randomizers
import nl.tudelft.rvh.simulation.ServerPool
import nl.tudelft.rvh.simulation.ServerPoolWithLatency
import nl.tudelft.rvh.simulation.SpecialController
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import rx.lang.scala.schedulers.ComputationScheduler

object ServerScalingSimulation {

	def consume_queue() = 100 * Randomizers.betavariate(20, 2)

	class ServerStaticTest extends StaticTestTab("Server Pool Static Test", "Server Pool Static Test", "Server instances", "Completion Rate") {
		var traffic = 1000

		override def bottomBox(): HBox = {
			this.traffic = 1000

			val box = super.bottomBox
			val kpTF = new TextField(this.traffic.toString)

			box.getChildren.addAll(kpTF)

			Observables.fromNodeEvents(kpTF, ActionEvent.ACTION)
				.map { _ => kpTF.getText }
				.map { _ toInt }
				.subscribe(this.traffic = _)

			box
		}

		def load_queue() = Randomizers.gaussian(traffic, traffic / 200.0)

		def seriesName: String = s"Traffic Intensity $traffic"

		def simulation(): Observable[(Int, Double)] = Loops.staticTest(new ServerPool(0, server = consume_queue, load = load_queue), 20, 20, 5, 1000)(_ toInt)
	}

	class ServerClosedLoop1(implicit dt: Double = 1.0) extends SimulationTab("Server Pool Loop 1", "Time", "Completion rate", "Number of servers") {
		
		var globalTime = 0

		def load_queue() = {
			globalTime += 1
			
			if (globalTime < 200) Randomizers.gaussian(1000, 5)
			else Randomizers.gaussian(1200, 5)
		}

		def seriesName = "Completion rate"

		def time: Observable[Long] = (0L until 300L).toObservable observeOn ComputationScheduler()

		def setpoint(time: Long): Double = if (time < 100) 0.8 else 0.6

		def simulation: ChartData[AnyVal] = {
			def simul: Observable[Map[String, AnyVal]] = {
				val p = new ServerPool(8, consume_queue, load_queue)
				val c = new PIDController(1, 5) map math.round map (_ toInt)

				Loops.closedLoop1(time map setpoint, 0.0, c ++ p)
			}

			val sim = simul.publish
			new ChartData(() => sim.connect, sim map (_("Completion rate")), sim map (_("Servers")))
		}

		def simulationForGitHub(): Observable[Double] = {
			def time: Observable[Long] = (0L until 300L).toObservable observeOn ComputationScheduler()
			def setpoint(t: Long): Double = if (t < 100) 0.8 else 0.6

			def consumeQueue() = 100 * Randomizers.betavariate(20, 2)
			def loadQueue() = {
				globalTime += 1

				if (globalTime < 200) Randomizers.gaussian(1000, 5)
				else Randomizers.gaussian(1200, 5)
			}

			val p = new ServerPool(8, consumeQueue, loadQueue)
			val c = new PIDController(1, 5) map math.round map (_ toInt)

			Loops.closedLoop(time map setpoint, 0.0, c ++ p)
		}
	}

	class ServerClosedLoop2(implicit dt: Double = 1.0) extends SimulationTab("Server Pool Loop 2", "Time", "Completion rate", "Number of servers") {
		
		var globalTime = 0

		def load_queue() = {
			globalTime += 1
			
			if (globalTime < 200) Randomizers.gaussian(1000, 5)
			else if (globalTime < 500) Randomizers.gaussian(800, 5)
			else Randomizers.gaussian(1200, 5)
		}

		override def time: Observable[Long] = (0L until 700L).toObservable observeOn ComputationScheduler()

		def setpoint(time: Long): Double = 0.9995

		def simulation: ChartData[AnyVal] = {
			def simul(): Observable[Map[String, AnyVal]] = {
				val p = new ServerPool(0, consume_queue, load_queue)
				val c = new AsymmetricPIDController(10, 200) map math.round map (_ toInt)

				Loops.closedLoop1(time map setpoint, 0.0, c ++ p)
			}

			val sim = simul.publish
			new ChartData(() => sim.connect, sim map (_("Completion rate")), sim map (_("Servers")))
		}
	}

	class ServerClosedLoop3(implicit dt: Double = 1.0) extends SimulationTab("Server Pool Loop 3", "Time", "Completion rate", "Number of servers")(dt) {

		var globalTime = 0

		def load_queue() = {
			globalTime += 1
			
			if (globalTime < 500) Randomizers.gaussian(1000, 5)
			else if (globalTime < 800) Randomizers.gaussian(800, 5)
			else Randomizers.gaussian(1200, 5)
		}
		
		override def time: Observable[Long] = (0L until 1200L).toObservable observeOn ComputationScheduler()

		def setpoint(time: Long): Double = 1.0

		def simulation: ChartData[AnyVal] = {
			def simul(): Observable[Map[String, AnyVal]] = {
				val c = new SpecialController(100, 10)
				val a = new Integrator map math.round map (_ toInt)
				val p = new ServerPool(0, consume_queue, load_queue)

				Loops.closedLoop1(time map setpoint, 0.0, c ++ a ++ p)
			}

			val sim = simul.publish
			new ChartData(() => sim.connect, sim map (_("Completion rate")), sim map (_("Servers")))
		}
	}
	
	class ServerPoolWithLatencySimulation(implicit dt: Double = 1.0) extends SimulationTab("Server Pool with Latency", "Time", "Completion rate", "Number of servers", "Number of standbys")(dt) {
		var globalTime = 0

		def load_queue() = {
			globalTime += 1
			
			if (globalTime < 500) Randomizers.gaussian(1000, 5)
			else if (globalTime < 800) Randomizers.gaussian(800, 5)
			else Randomizers.gaussian(1200, 5)
		}
		
		def time = (0L until 1200L).toObservable observeOn ComputationScheduler()
		
		def setpoint(time: Long): Double = 1.0
		
		def simulation: ChartData[AnyVal] = {
			def simul = {
				val c = new SpecialController(100, 10)
				val a = new Integrator map math.round map (_ toInt)
				val p = new ServerPoolWithLatency(0, consume_queue, load_queue, 120, List.fill(8)(1))

				Loops.closedLoop1(time map setpoint, 0.0, c ++ a ++ p)
			}
			
			val sim = simul.publish
			
			new ChartData(() => sim.connect, sim map (_("Completion rate")), sim map (_("Servers")), sim map (_("Standby")))
		}
	}
}