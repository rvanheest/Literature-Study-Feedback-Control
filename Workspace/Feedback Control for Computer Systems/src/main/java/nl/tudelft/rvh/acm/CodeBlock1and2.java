package nl.tudelft.rvh.acm;

public class CodeBlock1and2 {

	public static void main(String[] args) {
		simulation();
	}

	// TODO in article:
	// - remove code block 1
	// - replace code block 2 with code below (simulation, setpoint and cache)

	public static void simulation() {
		double k = 160;
		double hitrate = 0.0;
		double sumError = 0.0;

		System.out.println(0 + " " + hitrate);
		for (int time = 1; time < 30; time++) {
			double setpoint = setpoint(time);
			double error = setpoint - hitrate;
			sumError += error;
			double cacheSize = sumError * k;
			hitrate = cache(cacheSize);
			
			System.out.println(time + " " + hitrate);
		}
	}

	public static double setpoint(int time) {
		return 0.6;
	}

	public static double cache(double size) {
		return Math.max(0, Math.min(1, size / 100));
	}
}
