package nl.tudelft.rvh.scala.chapter2

import scala.concurrent.duration._
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import nl.tudelft.rvh.scala.ScalaChartTab
import rx.lang.scala.Observable
import rx.lang.scala.Subscriber
import rx.lang.scala.subjects.PublishSubject
import nl.tudelft.rvh.rxscalafx.Observables

class CacheSmallCumulative() extends ScalaChartTab("Chapter 2 - Small cumulative", "Cumulative simulation", "time", "cache size") {

	private var k: Float = 160

	override def bottomBox(): HBox = {
		this.k = 160

		val box = super.bottomBox()
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

	def simulation(): Observable[(Number, Number)] = {
		val time = Observable.interval(50 milliseconds).take(20)
		val setPoint = (_: Long) => 0.6
		val cache = (size: Double) => if (size < 0) 0 else if (size > 100) 1 else size / 100

		val feedbackLoop = Observable((subscriber: Subscriber[Double]) => {
			val hitrate = PublishSubject[Double]

			time.map { setPoint }
				.zipWith(hitrate)(_ - _)
				.scan((e: Double, cum: Double) => e + cum)
				.map { cum => this.k * cum }
				.map { cache }
				.subscribe { hitrate.onNext(_) }

			hitrate.subscribe(subscriber)
			hitrate.onNext(0.0)
		})
		time.zipWith(feedbackLoop)((_, _))
	}
}