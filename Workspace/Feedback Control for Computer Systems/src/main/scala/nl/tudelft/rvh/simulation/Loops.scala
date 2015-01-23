package nl.tudelft.rvh.simulation

import nl.tudelft.rvh.Extensions.ObsExtensions.extendDoubleObservable
import nl.tudelft.rvh.simulation.controller.Controller
import nl.tudelft.rvh.simulation.filters.Filter
import nl.tudelft.rvh.simulation.filters.Identity
import nl.tudelft.rvh.simulation.plant.Plant
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import rx.lang.scala.subjects.BehaviorSubject

object Loops {

	def staticTest(initPlant: Plant, umax: Int, stepMax: Int, repeatMax: Int, tMax: Int): Observable[Double] = {
		val steps = (0 until stepMax).toObservable
		val repeats = (0 until repeatMax).toObservable
		val ts = (0 until tMax).toObservable
		staticTest(initPlant, umax, steps, repeats, ts)
	}

	def staticTest(initPlant: Plant, umax: Int, steps: Observable[Int], repeats: Observable[Int], ts: Observable[Int]) = {
		for {
			i <- steps
			u <- steps.size.single map { i.toDouble * umax / _ }
			plant <- repeats map { r => initPlant }
			y <- ts map (_ => u) interactWith plant last
		} yield y
	}

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