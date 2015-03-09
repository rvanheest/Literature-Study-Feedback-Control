package nl.tudelft.rvh.simulation

class Boiler(g: Double = 0.01, y: Double = 0)(implicit DT: Double) extends Component[Double, Double] {

	def update(u: Double) = new Boiler(g, y + DT * (u - g * y))

	def action = y

	def monitor = Map("Boiler" -> action)
}

class Spring(x: Double = 0, v: Double = 0, m: Double = 0.1, k: Double = 1, g: Double = 0.05)(implicit DT: Double) extends Component[Double, Double] {

	def update(u: Double) = {
		val a = u - k * x - g * v
		val vv = v + DT * a
		val xx = x + DT * vv

		new Spring(xx, vv, m, k, g)
	}

	def action = x

	def monitor = Map("Spring" -> action)
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

	def monitor = Map("Cache hit rate" -> action, "Cache size" -> cache.size)
}

class AdPublisher(scale: Int, minPrice: Int, relWidth: Double = 0.1, value: Int = 0, price: Double = 0.0) extends Component[Double, Int] {

	def update(price: Double): AdPublisher = {
		if (price <= minPrice) {
			new AdPublisher(scale, minPrice, relWidth, 0, price)
		}
		else {
			val mean = scale * math.log(price / minPrice)
			val demand = math.floor(Randomizers.gaussian(mean, relWidth * mean)).toInt
			new AdPublisher(scale, minPrice, relWidth, math.max(0, demand), price)
		}
	}

	def action: Int = {
		value
	}

	def monitor = Map("Impressions" -> value, "Price" -> price)
}

object ServerPoolHelpers {
	
	case class ServerData(workers: Int, queueLoad: Double, completion: Double)
	case class LatencyServerData(workers: Int, queueLoad: Double, completion: Double, latency: Int, pending: List[Int])
	
	def abstractServerPool(n: Int, queue: Double, server: () => Double)(u: Int) = {
		val nNew = math.max(0, u)
		val completed = math.min((0 until nNew).map { _ => server() }.sum, queue)
		val qNew = queue - completed
		
		new ServerData(nNew, qNew, completed)
	}
	
	def serverPool(n: Int, server: () => Double, load: () => Double)(u: Int) = {
		val l = load()
		
		if (l == 0) new ServerData(n, 0, 1)
		else {
			val ServerData(nNew, qNew, completed) = abstractServerPool(n, l, server)(u)
			new ServerData(nNew, 0, completed / l)
		}
	}
	
	def queueingServerPool (n: Int, queue: Double, server: () => Double, load: () => Double)(u: Int) = {
		val l = load()
		val ServerData(nNew, qNew, completed) = abstractServerPool(n, queue + l, server)(u)
		ServerData(nNew, qNew, l - completed)
	}

	def serverPoolWithLatency(n: Int, server: () => Double, load: () => Double, latency: Int, pending: List[Int])(u: Int) = {
		val nNew = math.max(0, u)
		
		if (nNew <= n) {
			val ServerData(nnNew, qNew, completed) = serverPool(n, server, load)(nNew)
			new LatencyServerData(nnNew, qNew, completed, latency, pending)
		}
		else {
			val p2 = pending.map { _ - 1 }
			val newlyActive = p2.count { _ == 0 }
			val nnNew = n + newlyActive
			val p3 = p2.drop(newlyActive) ++ List.fill(nNew - nnNew)(latency)
			
			val ServerData(nnnNew, qNew, completed) = serverPool(nnNew, server, load)(nnNew)
			new LatencyServerData(nnnNew, qNew, completed, latency, p3)
		}
	}
}

class ServerPool(n: Int, server: () => Double, load: () => Double, res: Double = 0.0) extends Component[Int, Double] {

	import nl.tudelft.rvh.simulation.ServerPoolHelpers.ServerData
	
	def update(u: Int): ServerPool = {
		val ServerData(nNew, _, completed) = ServerPoolHelpers.serverPool(n, server, load)(u)
		new ServerPool(nNew, server, load, completed)
	}

	def action: Double = res

	def monitor = Map("Completion rate" -> res, "Servers" -> n)
}

class ServerPoolWithLatency(n: Int, server: () => Double, load: () => Double, latency: Int, pending: List[Int] = List.empty, res: Double = 0.0) extends Component[Int, Double] {
	
	import nl.tudelft.rvh.simulation.ServerPoolHelpers.LatencyServerData
	
	def update(u: Int): ServerPoolWithLatency = {
		val LatencyServerData(nNew, _, completed, l, p) = ServerPoolHelpers.serverPoolWithLatency(n, server, load, latency, pending)(u)
		new ServerPoolWithLatency(nNew, server, load, l, p, completed)
	}

	def action: Double = res

	def monitor = Map("Completion rate" -> res, "Servers" -> n)
}
