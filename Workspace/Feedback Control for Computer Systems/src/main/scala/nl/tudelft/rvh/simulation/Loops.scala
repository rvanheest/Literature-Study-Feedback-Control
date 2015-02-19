package nl.tudelft.rvh.simulation

import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import rx.lang.scala.schedulers.ComputationScheduler
import rx.lang.scala.subjects.BehaviorSubject

object Loops {

	def staticTest[A, B](initPlant: Component[A, B], umax: Int, stepMax: Int, repeatMax: Int, tMax: Int)(implicit f: Double => A): Observable[(A, B)] = {
		val steps = (0 until stepMax).toObservable.observeOn(ComputationScheduler())
		val repeats = (0 until repeatMax).toObservable
		val ts = (0 until tMax).toObservable
		staticTest(initPlant, umax, steps, repeats, ts)
	}

	def staticTest[A, B](initPlant: Component[A, B], umax: Int, steps: Observable[Int], repeats: Observable[Int], ts: Observable[Int])(implicit f: Double => A): Observable[(A, B)] = {
		for {
			i <- steps
			u <- steps.size.single map { i.toDouble * umax / _ }
			plant <- repeats map { r => initPlant }
			y <- (ts map (_ => u) scan(plant))(_ update _) drop 1 map (_ action) last
		} yield (u, y)
	}

	def stepResponse[A, B](time: Observable[Long], setPoint: Long => A, plant: Component[A, B]) = (time map setPoint scan plant)(_ update _) drop 1 map (_ action)

	def openLoop[A, B](time: Observable[Long], setPoint: Long => A, controller: Component[A, B], plant: Component[B, _]) = {
		time.map(setPoint).scan(controller ++ plant)(_ update _) drop 1 map (_ action)
	}

	def closedLoop[A](time: Observable[Long], setPoint: Long => A, seed: A, components: Component[A, A], inverted: Boolean = false)(implicit n: Numeric[A]) = {
		import n._
		Observable[A](subscriber => {
			val y = BehaviorSubject(seed)
			y drop 1 subscribe subscriber
			
			time.map(setPoint)
				.zipWith(y)(_ - _)
				.map { error => if (inverted) -error else error }
				.scan(components)(_ update _)
				.drop(1)
				.map(_ action)
				.subscribe(y)
		}).onBackpressureBuffer
	}
	
	def closedLoop1[A](time: Observable[Long], setPoint: Long => A, seed: A, components: Component[A, A], inverted: Boolean = false)(implicit n: Numeric[A]) = {
		import n._
		Observable[Map[String, AnyVal]](subscriber => {
			val y = BehaviorSubject(seed)
			
			time.map(setPoint)
				.zipWith(y)(_ - _)
				.map { error => if (inverted) -error else error }
				.scan(components)(_ update _).drop(1)
				.doOnNext(comp => subscriber.onNext(comp.monitor))
				.map(_ action)
				.subscribe(y)
		}).onBackpressureBuffer
	}
}
