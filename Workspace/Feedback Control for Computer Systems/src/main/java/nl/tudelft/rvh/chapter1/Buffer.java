package nl.tudelft.rvh.chapter1;

import java.util.Random;

public class Buffer {

	private final int maxWip; // max work in progress (max #items in ready pool)
	private final int maxFlow; // max flow from queue to downstream

	private int queued = 0;
	private int wip = 0;

	/**
	 * Constructs a buffer with maximum size of the ready pool and a maximum flow out of the queue.
	 * 
	 * @param maxWip The maximum number of items in the ready pool
	 * @param maxFlow The maximum number of items flowing out of the queue
	 */
	public Buffer(int maxWip, int maxFlow) {
		this.maxWip = maxWip;
		this.maxFlow = maxFlow;
	}

	/**
	 * Performs <code>u</code> work upstream puts it in the ready pool. A random percentage is taken
	 * out from the ready pool and put into the queue. Another random percentage is taken from the
	 * queue and goes downstream.
	 * 
	 * @param u the amount of work done upstream
	 * @return the length of the queue after processing 1 timeunit.
	 */
	public int work(double u) {
		return this.work(new Random(), u);
	}

	/**
	 * Performs <code>u</code> work upstream puts it in the ready pool. A random percentage is taken
	 * out from the ready pool and put into the queue. Another random percentage is taken from the
	 * queue and goes downstream. This method is used for testing with a controlled randomization.
	 * 
	 * @param random the randomizer used for transfers from the ready pool and the queue
	 * @param u the amount of work done upstream
	 * @return the length of the queue after processing 1 timeunit.
	 */
	public int work(Random random, double u) {
		// Add to ready pool
		// 0 <= u <= maxWip
		this.wip += Math.toIntExact(Math.min(Math.max(0, Math.round(u)), this.maxWip));

		// Transfer r items from ready pool to queue
		int r = Math.toIntExact(Math.round(random.nextDouble() * this.wip));
		this.wip -= r;
		this.queued += r;

		// Release s items from queue to downstream process
		int s = Math.toIntExact(Math.round(random.nextDouble() * this.maxFlow));
		s = Math.min(s, this.queued); // s <= #items in queue
		this.queued -= s;

		return this.queued;
	}
}
