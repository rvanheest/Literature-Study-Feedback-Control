package nl.tudelft.rvh.scala.simulation

import nl.tudelft.rvh.scala.Extensions.ObsExtensions.extendDoubleObservable
import nl.tudelft.rvh.scala.simulation.controller.Controller
import nl.tudelft.rvh.scala.simulation.filters.Filter
import nl.tudelft.rvh.scala.simulation.filters.Identity
import nl.tudelft.rvh.scala.simulation.plant.Plant
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject

object Loops {

	def stepResponse(time: Observable[Long], setPoint: Long => Double, plant: Plant) = {
		time.map(setPoint)
			.map(_ toDouble)
			.interactWith(plant)
	}

	def openLoop(time: Observable[Long], setPoint: Long => Double, controller: Controller, plant: Plant) = {
		time.map(setPoint)
			.map(_ toDouble)
			.interactWith(controller)
			.interactWith(plant)
	}

	def closedLoop(time: Observable[Long], setPoint: Long => Double, controller: Controller, plant: Plant,
		inverted: Boolean = false, actuator: Filter = new Identity, filter: Filter = new Identity) = {
		Observable[Double](subscriber => {
			val y = BehaviorSubject(0.0)
			y drop 1 subscribe subscriber

			time.map(setPoint)
				.zipWith(y)(_ - _)
				.map { e => if (inverted) -e else e }
				.interactWith(controller)
				.interactWith(actuator)
				.interactWith(plant)
				.interactWith(filter)
				.subscribe(y)
		})
	}
}