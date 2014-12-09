package nl.tudelft.rvh.rxscalafx

import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node
import rx.lang.scala.Observable
import rx.lang.scala.Subscriber
import rx.lang.scala.JavaConversions

object Observables {

	def fromNodeEvents[T <: Event](source: Node, eventType: EventType[T]): Observable[T] = {
		val x = nl.tudelft.rvh.rxjavafx.Observables.fromNodeEvents(source, eventType)
		JavaConversions.toScalaObservable(x)
	}
}