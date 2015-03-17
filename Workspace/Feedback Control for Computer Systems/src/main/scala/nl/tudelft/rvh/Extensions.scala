package nl.tudelft.rvh

import nl.tudelft.rvh.simulation.Component
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions

object Extensions {

	object RoundingExtensions {
		class ExtendedDouble(d: Double) {
			def roundAt(p: Int) = {
				val s = math.pow(10, p)
				math.round(d * s) / s
			}
		}

		implicit def extendDouble(d: Double) = new ExtendedDouble(d)
	}

	object ObsExtensions {
		class ExtendedObservable[T](obs: Observable[T]) {
			def delay(steps: Int, initVal: T) = (List.fill(steps)(initVal).toObservable ++ obs) slidingBuffer (steps, 1) map (_ head)
		}

		implicit def extendObservable[T](obs: Observable[T]) = new ExtendedObservable(obs)
	}
}