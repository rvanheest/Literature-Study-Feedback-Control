package nl.tudelft.rvh.chapter2;

import javafx.scene.chart.XYChart.Data;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import nl.tudelft.rvh.ChartTab;

public class Chapter2CumulativeSimulation extends ChartTab {

	public Chapter2CumulativeSimulation() {
		super("Chapter 2 - Cumulative", "Cumulative simulation",
				"time", "cache size");
	}

	@Override
	public String seriesName() {
		return "cumulative simulation";
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

			Observable.zip(time.map(setPoint), hitrate, (a, b) -> a - b)
					.scan((e, cum) -> e + cum)
					.map(cum -> k * cum)
					.map(cache)
					.subscribe(hitrate::onNext);

			hitrate.take(200).subscribe(subscriber);
			hitrate.onNext(0.0);
		}), Data<Number, Number>::new);
	}
}
