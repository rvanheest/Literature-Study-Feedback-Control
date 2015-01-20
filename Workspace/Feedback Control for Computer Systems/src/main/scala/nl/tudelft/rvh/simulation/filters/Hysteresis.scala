package nl.tudelft.rvh.simulation.filters

class Hysteresis(threshold: Double, prev: Double = 0, res: Double = 0) extends Filter {

	def update(u: Double): Hysteresis = {
		if (math.abs(u - prev) > threshold)
			new Hysteresis(threshold, u, u)
		else {
			new Hysteresis(threshold, prev, prev)
		}
	}
	
	def action = res
}