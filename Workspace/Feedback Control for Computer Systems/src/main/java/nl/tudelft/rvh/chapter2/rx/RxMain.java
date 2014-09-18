package nl.tudelft.rvh.chapter2.rx;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

public class RxMain {

	public static void main(String[] args) {
		experiment(160).subscribe(System.out::println);
	}

	public static Observable<Double> experiment(float k) {
		Func1<Integer, Double> setPoint = time -> time < 50 ? 0.6
				: time < 100 ? 0.8
						: time < 150 ? 0.1
								: 0.9;
		Func1<Double, Double> cache = size -> size < 0 ? 0
				: size > 100 ? 1
						: size / 100;

		return Observable.create((Subscriber<? super Double> subscriber) -> {
			ReplaySubject<Double> hitrate = ReplaySubject.create();
			hitrate.onNext(0.0);

			Observable.zip(Observable.range(0, 200).map(setPoint), hitrate, (a, b) -> a - b)
					.scan((e, cum) -> e + cum)
					.map(cum -> k * cum)
					.map(cache)
					.subscribe(hitrate::onNext);

			hitrate.skip(1).subscribe(subscriber);
		});
	}
}
