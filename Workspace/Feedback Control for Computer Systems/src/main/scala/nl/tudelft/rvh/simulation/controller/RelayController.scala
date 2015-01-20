package nl.tudelft.rvh.simulation.controller

class RelayController(err: Double = 0) extends Controller {

	def update(error: Double): RelayController = {
		if (error == 0)
			new RelayController(0)
		else
			new RelayController(error / math.abs(error))
	}
	
	def action: Double = err
}