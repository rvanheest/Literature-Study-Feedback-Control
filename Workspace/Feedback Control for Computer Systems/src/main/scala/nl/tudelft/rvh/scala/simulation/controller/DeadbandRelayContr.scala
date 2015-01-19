package nl.tudelft.rvh.scala.simulation.controller

class DeadbandRelayContr(zone: Double, res: Double = 0) extends Controller {

	def update(error: Double): DeadbandRelayContr = {
		if (error > zone)
			new DeadbandRelayContr(zone, 1)
		else if (error < -zone)
			new DeadbandRelayContr(zone, -1)
		else
			new DeadbandRelayContr(zone, 0)
	}

	def action: Double = res
}