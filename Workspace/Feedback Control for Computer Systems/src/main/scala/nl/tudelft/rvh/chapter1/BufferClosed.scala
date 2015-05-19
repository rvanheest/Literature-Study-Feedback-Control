package nl.tudelft.rvh.chapter1

import scala.concurrent.duration.DurationInt

import nl.tudelft.rvh.ChartTab
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ComputationScheduler
import rx.lang.scala.subjects.BehaviorSubject

class BufferClosed extends ChartTab("Chapter 1 - closed", "Closed simulation of random buffer simulation", "time", "queue size") {

	def seriesName = "closed simulation"

	override def time = Observable interval (1 millisecond, ComputationScheduler()) take 5000
	
	def setpoint(t: Long) = if (t < 100) 0 else if (t < 300) 50 else 10
	
	def simulation = {
		val buffer = new Buffer(10, 10)
		def control(e: Double, c: Double) = 1.25 * e + 0.01 * c
		
		Observable[Int](subscriber => {
			val queueLength = BehaviorSubject(0)
			queueLength subscribe subscriber
			
			time.map(setpoint)
				.zipWith(queueLength)(_ - _)
				.scan((0.0, 0.0))((c, e) => (e, e + c._2))
				.map((control(_, _)) tupled)
				.map(buffer work)
				.subscribe(queueLength)
		}).onBackpressureBuffer drop 1
	}
}