package nl.tudelft.rvh.scala.simulation

object Setpoint {

	def impulse(t: Int, t0: Int)(implicit DT: Double) = if (math.abs(t - t0) < DT) 1 else 0

	def step(t: Int, t0: Int) = if (t >= t0) 1 else 0

	def doubleStep(t: Int, t0: Int, t1: Int) = if (t >= t0 && t < t1) 1 else 0

	def harmonic(t: Int, t0: Int, tp: Int) = if (t >= t0) math.sin(2 * math.Pi * (t - t0) / tp) else 0

	def relay(t: Int, t0: Int, tp: Int) = if (t >= t0 && math.ceil(math.sin(2 * math.Pi * (t - t0) / tp)) > 0) 1 else 0
}