package nl.tudelft.rvh.scala

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
		import rx.lang.scala.Observable

		class ExtendedObservable[T](obs: Observable[T]) {

			def delay(steps: Int, initVal: T) = (Observable.from(List.fill(steps)(initVal)) ++ obs)
				.slidingBuffer(steps, 1).map(_.head)
		}

		implicit def extendObservable[T](obs: Observable[T]) = new ExtendedObservable(obs)
	}
}