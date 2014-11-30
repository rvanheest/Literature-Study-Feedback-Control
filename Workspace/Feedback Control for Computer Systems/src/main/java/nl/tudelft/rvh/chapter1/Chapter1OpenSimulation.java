package nl.tudelft.rvh.chapter1;

import javafx.scene.chart.XYChart.Data;
import nl.tudelft.rvh.ChartTab;
import rx.Observable;
import rx.functions.Func1;

public class Chapter1OpenSimulation extends ChartTab {

	public Chapter1OpenSimulation() {
		super("Chapter 1 - open", "Open simulation of random buffer simulation",
				"time", "queue size");
	}

	@Override
	public String seriesName() {
		return "closed simulation";
	}

	@Override
	public Observable<Data<Number, Number>> runSimulation() {
		Observable<Integer> time = Observable.range(0, 5000);
		Buffer b = new Buffer(10, 10);
		Func1<Integer, Double> target = t -> 5.0;

		Observable<Integer> map = time.map(target).map(b::work);
		return time.zipWith(map, Data<Number, Number>::new);
	}
}
