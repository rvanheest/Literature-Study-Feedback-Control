package nl.tudelft.rvh.scala.chapter4

import nl.tudelft.rvh.scala.Extensions.RoundingExtensions.extendDouble

class SpeedSystem(var speed: Double = 10) {
	def interact(power: Double) = {
		if (power <= 0) {
			speed = (0.90 * speed) roundAt 1
		}
		else {
			speed = (speed + power) roundAt 1
		}
		speed
	}
}