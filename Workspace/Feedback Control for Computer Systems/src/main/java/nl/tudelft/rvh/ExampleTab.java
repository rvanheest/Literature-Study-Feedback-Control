package nl.tudelft.rvh;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.scene.chart.XYChart.Data;
import nl.tudelft.rvh.rxjavafx.JavaFxScheduler;
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
	public Observable<Data<Number, Number>> runSimulation() {
		List<Data<Number, Number>> list1 = Arrays.asList(new Data<>(1, 23), new Data<>(2, 14),
				new Data<>(3, 15), new Data<>(4, 24), new Data<>(5, 34), new Data<>(6, 36),
				new Data<>(7, 22), new Data<>(8, 45), new Data<>(9, 43), new Data<>(10, 17),
				new Data<>(11, 29), new Data<>(12, 25));

		return Observable.interval(1L, TimeUnit.SECONDS, JavaFxScheduler.getInstance())
				.zipWith(Observable.from(list1), (l, d) -> d);
	}
}
