package nl.tudelft.rvh.chapter1

import scala.util.Random

class Buffer(maxWip: Int, maxFlow: Int) {

	private var queued = 0
	private var wip = 0
	
	def work(u: Double): Int = work(new Random, u)
	
	def work(random: Random, u: Double): Int = {
		// Add to ready pool
		// 0 <= u <= maxWip
		wip += math.min(math.max(0, math round u), this.maxWip).toInt
		
		// Transfer r items from ready pool to queue
		val r = math.round(random.nextDouble * this.wip).toInt
		this.wip -= r
		this.queued += r

		// Release s items from queue to downstream process
		// s <= #items in queue
		val s = math.min(math.round(random.nextDouble * this.maxFlow).toInt, queued)
		this.queued -= s;

		queued
	}
}