package nl.tudelft.rvh.scala

import scala.concurrent.duration.DurationInt

import rx.lang.scala.Observable

class ScalaExampleTab extends ScalaChartTab("ScalaExample", "Foobar", "Foo", "Bar") {

	def seriesName(): String = "series"

	def simulation(): Observable[(Number, Number)] = {
		Observable.interval(50 milliseconds)
			.drop(1)
			.take(12)
			.zipWith(List(23, 14, 15, 24, 34, 36, 22, 45, 43, 17, 29, 25))((_, _))
	}
}