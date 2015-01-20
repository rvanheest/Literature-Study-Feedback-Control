package nl.tudelft.rvh.simulation.plant

import nl.tudelft.rvh.simulation.Component

class Boiler(g: Double = 0.01, y: Double = 0)(implicit DT: Double) extends Plant {

	def update(u: Double) = new Boiler(g, y + DT * (u - g * y))

	def action = y
}