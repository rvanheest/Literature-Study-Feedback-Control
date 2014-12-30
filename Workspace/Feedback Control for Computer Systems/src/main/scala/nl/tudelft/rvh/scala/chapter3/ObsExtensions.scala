package nl.tudelft.rvh.scala.chapter3

import rx.lang.scala.Observable

object ObsExtensions {
	class ExtendedObservable[T](obs: Observable[T]) {

		def delay(steps: Int, initVal: T) = (Observable.from(List.fill(steps)(initVal)) ++ obs)
			.slidingBuffer(steps, 1).map(_.head)
	}
	
	implicit def extendObservable[T](obs: Observable[T]) = new ExtendedObservable(obs)
}
