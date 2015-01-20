package nl.tudelft.rvh

import nl.tudelft.rvh.simulation.Component
import rx.lang.scala.Observable

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
			def delay(steps: Int, initVal: T) = {
				val start = Observable.from(List.fill(steps)(initVal))

				(start ++ obs).slidingBuffer(steps, 1).map(_.head)
			}
		}

		class ExtendedDoubleObservable(obs: Observable[Double]) {
			def interactWith(initVal: Component) = {
				obs.scan(initVal)((r, t) => r.update(t))
					.drop(1)
					.map(comp => comp.action)
			}
		}

		implicit def extendObservable[T](obs: Observable[T]) = new ExtendedObservable(obs)
		implicit def extendDoubleObservable(obs: Observable[Double]) = new ExtendedDoubleObservable(obs)
	}
}