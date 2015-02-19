package nl.tudelft.rvh.simulation.simulations

import scala.concurrent.duration.DurationDouble
import scala.util.Random
import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import nl.tudelft.rvh.ChartTab
import nl.tudelft.rvh.SimulationTab
import nl.tudelft.rvh.StaticTestTab
import nl.tudelft.rvh.StepTestTab
import nl.tudelft.rvh.rxscalafx.Observables
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import rx.lang.scala.schedulers.ComputationScheduler
import nl.tudelft.rvh.simulation.SpecialController
import nl.tudelft.rvh.simulation.Randomizers
import nl.tudelft.rvh.simulation.AsymmController
import nl.tudelft.rvh.simulation.Loops
import nl.tudelft.rvh.simulation.Integrator
import nl.tudelft.rvh.simulation.ServerPool
import nl.tudelft.rvh.simulation.PIDController

object ServerScalingSimulation {

	var global_time = 0

	def load_queue() = {
		global_time += 1

		if (global_time > 2500) Randomizers.gaussian(1200, 5)
		else if (global_time > 2200) Randomizers.gaussian(800, 5)
		else Randomizers.gaussian(1000, 5)
	}

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

		def loadqueue() = Randomizers.gaussian(traffic, traffic / 200.0)

		def seriesName: String = s"Traffic Intensity $traffic"

		def simulation(): Observable[(Int, Double)] = Loops.staticTest(new ServerPool(0, server = consume_queue, load = loadqueue), 20, 20, 5, 1000)(_ toInt)
	}

	class ServerClosedLoop1(dt: Double = 1.0) extends SimulationTab("Server Pool Loop 1", "Server Pool Loop 1", "time", "completion rate")(dt) {

		implicit val DT = dt

		def loadqueue() = {
			global_time += 1

			if (global_time > 2100) Randomizers.gaussian(1200, 5)
			else Randomizers.gaussian(1000, 5)
		}

		def seriesName = "Completion rate"

		override def time: Observable[Long] = (0L until 5000L).toObservable observeOn ComputationScheduler()

		def setpoint(time: Long): Double = if (time > 100) 0.6 else 0.8

		def simulation(): Observable[Map[String, AnyVal]] = {
			val p = new ServerPool(8, server = consume_queue, load = loadqueue)
			val c = new PIDController(1, 5) map math.round map (_ toInt)

			Loops.closedLoop1(time, setpoint, 0.0, c ++ p)
				.map(_ filter { case (name, _) => name == "Completion rate" })
		}
	}

	class ServerClosedLoop2(dt: Double = 1.0) extends SimulationTab("Server Pool Loop 2", "Server Pool Loop 2", "time", "completion rate")(dt) {

		implicit val DT = dt

		def seriesName = "Completion rate"

		override def time: Observable[Long] = (0L until 5000L).toObservable observeOn ComputationScheduler()

		def setpoint(time: Long): Double = if (time < 50) time / 50.0 else 0.9995

		def simulation(): Observable[Map[String, AnyVal]] = {
			val p = new ServerPool(0, server = consume_queue, load = load_queue)
			val c = new AsymmController(10, 200) map math.round map (_ toInt)

			Loops.closedLoop1(time, setpoint, 0.0, c ++ p)
				.map(_ filter { case (name, _) => name == "Completion rate" })
		}
	}

	class ServerClosedLoop3(dt: Double = 1.0) extends SimulationTab("Server Pool Loop 3", "Server Pool Loop 3", "time", "completion rate")(dt) {

		implicit val DT = dt

		override def time: Observable[Long] = (0L until 5000L).toObservable observeOn ComputationScheduler()

		def setpoint(time: Long): Double = 1.0

		def simulation(): Observable[Map[String, AnyVal]] = {
			val c = new SpecialController(100, 10)
			val a = new Integrator map math.round map (_ toInt)
			val p = new ServerPool(0, server = consume_queue, load = load_queue)

			Loops.closedLoop1(time, setpoint, 0.0, c ++ a ++ p)
				.map(_ filter { case (name, _) => name == "Completion rate" || name == "Servers" })
		}
	}
}