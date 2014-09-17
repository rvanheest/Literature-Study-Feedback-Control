package nl.tudelft.rvh.chapter2;

import java.util.function.Function;

public class Main {

	public static void main(String[] args) {
		experiment(160);
	}

	public static void experiment(float k) {
		Function<Integer, Double> setPoint = time -> time < 50 ? 0.6
				: time < 100 ? 0.8
						: time < 150 ? 0.1 : 0.9;
		Function<Double, Double> cache = size -> size < 0 ? 0 : size > 100 ? 1 : size / 100;

		double y = 0;
		double cum = 0;
		for (int time = 0; time < Integer.MAX_VALUE; time++) {
			double target = setPoint.apply(time);
			double error = target - y;
			cum += error;
			double action = k * cum;
			y = cache.apply(action);

			System.out.println(time + "\t" + target + "\t" + error + "\t" + cum + "\t" + action
					+ "\t" + y);
		}
	}
}
