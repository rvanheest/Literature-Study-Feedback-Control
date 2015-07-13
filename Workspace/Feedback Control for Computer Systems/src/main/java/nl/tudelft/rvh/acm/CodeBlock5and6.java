package nl.tudelft.rvh.acm;

public class CodeBlock5and6 {

	public static void main(String[] args) {
		simulation();
	}

	public static void simulation() {
		double kp = 0.7;
		double ki = 0.003;
		FlowControlSystem flow = new FlowControlSystem();
		PI controller = new PI(kp, ki);
		double speed = flow.speed;

		System.out.println(0 + " " + speed);
		for (int time = 1; time < 60; time++) {
			double setpoint = setpoint(time);
			double error = setpoint - speed;
			double power = controller.interact(error, 1);
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
			if (power <= 0) return this.speed = Math.round(0.90 * this.speed * 10) / 10.0;
			else return this.speed = Math.round((this.speed + power) * 10) / 10.0;
		}
	}

	static class PI {
		private double integral = 0.0;
		private final double kp, ki;
		public PI(double kp, double ki) {
			this.kp = kp;
			this.ki = ki;
		}
		public double interact(double error, double DT) {
			return error * this.kp + (this.integral += (DT * error)) * this.ki;
		}
	}

	static class PID {
		private double integral, previous = 0.0;
		private final double kp, ki, kd;
		public PID(double kp, double ki, double kd) {
			this.kp = kp;
			this.ki = ki;
			this.kd = kd;
		}
		public double interact(double error, double DT) {
			this.integral += (DT * error);
			double deriv = (error - this.previous) / DT;
			this.previous = error;
			return error * this.kp + this.integral * this.ki + deriv * this.kd;
		}
	}
}
