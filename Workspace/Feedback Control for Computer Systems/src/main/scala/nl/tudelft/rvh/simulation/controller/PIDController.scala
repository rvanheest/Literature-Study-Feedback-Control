package nl.tudelft.rvh.simulation.controller

class PIDController(kp: Double, ki: Double, kd: Double = 0.0,
		integral: Double = 0, deriv: Double = 0, prev: Double = 0)
		(implicit DT: Double) extends Controller {

	def update(error: Double): PIDController = {
		val i = integral + DT * error
		val d = (error - prev) / DT
		
		new PIDController(kp, ki, kd, i, d, error)
	}

	def action = prev * kp + integral * ki + deriv * kd
}
