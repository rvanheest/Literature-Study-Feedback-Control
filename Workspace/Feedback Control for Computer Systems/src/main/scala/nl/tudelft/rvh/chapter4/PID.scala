package nl.tudelft.rvh.scala.chapter4

class PID(val prop: Double = 0, val integral: Double = 0, val deriv: Double = 0, prevErr: Double = 0) {
	def work(error: Double, DT: Double = 1): PID = {
		new PID(error, integral + error, (error - prevErr) / DT, error)
	}

	def controlAction(kp: Double, ki: Double, kd: Double) = {
		prop * kp + integral * ki + deriv * kd
	}
}