package nl.tudelft.rvh.simulation

import scala.concurrent.duration.DurationDouble
import nl.tudelft.rvh.ChartTab
import nl.tudelft.rvh.simulation.controller.PIDController
import nl.tudelft.rvh.simulation.plant.Boiler
import rx.lang.scala.Observable
import javafx.scene.layout.HBox
import nl.tudelft.rvh.rxscalafx.Observables
import javafx.scene.control.TextField
import javafx.event.ActionEvent
import javafx.scene.layout.VBox
import rx.lang.scala.schedulers.ComputationScheduler

class BoilerSim(dt: Double = 1.0) extends ChartTab("Boiler", "Boiler simulation", "time", "temperature") {
	
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
	
	override def time: Observable[Long] = Observable interval (1 milliseconds) take (150.0 / dt).toInt
	
	def setpoint(t: Long): Double = 10 * Setpoint.doubleStep(t, math.round(10.0 / dt), math.round(60.0 / dt))
	
	def simulation(): Observable[(Number, Number)] = {
		val p = new Boiler
		val c = new PIDController(kp, ki)

		val res = Loops.closedLoop(time map (_ toInt), setpoint, c, p)
		time.zipWith(res)((_, _))
			.onBackpressureBuffer
			.asInstanceOf[Observable[(Number, Number)]]
	}
}