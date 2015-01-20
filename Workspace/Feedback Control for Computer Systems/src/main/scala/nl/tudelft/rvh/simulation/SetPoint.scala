package nl.tudelft.rvh.scala.simulation

object Setpoint {

	def impulse(t: Long, t0: Long)(implicit DT: Double) = if (math.abs(t - t0) < DT) 1 else 0

	def step(t: Long, t0: Long) = if (t >= t0) 1 else 0

	def doubleStep(t: Long, t0: Long, t1: Long) = if (t >= t0 && t < t1) 1 else 0

	def harmonic(t: Long, t0: Long, tp: Long) = if (t >= t0) math.sin(2 * math.Pi * (t - t0) / tp) else 0

	def relay(t: Long, t0: Long, tp: Long) = if (t >= t0 && math.ceil(math.sin(2 * math.Pi * (t - t0) / tp)) > 0) 1 else 0
}