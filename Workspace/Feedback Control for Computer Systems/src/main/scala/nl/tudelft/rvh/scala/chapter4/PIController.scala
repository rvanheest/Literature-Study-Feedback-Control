package nl.tudelft.rvh.scala.chapter4

import scala.concurrent.duration.DurationInt

import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import nl.tudelft.rvh.rxscalafx.Observables
import nl.tudelft.rvh.scala.RoundingExtensions.extendDouble
import nl.tudelft.rvh.scala.ScalaChartTab
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject

class PIController extends ScalaChartTab("Chapter 4 - PI controller", "Cruise control", "time", "speed") {

	private var kp: Double = 0.5
	private var ki: Double = 0.001

	class SpeedSystem(var speed: Double = 10) {
		def interact(power: Double) = {
			if (power <= 0) {
				speed = (0.85 * speed) roundAt 1
			}
			else {
				speed = (speed + power) roundAt 1
			}
			
			speed
		}
	}

	override def bottomBox(): HBox = {
		this.kp = 0.5
		this.ki = 0.001
		
		val box = super.bottomBox()
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
	
	def simulation(): Observable[(Number, Number)] = {
		val time = Observable.interval(50 milliseconds).take(60)
		def setPoint(time: Long): Int = if (time < 20) 15 else if (time < 40) 5 else 20
		val cc = new SpeedSystem
		
		val feedbackLoop = Observable[Double](subscriber => {
			val speed = BehaviorSubject(cc.speed)
			speed.subscribe(subscriber)
			
			time.map(setPoint)
				.zipWith(speed)(_ - _)
				.scan((0.0, 0.0))((c, e) => (e, e + c._2))
				.drop(1)
				.map { t => t._1 * kp + t._2 * ki }
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
			speed.subscribe(subscriber)
			
			Observable.from(0 until 60)
				.map(setPoint)
				.zipWith(speed)(_ - _)
				.scan((0.0, 0.0))((c, e) => (e, e + c._2))
				.map { t => t._1 * this.kp + t._2 * this.ki }
				.map(cc.interact)
				.subscribe(speed)
		})
	}
}