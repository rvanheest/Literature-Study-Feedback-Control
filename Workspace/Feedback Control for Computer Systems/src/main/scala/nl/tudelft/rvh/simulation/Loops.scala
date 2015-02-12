package nl.tudelft.rvh.simulation

import nl.tudelft.rvh.Extensions.ObsExtensions.extendObservable
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import rx.lang.scala.schedulers.ComputationScheduler
import rx.lang.scala.subjects.BehaviorSubject

object Loops {

	def staticTest[A](initPlant: Component[Double, A], umax: Int, stepMax: Int, repeatMax: Int, tMax: Int): Observable[(Double, A)] = {
		val steps = (0 until stepMax).toObservable.observeOn(ComputationScheduler())
		val repeats = (0 until repeatMax).toObservable
		val ts = (0 until tMax).toObservable
		staticTest(initPlant, umax, steps, repeats, ts)
	}

	def staticTest[A](initPlant: Component[Double, A], umax: Int, steps: Observable[Int], repeats: Observable[Int], ts: Observable[Int]): Observable[(Double, A)] = {
		for {
			i <- steps
			u <- steps.size.single map { i.toDouble * umax / _ }
			plant <- repeats map { r => initPlant }
			y <- ts map (_ => u) interactWith plant last
		} yield (u, y)
	}

	def stepResponse[A, B](time: Observable[Long], setPoint: Long => A, plant: Component[A, B]) = time.map(setPoint).interactWith(plant)

	def openLoop[A, B](time: Observable[Long], setPoint: Long => A, controller: Component[A, B], plant: Component[B, _]) = {
		time.map(setPoint)
			.interactWith(controller ++ plant)
	}

	def closedLoop[A](time: Observable[Long], setPoint: Long => A, seed: A, components: Component[A, A], inverted: Boolean = false)(implicit n: Numeric[A]) = {
		import n._
		Observable[A](subscriber => {
			val y = BehaviorSubject(seed)
			y drop 1 subscribe subscriber
			
			time.map(setPoint)
				.zipWith(y)(_ - _)
				.map { error => if (inverted) -error else error }
				.interactWith(components)
				.subscribe(y)
		}).onBackpressureBuffer
	}
}