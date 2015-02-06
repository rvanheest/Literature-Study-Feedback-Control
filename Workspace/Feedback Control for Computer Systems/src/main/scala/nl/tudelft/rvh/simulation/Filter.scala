package nl.tudelft.rvh.simulation

// reproduces the input
class Identity[A](value: A) extends Component[A, A] {
	def update(u: A): Identity[A] = new Identity(u)
	
	def action = value
}

// calculates the unweighted average over its last n inputs
class FixedFilter(n: Int, data: List[Double] = List()) extends Component[Double, Double] {
	def update(u: Double): FixedFilter = {
		val list = (if (data.length >= n) data drop 1 else data) :+ u
		new FixedFilter(n, list)
	}
	
	def action = data.sum / data.length
}

// implementation of the exponential smoothing algorithm s(t) = a*x(t) + (1-a)*s(t-1)
class RecursiveFilter(alpha: Double, y: Double = 0) extends Component[Double, Double] {
	def update(u: Double): RecursiveFilter = {
		val res = alpha * u + (1 - alpha) * y
		new RecursiveFilter(alpha, res)
	}
	
	def action = y
}

class Limiter(lo: Double, hi: Double, res: Double = 0) extends Component[Double, Double] {
	def update(u: Double): Limiter = new Limiter(lo, hi, math.max(lo, math.min(u, hi)))
	
	def action = res
}

// maintains a cumulative sum of all inputs
class Integrator(data: Double = 0)(implicit DT: Double) extends Component[Double, Double] {
	def update(u: Double): Integrator = new Integrator(data + u)
	
	def action = DT * data
}

class Discretizer(binwidth: Double, res: Double = 0) extends Component[Double, Double] {
	def update(u: Double): Discretizer = {
		val dis = binwidth * math.floor(u / binwidth)
		new Discretizer(binwidth, dis)
	}
	
	def action = res
}

class Hysteresis(threshold: Double, prev: Double = 0, res: Double = 0) extends Component[Double, Double] {
	def update(u: Double): Hysteresis = {
		if (math.abs(u - prev) > threshold)
			new Hysteresis(threshold, u, u)
		else {
			new Hysteresis(threshold, prev, prev)
		}
	}
	
	def action = res
}