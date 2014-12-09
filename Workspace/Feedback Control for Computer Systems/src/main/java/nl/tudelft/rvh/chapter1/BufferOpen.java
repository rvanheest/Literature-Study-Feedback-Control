package nl.tudelft.rvh.chapter1;

import java.util.concurrent.TimeUnit;

import nl.tudelft.rvh.ChartTab;
import nl.tudelft.rvh.Tuple;
import rx.Observable;
import rx.functions.Func1;

public class BufferOpen extends ChartTab {

	public BufferOpen() {
		super("Chapter 1 - open", "Open simulation of random buffer simulation",
				"time", "queue size");
	}

	@Override
	public String seriesName() {
		return "closed simulation";
	}

	@Override
	public Observable<Tuple<Number, Number>> runSimulation() {
		Observable<Long> time = Observable.interval(1L, TimeUnit.MILLISECONDS).take(5000);
		Buffer b = new Buffer(10, 10);
		Func1<Long, Double> target = t -> 5.0;

		Observable<Integer> map = time.map(target).map(b::work);
		return time.zipWith(map, Tuple<Number, Number>::new);
	}
}
