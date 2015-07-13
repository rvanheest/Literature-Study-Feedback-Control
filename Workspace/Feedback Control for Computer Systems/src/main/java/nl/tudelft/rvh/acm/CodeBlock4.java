package nl.tudelft.rvh.acm;

public class CodeBlock4 {

	public static void main(String[] args) {
		simulation();
	}

	public static void simulation() {
		double k = 0.7;
		FlowControlSystem flow = new FlowControlSystem();
		double speed = flow.speed;
		
		System.out.println(0 + " " + speed);
		for (int time = 1; time < 60; time++) {
			double setpoint = setpoint(time);
			double error = setpoint - speed;
			double power = error * k;
			speed = flow.interact(power);
			
			System.out.println(time + " " + speed);
		}
	}

	public static int setpoint(int time) {
		if (time <= 20) return 15;
		else if (time <= 40) return 5;
		else return 20;
	}

	static class FlowControlSystem {
		private double speed = 10.0;
		public double interact(double power) {
			if (power <= 0) this.speed = Math.round(0.90 * this.speed * 10) / 10.0;
			else this.speed = Math.round((this.speed + power) * 10) / 10.0;
			return this.speed;
		}
	}
}
