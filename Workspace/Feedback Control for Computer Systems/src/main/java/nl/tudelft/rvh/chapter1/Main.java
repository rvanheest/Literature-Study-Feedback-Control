package nl.tudelft.rvh.chapter1;

import java.util.function.Function;

public class Main {

	public static void main(String[] args) {
//		openLoop(new Buffer(2, 3), 5000);
		closedLoop(new Controller(1.25, 0.01), new Buffer(10, 10), 5000);
	}

	public static void openLoop(Buffer b, int tm) {
		Function<Integer, Double> target = t -> 5.0;
		
		for (int time = 0; time < tm; time++) {
			double units = target.apply(time);
			int queueLength = b.work(units);
			
			System.out.println(time + " " + queueLength);
		}
	}
	
	public static void closedLoop(Controller c, Buffer b, int tm) {
		Function<Integer, Integer> target = (t) -> t < 100 ? 0 : t < 300 ? 50 : 10;
		
		int queueLength = 0;
		for (int time = 0; time < tm; time++) {
			int targetValue = target.apply(time);
			int error = targetValue - queueLength;
			double releasedUnits = c.work(error);
			queueLength = b.work(releasedUnits);
			
			System.out.println(time + "\t" + targetValue + "\t" + error + "\t" + releasedUnits + "\t" + queueLength);
		}
	}
}
