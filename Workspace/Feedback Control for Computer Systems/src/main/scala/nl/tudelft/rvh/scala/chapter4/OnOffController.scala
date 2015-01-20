package nl.tudelft.rvh.scala.chapter4

import scala.concurrent.duration.DurationInt

import nl.tudelft.rvh.scala.ChartTab
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject

class OnOffController extends ChartTab("Chapter 4 - On/Off controller", "Cruise control", "time", "speed") {

	def seriesName(): String = "Cruise control with on/off controller"

	override def time = super.time take 60
	
	def setpoint(time: Long) = if (time < 20) 15 else if (time < 40) 5 else 20
	
	def simulation(): Observable[(Number, Number)] = {
		val cc = new OnOffSpeedSystem
		
		val feedbackLoop = Observable[Int](subscriber => {
			val speed = BehaviorSubject(cc.speed)
			speed.subscribe(subscriber)
			
			time.map(setpoint)
				.zipWith(speed)(_ - _)
				.map { x => if (x > 0) true else false }
				.map(cc.interact)
				.subscribe(speed)
		})
		time.zipWith(feedbackLoop)((_, _))
	}

	def simulationForGitHub(): Observable[Int] = {
		def setPoint(time: Int): Int = if (time < 10) 15 else if (time < 20) 10 else 20
		val cc = new OnOffSpeedSystem
		
		Observable(subscriber => {
			val speed = BehaviorSubject(cc.speed)
			speed.subscribe(subscriber)
			
			Observable.from(0 until 40)
				.map(setPoint)
				.zipWith(speed)(_ - _)
				.map { x => if (x > 0) true else false }
				.map(cc.interact)
				.subscribe(speed)
		})
	}
}