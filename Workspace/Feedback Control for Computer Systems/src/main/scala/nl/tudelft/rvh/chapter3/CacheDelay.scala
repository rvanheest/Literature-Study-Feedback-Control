package nl.tudelft.rvh.chapter3

import scala.concurrent.duration.DurationInt

import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import nl.tudelft.rvh.rxscalafx.Observables
import nl.tudelft.rvh.ChartTab
import nl.tudelft.rvh.Extensions.ObsExtensions.extendObservable
import rx.lang.scala.Observable
import rx.lang.scala.Subscriber
import rx.lang.scala.subjects.BehaviorSubject

class CacheDelay extends ChartTab("Chapter 3 - Cache with delay", "Delay simulation", "time", "hit rate") {

	private var k: Float = 50
	private var delay: Int = 2

	override def bottomBox(): HBox = {
		this.k = 50
		this.delay = 2

		val box = super.bottomBox
		val kTF = new TextField(this.k.toString)
		val delayTF = new TextField((this.delay - 1).toString)

		box.getChildren.addAll(kTF, delayTF)

		Observables.fromNodeEvents(kTF, ActionEvent.ACTION)
			.map { _ => kTF.getText }
			.map { _.toInt }
			.subscribe(i => this.k = i, _ -> {})

		Observables.fromNodeEvents(delayTF, ActionEvent.ACTION)
			.map { _ => delayTF.getText }
			.map { _.toInt }
			.subscribe(i => this.delay = i + 1, _ -> {})

		box
	}

	def seriesName(): String = s"k = $k, delay = " + (delay - 1)

	override def time = super.time take 120
	
	def setpoint(time: Long) = if (time < 30) 0.6 else if (time < 60) 0.8 else if (time < 90) 0.1 else 0.4

	def simulation() = {
		def cache(size: Double): Double = math.max(0, math.min(1, size / 100))

		Observable((subscriber: Subscriber[Double]) => {
			val hitrate = BehaviorSubject[Double]

			time.map(setpoint)
				.zipWith(hitrate)(_ - _)
				.scan((cum: Double, e: Double) => cum + e)
				.map { this.k * _ }
				.map(cache)
				.delay(this.delay, 0.0)
				.subscribe(hitrate)

			hitrate.subscribe(subscriber)
		})
	}

	def simulationForGitHub(): Observable[Double] = {
		def setPoint(time: Int): Double = if (time < 30) 0.6 else if (time < 60) 0.8 else if (time < 90) 0.1 else 0.4
		def cache(size: Double): Double = math.max(0, math.min(1, size / 100))

		Observable((subscriber: Subscriber[Double]) => {
			val hitrate = BehaviorSubject[Double]

			Observable.from(0 until 120)
				.map(setPoint)
				.zipWith(hitrate)(_ - _)
				.scan((cum: Double, e: Double) => cum + e)
				.map { this.k * _ }
				.map(cache)
				.delay(this.delay, 0.0)
				.subscribe(hitrate)

			hitrate.subscribe(subscriber)
		})
	}
}