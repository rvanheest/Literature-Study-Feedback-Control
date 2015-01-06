package nl.tudelft.rvh.scala

object RoundingExtensions {
	class ExtendedDouble(d: Double) {
		def roundAt(p: Int) = {
			val s = math.pow(10, p)
			math.round(d * s) / s
		}
	}
	
	implicit def extendDouble(d: Double) = new ExtendedDouble(d)
}