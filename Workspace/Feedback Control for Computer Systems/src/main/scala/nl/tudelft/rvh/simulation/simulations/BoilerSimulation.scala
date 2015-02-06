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
import nl.tudelft.rvh.simulation.Boiler
import nl.tudelft.rvh.simulation.Loops
import nl.tudelft.rvh.simulation.Setpoint
import nl.tudelft.rvh.simulation.PIDController

class BoilerSimulation(dt: Double = 1.0) extends ChartTab("Boiler", "Boiler simulation", "time", "temperature")(dt) {

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

		Loops.closedLoop(time, setpoint(_), 0.0, c ++ p)
	}

	def simulationForGithub(): Observable[Double] = {
		implicit val DT = 1.0
		def setpoint(t: Long): Double = 10 * Setpoint.doubleStep(t, 10, 60)

		val time = Observable from (0 until 150)
		val p = new Boiler
		val c = new PIDController(0.45, 0.01)

		Loops.closedLoop(time map (_ toLong), setpoint _, 0.0, c ++ p)
	}
}