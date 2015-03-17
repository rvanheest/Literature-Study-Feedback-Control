package nl.tudelft.rvh.chapter2

object BookExample extends App {

	def cache(size: Double): Double = math.max(0, math.min(1, size / 100))
	
	def setPoint(time: Long): Double = if (time < 30) 0.6 else if (time < 60) 0.8 else if (time < 90) 0.1 else 0.4
	val gain = 160
	
	var hitrate = 0.0
	var cumErr = 0.0
	
	println(0, hitrate)
	
	for (t <- 0 until 120) {
		val trErr = setPoint(t) - hitrate
		cumErr += trErr
		val controlAction = gain * cumErr
		hitrate = cache(controlAction)
		
		println(t + 1, hitrate)
	}
}