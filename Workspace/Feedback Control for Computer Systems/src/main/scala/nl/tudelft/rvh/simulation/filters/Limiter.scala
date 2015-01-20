package nl.tudelft.rvh.simulation.filters

class Limiter(lo: Double, hi: Double, res: Double = 0) extends Filter {

	def update(u: Double): Limiter = new Limiter(lo, hi, math.max(lo, math.min(u, hi)))
	
	def action = res
}