package nl.tudelft.rvh.chapter1;

public class Controller {

	private final double kp;
	private final double ki;
	
	private int i = 0; // cumulative error

	public Controller(double kp, double ki) {
		this.kp = kp;
		this.ki = ki;
	}

	public double work(int e) {
		this.i += e;
		
		return this.kp * e + this.ki * this.i;
	}
}
