package nl.tudelft.rvh.simulation

class PIDController(kp: Double, ki: Double, kd: Double = 0.0, integral: Double = 0, deriv: Double = 0, prev: Double = 0)(implicit DT: Double) extends Component {

	def update(error: Double): PIDController = {
		val i = integral + DT * error
		val d = (error - prev) / DT
		
		new PIDController(kp, ki, kd, i, d, error)
	}

	def action = prev * kp + integral * ki + deriv * kd
}

class AdvController(kp: Double, ki: Double, kd: Double = 0, clamp: (Double, Double) = (-1e10, 1e10), smooth: Double = 1,
		integral: Double = 0, deriv: Double = 0, prev: Double = 0, unclamped: Boolean = true)(implicit DT: Double) extends Component {

	def update(error: Double): AdvController = {
		val i = if (unclamped) integral + DT * error else integral
		val d = smooth * (error - prev) / DT + (1 - smooth) * deriv
		
		val u = kp * error + ki * integral + kd * deriv
		val un = clamp._1 < u && u < clamp._2
		
		new AdvController(kp, ki, kd, clamp, smooth, i, d, error, un)
	}

	def action = prev * kp + integral * ki + deriv * kd
}

class RelayController(err: Double = 0) extends Component {

	def update(error: Double): RelayController = {
		if (error == 0)
			new RelayController(0)
		else
			new RelayController(error / math.abs(error))
	}
	
	def action: Double = err
}

class HysteresisRelayController(zone: Double, prev: Double = 0, res: Double = 0) extends Component {

	def update(error: Double): HysteresisRelayController = {
		val u = if (error > prev)
			if (error < zone) 0 else 1
		else
			if (error > -zone) 0 else -1
		new HysteresisRelayController(zone, error, u)
	}
	
	def action: Double = res
}

class DeadbandController(zone: Double, res: Double = 0) extends Component {

	def update(error: Double): DeadbandController = {
		if (error > zone)
			new DeadbandController(zone, error - zone)
		else if (error < -zone)
			new DeadbandController(zone, error + zone)
		else
			new DeadbandController(zone, 0)
	}
	
	def action: Double = res
}

class DeadbandRelayController(zone: Double, res: Double = 0) extends Component {

	def update(error: Double): DeadbandRelayController = {
		if (error > zone)
			new DeadbandRelayController(zone, 1)
		else if (error < -zone)
			new DeadbandRelayController(zone, -1)
		else
			new DeadbandRelayController(zone, 0)
	}

	def action: Double = res
}

class AsymmController(kp: Double, ki: Double, kd: Double = 0.0, integral: Double = 0, deriv: Double = 0, prev: Double = 0)(implicit DT: Double) extends Component {
	
	def update(error: Double): AsymmController = {
		var e = error
		if (e > 0) e /= 20.0
		
		val i = integral + DT * e
		val d = (prev - e) / DT
		
		new AsymmController(kp, ki, kd, i, d, e)
	}

	def action = prev * kp + integral * ki + deriv * kd
}

class SpecialController(period1: Int, period2: Int, t: Int = 0, res: Int = 0) extends Component {
	
	def update(error: Double): SpecialController = {
		if (error > 0)
			new SpecialController(period1, period2, period1, 1)
		else if (t - 1 == 0)
			new SpecialController(period1, period2, period2, -1)
		else
			new SpecialController(period1, period2, t - 1, 0)
	}
	
	def action = res
}