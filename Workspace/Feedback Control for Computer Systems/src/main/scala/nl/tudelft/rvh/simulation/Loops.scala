package nl.tudelft.rvh.simulation

import scala.util.Random

import nl.tudelft.rvh.Extensions.ObsExtensions.extendDoubleObservable
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import rx.lang.scala.schedulers.ComputationScheduler
import rx.lang.scala.subjects.BehaviorSubject

object Loops {

	def staticTest(initPlant: Component, umax: Int, stepMax: Int, repeatMax: Int, tMax: Int): Observable[(Double, Double)] = {
		val steps = (0 until stepMax).toObservable.observeOn(ComputationScheduler())
		val repeats = (0 until repeatMax).toObservable
		val ts = (0 until tMax).toObservable
		staticTest(initPlant, umax, steps, repeats, ts)
	}

	def staticTest(initPlant: Component, umax: Int, steps: Observable[Int], repeats: Observable[Int], ts: Observable[Int]) = {
		for {
			i <- steps
			u <- steps.size.single map { i.toDouble * umax / _ }
			plant <- repeats map { r => initPlant }
			y <- ts map (_ => u) interactWith plant last
		} yield (u, y)
	}

	def stepResponse(time: Observable[Long], setPoint: Long => Double, plant: Component) = time.map(setPoint).interactWith(plant)

	def openLoop(time: Observable[Long], setPoint: Long => Double, controller: Component, plant: Component) = {
		time.map(setPoint)
			.map(_ toDouble)
			.interactWith(controller ++ plant)
	}

	def closedLoop(time: Observable[Long], setPoint: Long => Double, controller: Component, plant: Component,
		inverted: Boolean = false, actuator: Component = new Identity, filter: Component = new Identity) = {
		Observable[Double](subscriber => {
			val y = BehaviorSubject(0.0)
			y drop 1 subscribe subscriber

			time.map(setPoint)
				.zipWith(y)(_ - _)
				.map { e => if (inverted) -e else e }
				.interactWith(controller ++ actuator ++ plant ++ filter)
				.subscribe(y)
		})
	}
}