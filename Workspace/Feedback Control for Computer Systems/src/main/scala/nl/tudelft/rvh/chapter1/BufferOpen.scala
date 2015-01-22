package nl.tudelft.rvh.chapter1

import scala.concurrent.duration.DurationInt
import nl.tudelft.rvh.ChartTab
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ComputationScheduler

class BufferOpen extends ChartTab("Chapter 1 - open", "Open simulation of random buffer simulation", "time", "queue size") {

	def seriesName = "closed simulation"
	
	override def time = Observable interval (1 millisecond, ComputationScheduler()) take 5000
	
	def setpoint(time: Long) = 5.0

	def simulation = {
		val buffer = new Buffer(10, 10)
		time.map(setpoint).map(buffer.work(_))
	}
}