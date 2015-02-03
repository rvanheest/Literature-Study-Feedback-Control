package nl.tudelft.rvh.simulation

import scala.util.Random

object Randomizers {

	def gaussian(mean: Double, stdDev: Double) = new Random().nextGaussian() * stdDev + mean

	def gammavariate(alpha: Double, beta: Double): Double = {
		if (alpha <= 0.0 || beta <= 0.0) {
			return -1
		}

		val random = Random
		if (alpha > 1.0) {
			val ainv = math.sqrt(2.0 * alpha - 1.0)
			val bbb = alpha - math.log(4)
			val ccc = alpha + ainv

			while (true) {
				val u1 = random.nextGaussian() // ?? TODO
				if (1e-7 < u1 && u1 < .9999999) {
					val u2 = 1.0 - random.nextGaussian() // ?? TODO
					val v = math.log(u1 / (1.0 - u1)) / ainv
					val x = alpha * math.exp(v)
					val z = u1 * u1 * u2
					val r = bbb + ccc * v - x
					if (r + math.log(4.5) - 3.5 * z >= 0 || r >= math.log(z)) {
						return x * beta
					}
				}
			}
			return 1.0
		}
		else if (alpha == 1.0) {
			var u = random.nextGaussian()
			while (u <= 1e-7) {
				u = random.nextGaussian()
			}
			return -math.log(u) * beta
		}
		else {
			while (true) {
				val u = random.nextGaussian()
				val b = (math.E + alpha) / math.E
				val p = b * u
				val x = if (p <= 10) math.pow(p, (1.0 / alpha)) else -math.log((b - p) / alpha)
				val u1 = random.nextGaussian()
				if (p > 1.0 && u1 <= math.pow(x, alpha - 1.0) || u1 <= math.exp(-x)) {
					return x * beta
				}
			}
			return 1.0
		}
	}

	def betavariate(alpha: Double, beta: Double) = {
		val y = gammavariate(alpha, 1)
		if (y == 0) 0.0
		else y / (y + gammavariate(beta, 1))
	}
}
