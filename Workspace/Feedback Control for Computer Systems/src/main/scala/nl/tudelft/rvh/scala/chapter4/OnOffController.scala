package nl.tudelft.rvh.scala.chapter4

import scala.concurrent.duration.DurationInt

import nl.tudelft.rvh.scala.ScalaChartTab
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject

class OnOffController extends ScalaChartTab("Chapter 4 - On/Off controller", "Cruise control", "time", "speed") {

	def seriesName(): String = "Cruise control with on/off controller"
	
	def simulation(): Observable[(Number, Number)] = {
		val time = Observable.interval(50 milliseconds).take(60)
		def setPoint(time: Long): Int = if (time < 20) 15 else if (time < 40) 5 else 20
		val cc = new OnOffSpeedSystem
		
		val feedbackLoop = Observable[Int](subscriber => {
			val speed = BehaviorSubject(cc.speed)
			speed.subscribe(subscriber)
			
			time.map(setPoint)
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