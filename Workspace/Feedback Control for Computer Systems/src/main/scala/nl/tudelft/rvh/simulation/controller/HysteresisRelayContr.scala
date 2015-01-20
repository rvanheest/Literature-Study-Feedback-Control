package nl.tudelft.rvh.simulation.controller

class HysteresisRelayController(zone: Double, prev: Double = 0, res: Double = 0) extends Controller {

	def update(error: Double): HysteresisRelayController = {
		val u = if (error > prev)
			if (error < zone) 0 else 1
		else
			if (error > -zone) 0 else -1
		new HysteresisRelayController(zone, error, u)
	}
	
	def action: Double = res
}