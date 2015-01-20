package nl.tudelft.rvh.simulation.filters

// calculates the unweighted average over its last n inputs
class FixedFilter(n: Int, data: List[Double] = List()) extends Filter {

	def update(u: Double): FixedFilter = {
		val list = (if (data.length >= n) data drop 1 else data) :+ u
		new FixedFilter(n, list)
	}
	
	def action = data.sum / data.length
}