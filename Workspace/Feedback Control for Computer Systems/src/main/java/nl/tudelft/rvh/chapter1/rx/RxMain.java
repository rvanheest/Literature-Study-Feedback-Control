package nl.tudelft.rvh.chapter1.rx;

import nl.tudelft.rvh.chapter1.Buffer;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;

public class RxMain {

	public static void main(String[] args) {
		openLoop(new Buffer(10, 10), 5000).subscribe(System.out::println);
		
//		PublishSubject<Integer> closed = closedLoop(1.25, 0.01, new Buffer(10, 10), 5000);
//		closed.subscribe(System.out::println);
//		closed.onNext(0);
	}

	public static Observable<Integer> openLoop(Buffer b, int tm) {
		Func1<Integer, Double> target = t -> 5.0;

		return Observable.range(0, tm).map(target).map(b::work);
	}

	public static PublishSubject<Integer> closedLoop(double kp, double ki, Buffer b, int tm) {
		Func1<Integer, Integer> target = t -> t < 100 ? 0 : t < 300 ? 50 : 10;
		Func2<Integer, Integer, Double> control = (e, c) -> kp * e + ki * c;
		
		PublishSubject<Integer> queueLength = PublishSubject.create();
		
		Observable<Integer> error = Observable.zip(Observable.range(0, tm).map(target),
				queueLength, (tar, q) -> tar - q);
		Observable.zip(error, error.scan((e, cum) -> e + cum), control)
				.map(b::work)
				.subscribe(queueLength::onNext);
		
		return queueLength;
	}
}
