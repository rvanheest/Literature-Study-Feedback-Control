package nl.tudelft.rvh.scala.chapter4

class PI(val prop: Double = 0, val integral: Double = 0) {
	def work(error: Double): PI = {
		new PI(error, integral + error)
	}

	def controlAction(kp: Double, ki: Double) = {
		prop * kp + integral * ki
	}
}