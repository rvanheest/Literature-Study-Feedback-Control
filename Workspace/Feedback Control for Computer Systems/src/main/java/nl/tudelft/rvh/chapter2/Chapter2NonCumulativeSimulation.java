package nl.tudelft.rvh.chapter2;

import javafx.scene.chart.XYChart.Data;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import nl.tudelft.rvh.ChartTab;

public class Chapter2NonCumulativeSimulation extends ChartTab {

	public Chapter2NonCumulativeSimulation() {
		super("Chapter 2 - NonCumulative", "NonCumulative simulation",
				"time", "cache size");
	}

	@Override
	public String seriesName() {
		return "noncumulative simulation";
	}

	@Override
	public Observable<Data<Number, Number>> runSimulation() {
		Observable<Integer> time = Observable.range(0, 200);
		float k = 160;
		Func1<Integer, Double> setPoint = t -> t < 50 ? 0.6
				: t < 100 ? 0.8
						: t < 150 ? 0.1
								: 0.9;
		Func1<Double, Double> cache = size -> size < 0 ? 0
				: size > 100 ? 1
						: size / 100;

		return time.zipWith(Observable.create((Subscriber<? super Double> subscriber) -> {
			PublishSubject<Double> hitrate = PublishSubject.create();

			Observable.zip(Observable.range(0, 200).map(setPoint), hitrate, (a, b) -> a - b)
					.map(e -> k * e)
					.map(cache)
					.subscribe(hitrate::onNext);

			hitrate.take(200).subscribe(subscriber);
			hitrate.onNext(0.0);
		}), Data<Number, Number>::new);
	}
}
