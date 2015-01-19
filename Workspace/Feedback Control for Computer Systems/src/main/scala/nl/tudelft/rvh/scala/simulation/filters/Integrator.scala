package nl.tudelft.rvh.scala.simulation.filters

// maintains a cumulative sum of all inputs
class Integrator(data: Double = 0)(implicit DT: Double) extends Filter {

	def update(u: Double): Integrator = new Integrator(data + u)
	
	def action = DT * data
}