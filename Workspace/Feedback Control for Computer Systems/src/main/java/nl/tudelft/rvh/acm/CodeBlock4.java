package nl.tudelft.rvh.acm;

public class CodeBlock4 {

	public static void main(String[] args) {
		simulation();
	}

	// TODO in article
	// - replace code sample 4 with code below
	public static void simulation() {
		double k = 0.7;
		SpeedSystem ss = new SpeedSystem();
		double speed = ss.speed;
		
		System.out.println(0 + " " + speed);
		for (int time = 1; time < 60; time++) {
			double setpoint = setpoint(time);
			double error = setpoint - speed;
			double power = error * k;
			speed = ss.interact(power);
			
			System.out.println(time + " " + speed);
		}
	}

	public static int setpoint(int time) {
		if (time <= 20) return 15;
		else if (time <= 40) return 5;
		else return 20;
	}

	static class SpeedSystem {
		private double speed = 10.0;
		public double interact(double power) {
			if (power <= 0) return this.speed = Math.round(0.90 * this.speed * 10) / 10.0;
			else return this.speed = Math.round((this.speed + power) * 10) / 10.0;
		}
	}
}
