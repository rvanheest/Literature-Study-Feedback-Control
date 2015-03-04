package nl.tudelft.rvh.simulation

class PIDController(kp: Double, ki: Double, kd: Double = 0.0, integral: Double = 0, deriv: Double = 0, prev: Double = 0)(implicit DT: Double) extends Component[Double, Double] {

	def update(error: Double): PIDController = {
		val i = integral + DT * error
		val d = (error - prev) / DT

		new PIDController(kp, ki, kd, i, d, error)
	}

	def action = prev * kp + integral * ki + deriv * kd

	def monitor = Map("PID controller" -> action)
}

class AdvController(kp: Double, ki: Double, kd: Double = 0, clamp: (Double, Double) = (-1e10, 1e10), smooth: Double = 1,
		integral: Double = 0, deriv: Double = 0, prev: Double = 0, unclamped: Boolean = true)(implicit DT: Double) extends Component[Double, Double] {

	def update(error: Double): AdvController = {
		val i = if (unclamped) integral + DT * error else integral
		val d = smooth * (error - prev) / DT + (1 - smooth) * deriv

		val u = kp * error + ki * integral + kd * deriv
		val un = clamp._1 < u && u < clamp._2

		new AdvController(kp, ki, kd, clamp, smooth, i, d, error, un)
	}

	def action = prev * kp + integral * ki + deriv * kd

	def monitor = Map("Advanced controller" -> action)
}

class RelayController(err: Double = 0) extends Component[Double, Double] {

	def update(error: Double): RelayController = {
		if (error == 0)
			new RelayController(0)
		else
			new RelayController(error / math.abs(error))
	}

	def action: Double = err

	def monitor = Map("Relay controller" -> action)
}

class HysteresisRelayController(zone: Double, prev: Double = 0, res: Double = 0) extends Component[Double, Double] {

	def update(error: Double): HysteresisRelayController = {
		val u = if (error > prev)
			if (error < zone) 0 else 1
		else if (error > -zone) 0 else -1
		new HysteresisRelayController(zone, error, u)
	}

	def action: Double = res

	def monitor = Map("Hysteresis relay controller" -> action)
}

class DeadbandController(zone: Double, res: Double = 0) extends Component[Double, Double] {

	def update(error: Double): DeadbandController = {
		if (error > zone)
			new DeadbandController(zone, error - zone)
		else if (error < -zone)
			new DeadbandController(zone, error + zone)
		else
			new DeadbandController(zone, 0)
	}

	def action: Double = res

	def monitor = Map("Deadband controller" -> action)
}

class DeadbandRelayController(zone: Double, res: Double = 0) extends Component[Double, Double] {

	def update(error: Double): DeadbandRelayController = {
		if (error > zone)
			new DeadbandRelayController(zone, 1)
		else if (error < -zone)
			new DeadbandRelayController(zone, -1)
		else
			new DeadbandRelayController(zone, 0)
	}

	def action: Double = res

	def monitor = Map("Deadband relay controller" -> action)
}

class AsymmController(kp: Double, ki: Double, kd: Double = 0.0, integral: Double = 0, deriv: Double = 0, prev: Double = 0)(implicit DT: Double) extends Component[Double, Double] {

	def update(error: Double): AsymmController = {
		val e = if (error > 0) error / 20.0 else error

		val i = integral + DT * e
		val d = (prev - e) / DT

		new AsymmController(kp, ki, kd, i, d, e)
	}

	def action = prev * kp + integral * ki + deriv * kd

	def monitor = Map("Asymmetric controller" -> action)
}

class SpecialController(period1: Int, period2: Int, t: Int = 0, res: Int = 0) extends Component[Double, Double] {

	def update(error: Double): SpecialController = {
		if (error > 0)
			new SpecialController(period1, period2, period1, 1)
		else if (t - 1 == 0)
			new SpecialController(period1, period2, period2, -1)
		else
			new SpecialController(period1, period2, t - 1, 0)
	}

	def action = res

	def monitor = Map("Special controller" -> action)
}