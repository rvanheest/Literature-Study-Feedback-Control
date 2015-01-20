package nl.tudelft.rvh.simulation.filters

class Discretizer(binwidth: Double, res: Double = 0) extends Filter {

	def update(u: Double): Discretizer = {
		val dis = binwidth * math.floor(u / binwidth)
		new Discretizer(binwidth, dis)
	}
	
	def action = res
}