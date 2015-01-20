package nl.tudelft.rvh.simulation.controller

class DeadbandController(zone: Double, res: Double = 0) extends Controller {

	def update(error: Double): DeadbandController = {
		if (error > zone)
			new DeadbandController(zone, error - zone)
		else if (error < -zone)
			new DeadbandController(zone, error + zone)
		else
			new DeadbandController(zone, 0)
	}
	
	def action: Double = res
}