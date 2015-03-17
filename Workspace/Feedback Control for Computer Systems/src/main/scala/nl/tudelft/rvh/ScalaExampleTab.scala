package nl.tudelft.rvh

import scala.concurrent.duration.DurationInt

import rx.lang.scala.Observable

class ScalaExampleTab extends ChartTab("ScalaExample", "Foobar", "Foo", "Bar") {

	def seriesName(): String = "series"
	
	override def time = super.time drop 1 take 12
	
	def setpoint(t: Long) = 20

	def simulation()= {
		Observable.from(List(23, 14, 15, 24, 34, 36, 22, 45, 43, 17, 29, 25))
	}
}