package nl.tudelft.rvh.chapter4

import scala.concurrent.duration.DurationInt

import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import nl.tudelft.rvh.rxscalafx.Observables
import nl.tudelft.rvh.ChartTab
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject

class PIController extends ChartTab("Chapter 4 - PI controller", "Cruise control", "time", "speed") {

	private var kp: Double = 0.5
	private var ki: Double = 0.001

	override def bottomBox(): HBox = {
		this.kp = 0.5
		this.ki = 0.001
		
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

	def seriesName(): String = s"kp = $kp; ki = $ki"
	
	override def time = super.time take 60
	
	def setpoint(time: Long) = if (time < 20) 15 else if (time < 40) 5 else 20
	
	def simulation(): Observable[(Number, Number)] = {
		val cc = new SpeedSystem
		
		val feedbackLoop = Observable[Double](subscriber => {
			val speed = BehaviorSubject(cc.speed)
			speed.drop(1).subscribe(subscriber)
			
			time.map(setpoint)
				.zipWith(speed)(_ - _)
				.scan(new PI)(_ work _)
				.drop(1)
				.map(_.controlAction(kp, ki))
				.map(cc.interact)
				.subscribe(speed)
		})
		time.zipWith(feedbackLoop)((_, _))
	}

	def simulationForGitHub(): Observable[Double] = {
		def setPoint(time: Int): Int = if (time < 20) 15 else if (time < 40) 5 else 20
		val cc = new SpeedSystem
		
		Observable(subscriber => {
			val speed = BehaviorSubject(cc.speed)
			speed.drop(1).subscribe(subscriber)
			
			Observable.from(0 until 60)
				.map(setPoint)
				.zipWith(speed)(_ - _)
				.scan(new PI)(_ work _)
				.drop(1)
				.map(_.controlAction(kp, ki))
				.map(cc.interact)
				.subscribe(speed)
		})
	}
}