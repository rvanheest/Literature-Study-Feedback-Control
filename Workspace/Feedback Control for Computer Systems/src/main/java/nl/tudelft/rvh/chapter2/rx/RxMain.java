package nl.tudelft.rvh.chapter2.rx;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class RxMain {

	public static void main(String[] args) {
		PublishSubject<Double> exp = experiment(160);
		exp.subscribe(System.out::println);
		exp.onNext(0.0);
	}

	public static PublishSubject<Double> experiment(float k) {
		Func1<Integer, Double> setPoint = time -> time < 50 ? 0.6
				: time < 100 ? 0.8
						: time < 150 ? 0.1 : 0.9;
		Func1<Double, Double> cache = size -> size < 0 ? 0 : size > 100 ? 1 : size / 100;

		PublishSubject<Double> y = PublishSubject.create();

		Observable.zip(Observable.range(0, 200).map(setPoint), y, (a, b) -> a - b)
				.scan((e, cum) -> e + cum)
				.map(cum -> k * cum)
				.map(cache)
				.subscribe(y::onNext);

		return y;
	}
}
