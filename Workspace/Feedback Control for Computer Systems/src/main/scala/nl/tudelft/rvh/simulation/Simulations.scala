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

class BoilerSim(implicit DT: Double = 1.0) extends ChartTab("Boiler", "Boiler simulation", "time", "temperature") {
	
	private var kp: Double = 0.45
	private var ki: Double = 0.01

	override def bottomBox(): HBox = {
		this.kp = 0.45
		this.ki = 0.01
		
		val box = super.bottomBox
		val kpTF = new TextField(this.kp.toString)
		val kiTF = new TextField(this.ki.toString)
		
		box.getChildren.addAll(kpTF, kiTF)
		
		Observables.fromNodeEvents(kpTF, ActionEvent.ACTION)
			.map { _ => kpTF.getText }
			.map { _.toDouble }
			.subscribe(i => this.kp = i)
		
		Observables.fromNodeEvents(kiTF, ActionEvent.ACTION)
			.map { _ => kiTF.getText }
			.map { _.toDouble }
			.subscribe(i => this.ki = i)
		
		box
	}

	def seriesName = "Boiler simulation"
	
	override def time: Observable[Long] = Observable interval (DT milliseconds) take 15000
	
	def setpoint(t: Long): Double = 10 * Setpoint.doubleStep(t, 1000, 6000)

	def simulation(): Observable[(Number, Number)] = {
		val p = new Boiler
		val c = new PIDController(kp, ki)

		val res = Loops.closedLoop(time map (_ toInt), setpoint, c, p)
		time.zipWith(res)((_, _))
			.onBackpressureBuffer
			.asInstanceOf[Observable[(Number, Number)]]
	}
}