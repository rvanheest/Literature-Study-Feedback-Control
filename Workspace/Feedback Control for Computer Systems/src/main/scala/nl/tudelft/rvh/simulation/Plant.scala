package nl.tudelft.rvh.simulation

class Boiler(g: Double = 0.01, y: Double = 0)(implicit DT: Double) extends Component[Double, Double] {

	def update(u: Double) = new Boiler(g, y + DT * (u - g * y))

	def action = y
}

class Spring(x: Double = 0, v: Double = 0, m: Double = 0.1, k: Double = 1, g: Double = 0.05)(implicit DT: Double) extends Component[Double, Double] {

	def update(u: Double) = {
		val a = u - k * x - g * v
		val vv = v + DT * a
		val xx = x + DT * vv
		
		new Spring(xx, vv, m, k, g)
	}

	def action = x
}

class Cache(size: Int, demand: Long => Int, internalTime: Long = 0, cache: Map[Int, Long] = Map(), res: Boolean = false) extends Component[Double, Boolean] {

	def update(u: Double): Cache = {
		val time = internalTime + 1
		val newSize = math.max(0, math floor u)
		val item = demand(time)

		if (cache contains item) {
			val newCache = cache + (item -> time)

			new Cache(newSize toInt, demand, time, newCache, true)
		}
		else if (cache.size >= size) {
			val n = 1 + cache.size - size
			val vk = cache map { case (i, l) => (l, i) }
			val newCache = (cache /: vk.map { case (l, _) => l }.toList.sorted.take(n).map(vk(_)))(_ - _)

			new Cache(newSize.toInt, demand, time, newCache + (item -> time), false)
		}
		else {
			val newCache = cache + (item -> time)

			new Cache(newSize.toInt, demand, time, newCache, false)
		}
	}

	def action: Boolean = res
}

class AdPublisher(scale: Int, minPrice: Int, relWidth: Double = 0.1, value: Double = 0.0) extends Component[Double, Double] {
	
	def update(u: Double): AdPublisher = new AdPublisher(scale, minPrice, relWidth, u)

	def action: Double = {
		if (value <= minPrice) {
			0
		}
		else {
			val mean = scale * math.log(value / minPrice)
			val demand = math.floor(Randomizers.gaussian(mean, relWidth * mean))
			math.max(0, demand)
		}
	}
}

class ServerPool(n: Int, queue: Double = 0, server: () => Double, load: () => Double, res: Double = 0.0) extends Component[Double, Double] {
	
	def update(u: Double): ServerPool = {
		val l = load()
		
		if (l == 0) {
			new ServerPool(n , l, server, load, 1)
		}
		else {
			val nNew = math.max(0, math.round(u).toInt)
			val completed = math.min((0 until nNew).map { _ => server() }.sum, l)
			new ServerPool(nNew, queue - completed, server, load, completed/l)
		}
	}
	
	def action: Double = res
}