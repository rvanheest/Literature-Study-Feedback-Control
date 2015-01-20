package nl.tudelft.rvh.scala.simulation.filters

// implementation of the exponential smoothing algorithm s(t) = a*x(t) + (1-a)*s(t-1)
class RecursiveFilter(alpha: Double, y: Double = 0) extends Filter {

	def update(u: Double): RecursiveFilter = {
		val res = alpha * u + (1 - alpha) * y
		new RecursiveFilter(alpha, res)
	}
	
	def action = y
}