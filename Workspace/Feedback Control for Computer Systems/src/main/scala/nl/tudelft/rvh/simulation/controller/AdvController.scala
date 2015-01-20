package nl.tudelft.rvh.scala.simulation.controller

class AdvController(kp: Double, ki: Double, kd: Double = 0,
		clamp: (Double, Double) = (-1e10, 1e10), smooth: Double = 1,
		integral: Double = 0, deriv: Double = 0, prev: Double = 0,
		unclamped: Boolean = true)(implicit DT: Double) extends Controller {

	def update(error: Double): AdvController = {
		val i = if (unclamped) integral + DT * error else integral
		val d = smooth * (error - prev) / DT + (1 - smooth) * deriv
		
		val u = kp * error + ki * integral + kd * deriv
		val un = clamp._1 < u && u < clamp._2
		
		new AdvController(kp, ki, kd, clamp, smooth, i, d, error, un)
	}

	def action = prev * kp + integral * ki + deriv * kd
}