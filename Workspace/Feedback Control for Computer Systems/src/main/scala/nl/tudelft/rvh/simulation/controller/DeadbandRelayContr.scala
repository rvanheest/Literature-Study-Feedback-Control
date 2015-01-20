package nl.tudelft.rvh.simulation.controller

class DeadbandRelayController(zone: Double, res: Double = 0) extends Controller {

	def update(error: Double): DeadbandRelayController = {
		if (error > zone)
			new DeadbandRelayController(zone, 1)
		else if (error < -zone)
			new DeadbandRelayController(zone, -1)
		else
			new DeadbandRelayController(zone, 0)
	}

	def action: Double = res
}