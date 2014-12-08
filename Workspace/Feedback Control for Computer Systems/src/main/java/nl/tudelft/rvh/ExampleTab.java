package nl.tudelft.rvh;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public class ExampleTab extends ChartTab {

	public ExampleTab() {
		super("Example", "Foobar", "foo", "bar");
	}

	@Override
	public String seriesName() {
		return "sample series";
	}

	@Override
	public Observable<Tuple<Number, Number>> runSimulation() {
		return Observable.interval(50L, TimeUnit.MILLISECONDS)
				.skip(1)
				.take(12)
				.zipWith(Arrays.asList(23, 14, 15, 24, 34, 36, 22, 45, 43, 17, 29, 25),
						Tuple<Number, Number>::new);
	}
}
