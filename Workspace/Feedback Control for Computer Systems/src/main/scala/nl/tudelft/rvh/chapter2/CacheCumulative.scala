package nl.tudelft.rvh.chapter2

import scala.concurrent.duration.DurationInt

import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import nl.tudelft.rvh.rxscalafx.Observables
import nl.tudelft.rvh.ChartTab
import rx.lang.scala.Observable
import rx.lang.scala.Subscriber
import rx.lang.scala.subjects.PublishSubject

class CacheCumulative extends ChartTab("Chapter 2 - Cumulative", "Cumulative simulation", "time", "hit rate") {

	private var k: Float = 160

	override def bottomBox(): HBox = {
		this.k = 160

		val box = super.bottomBox
		val tf = new TextField(this.k.toString)
		box.getChildren.add(tf)

		Observables.fromNodeEvents(tf, ActionEvent.ACTION)
			.map { _ => tf.getText }
			.map { _.toInt }
			.subscribe(i => this.k = i,
				_ -> {})

		box
	}

	def seriesName(): String = s"k = $k"

	override def time = super.time take 120
	
	def setpoint(time: Long): Double = if (time < 30) 0.6 else if (time < 60) 0.8 else if (time < 90) 0.1 else 0.4

	def simulation() = {
		def cache(size: Double): Double = math.max(0, math.min(1, size / 100))

		Observable((subscriber: Subscriber[Double]) => {
			val hitrate = PublishSubject[Double]

			time.map(setpoint)
				.zipWith(hitrate)(_ - _)
				.scan((cum: Double, e: Double) => cum + e)
				.map(this.k * _)
				.map(cache)
				.subscribe(hitrate)

			hitrate.subscribe(subscriber)
			hitrate.onNext(0.0)
		})
	}

	def simulationForGitHub(): Observable[Double] = {
		val k = 160
		def setPoint(time: Int): Double = if (time < 30) 0.6 else if (time < 60) 0.8 else if (time < 90) 0.1 else 0.4
		def cache(size: Double): Double = math.max(0, math.min(1, size / 100))

		Observable((subscriber: Subscriber[Double]) => {
			val hitrate = PublishSubject[Double]

			Observable.from(0 until 120)
				.map(setPoint)
				.zipWith(hitrate)(_ - _)
				.scan((cum: Double, e: Double) => cum + e)
				.map(k * _)
				.map(cache)
				.subscribe(hitrate)

			hitrate.subscribe(subscriber)
			hitrate.onNext(0.0)
		})
	}
}