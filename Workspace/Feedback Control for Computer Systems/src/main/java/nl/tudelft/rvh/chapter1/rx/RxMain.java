package nl.tudelft.rvh.chapter1.rx;

import nl.tudelft.rvh.chapter1.Buffer;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.ReplaySubject;

public class RxMain {

	public static void main(String[] args) {
		// openLoop(new Buffer(10, 10), 5000).subscribe(System.out::println);
		closedLoop(1.25, 0.01, new Buffer(10, 10), 5000).subscribe(System.out::println);
	}

	public static Observable<Integer> openLoop(Buffer b, int tm) {
		Func1<Integer, Double> target = t -> 5.0;

		return Observable.range(0, tm).map(target).map(b::work);
	}

	public static Observable<Integer> closedLoop(double kp, double ki, Buffer b, int tm) {
		Func1<Integer, Integer> target = t -> t < 100 ? 0
				: t < 300 ? 50
						: 10;
		Func2<Integer, Integer, Double> control = (e, c) -> kp * e + ki * c;

		return Observable.create((Subscriber<? super Integer> subscriber) -> {
			ReplaySubject<Integer> queueLength = ReplaySubject.create();
			queueLength.onNext(0);

			Observable<Integer> error = Observable.zip(Observable.range(0, tm).map(target),
					queueLength, (tar, q) -> tar - q);
			Observable.zip(error, error.scan((e, cum) -> e + cum), control)
					.map(b::work)
					.subscribe(queueLength::onNext);

			queueLength.skip(1).subscribe(subscriber);
		});
	}
}
