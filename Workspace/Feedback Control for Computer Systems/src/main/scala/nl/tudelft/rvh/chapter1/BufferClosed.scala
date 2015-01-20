package nl.tudelft.rvh.scala.chapter1

import scala.concurrent.duration.DurationInt
import nl.tudelft.rvh.scala.ChartTab
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject

class BufferClosed extends ChartTab("Chapter 1 - closed", "Closed simulation of random buffer simulation", "time", "queue size") {

	def seriesName = "closed simulation"

	override def time = Observable interval (1 millisecond) take 5000
	
	def setpoint(t: Long) = if (t < 100) 0 else if (t < 300) 50 else 10
	
	def simulation = {
		val buffer = new Buffer(10, 10)
		def control(e: Double, c: Double) = 1.25 * e + 0.01 * c
		
		val feedbackLoop = Observable[Int](subscriber => {
			val queueLength = BehaviorSubject(0)
			queueLength subscribe subscriber
			
			time.map(setpoint)
				.zipWith(queueLength)(_ - _)
				.scan((0.0, 0.0))((c, e) => (e, e + c._2))
				.map(t => control(t._1, t._2))
				.map(buffer.work(_))
				.subscribe(queueLength)
		})
		
		time.zipWith(feedbackLoop.onBackpressureBuffer drop 1)((_, _))
	}
}