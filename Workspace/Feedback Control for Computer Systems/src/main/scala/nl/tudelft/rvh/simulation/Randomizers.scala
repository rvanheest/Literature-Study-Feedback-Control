package nl.tudelft.rvh.simulation

import scala.util.Random
import scala.annotation.tailrec

object Randomizers {

	def gaussian(mean: Double, stdDev: Double) = new Random().nextGaussian() * stdDev + mean

	def gammavariate(alpha: Double, beta: Double): Double = {
		@tailrec def whileLoop1(ainv: Double, bbb: Double, ccc: Double): Double = {
			val u1 = math.random
			if (1e-7 < u1 && u1 < .9999999) {
				val u2 = 1.0 - math.random
				val v = math.log(u1 / (1.0 - u1)) / ainv
				val x = alpha * math.exp(v)
				val z = u1 * u1 * u2
				val r = bbb + ccc * v - x
				if (r + 1.0 + math.log(4.5) - 4.5 * z >= 0 || r >= math.log(z)) {
					return x * beta
				}
			}
			
			whileLoop1(ainv, bbb, ccc)
		}
		
		@tailrec def getRandomValueLargerThan(minimum: Double): Double = {
			val u = math.random
			if (u > minimum) u
			else getRandomValueLargerThan(minimum)
		}
		
		@tailrec def whileLoop2: Double = {
			val u = math.random
			val b = (math.E + alpha) / math.E
			val p = b * u
			val x = if (p <= 1.0) math.pow(p, (1.0 / alpha)) else -math.log((b - p) / alpha)
			val u1 = math.random
			if (p > 1.0 && u1 <= math.pow(x, alpha - 1.0) || u1 <= math.exp(-x)) {
				return x * beta
			}
			return whileLoop2
		}
		
		if (alpha <= 0.0 || beta <= 0.0) {
			return -1
		}

		if (alpha > 1.0) {
			val ainv = math.sqrt(2.0 * alpha - 1.0)
			val bbb = alpha - math.log(4)
			val ccc = alpha + ainv

			whileLoop1(ainv, bbb, ccc)
		}
		else if (alpha == 1.0) {
			val u = getRandomValueLargerThan(1e-7)
			return -math.log(u) * beta
		}
		else whileLoop2
	}

	def betavariate(alpha: Double, beta: Double) = {
		val y = gammavariate(alpha, 1)
		if (y == 0) 0.0
		else y / (y + gammavariate(beta, 1))
	}
}
