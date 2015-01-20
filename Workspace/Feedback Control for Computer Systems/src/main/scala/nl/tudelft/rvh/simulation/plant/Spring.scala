package nl.tudelft.rvh.scala.simulation.plant

class Spring(x: Double = 0, v: Double = 0, m: Double = 0.1, k: Double = 1, g: Double = 0.05)(implicit DT: Double) extends Plant {

	def update(u: Double) = {
		val a = u - k * x - g * v
		val vv = v + DT * a
		val xx = x + DT * vv
		
		new Spring(xx, vv, m, k, g)
	}

	def action = x
}