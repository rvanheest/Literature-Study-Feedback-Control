package nl.tudelft.rvh.chapter1;

import javafx.scene.chart.XYChart.Data;
import nl.tudelft.rvh.ChartTab;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.ReplaySubject;

public class Chapter1ClosedSimulation extends ChartTab {

	public Chapter1ClosedSimulation() {
		super("Chapter 1 - closed", "Closed simulation of random buffer simulation",
				"time", "queue size");
	}

	@Override
	public String seriesName() {
		return "closed simulation";
	}

	@Override
	public Observable<Data<Number, Number>> runSimulation() {
		Observable<Integer> time = Observable.range(0, 5000);
		Func1<Integer, Integer> target = t -> t < 100 ? 0
				: t < 300 ? 50
						: 10;
		Func2<Integer, Integer, Double> control = (e, c) -> 1.25 * e + 0.01 * c;
		Buffer b = new Buffer(10, 10);

		return Observable.create(subscriber -> {
			ReplaySubject<Integer> queueLength = ReplaySubject.create();
			queueLength.onNext(0);

			Observable<Integer> error = Observable.zip(time.map(target),
					queueLength, (tar, q) -> tar - q);
			Observable.zip(error, error.scan((e, cum) -> e + cum), control)
					.map(b::work)
					.subscribe(queueLength::onNext);

			Observable<Data<Number, Number>> data = time.zipWith(queueLength.skip(1),
					Data<Number, Number>::new);
			data.subscribe(subscriber);
		});
	}
}
