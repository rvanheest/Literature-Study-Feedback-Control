package nl.tudelft.rvh.simulation.filters

// reproduces the input
class Identity(value: Double = 0) extends Filter {

	def update(u: Double): Identity = new Identity(u)
	
	def action = value
}