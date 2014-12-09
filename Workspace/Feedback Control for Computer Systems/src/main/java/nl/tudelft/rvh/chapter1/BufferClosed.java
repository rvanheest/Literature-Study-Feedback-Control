package nl.tudelft.rvh.chapter1;

import java.util.concurrent.TimeUnit;

import nl.tudelft.rvh.ChartTab;
import nl.tudelft.rvh.Tuple;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;

public class BufferClosed extends ChartTab {

	public BufferClosed() {
		super("Chapter 1 - closed", "Closed simulation of random buffer simulation",
				"time", "queue size");
	}

	@Override
	public String seriesName() {
		return "closed simulation";
	}

	@Override
	public Observable<Tuple<Number, Number>> runSimulation() {
		Observable<Long> time = Observable.interval(1L, TimeUnit.MILLISECONDS)
				.take(5000);
		Buffer b = new Buffer(10, 10);
		Func1<Long, Integer> target = t -> t < 100 ? 0
				: t < 300 ? 50
						: 10;
		Func2<Integer, Integer, Double> control = (e, c) -> 1.25 * e + 0.01 * c;

		Observable<Integer> feedbackLoop = Observable.create(subscriber -> {
			PublishSubject<Integer> queueLength = PublishSubject.create();

			Observable<Integer> error = time.map(target).zipWith(queueLength, Math::subtractExact);

			error.scan(new Tuple<>(0, 0), (c, e) -> new Tuple<>(e, e + c.getY()))
					.map(t -> control.call(t.getX(), t.getY()))
					.map(b::work)
					.subscribe(queueLength::onNext);

			queueLength.subscribe(subscriber);
			queueLength.onNext(0);
		});
		return time.zipWith(feedbackLoop.onBackpressureBuffer().skip(1),
				Tuple<Number, Number>::new);
	}
}
