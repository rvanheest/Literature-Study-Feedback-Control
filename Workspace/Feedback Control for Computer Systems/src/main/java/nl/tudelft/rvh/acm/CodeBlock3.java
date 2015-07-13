package nl.tudelft.rvh.acm;

public class CodeBlock3 {

	public static void main(String[] args) {
		simulation();
	}

	public static void simulation() {
		OnOffFlowControlSystem flow = new OnOffFlowControlSystem();
		int speed = flow.speed;

		System.out.println(0 + " " + speed);
		for (int time = 1; time < 60; time++) {
			double setpoint = setpoint(time);
			double error = setpoint - speed;
			boolean setting = error > 0;
			speed = flow.interact(setting);

			System.out.println(time + " " + speed);
		}
	}

	public static int setpoint(int time) {
		if (time <= 20) return 15;
		else if (time <= 40) return 5;
		else return 20;
	}

	static class OnOffFlowControlSystem {
		private int speed = 10;
		public int interact(boolean setting) {
			if (setting) return this.speed += 1;
			else return this.speed -= 1;
		}
	}
}
