package nl.tudelft.rvh.rxjavafx;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import rx.Observable;
import rx.Subscriber;

public enum Observables {
	; // no class instances

	public static <T extends Event> Observable<T> fromNodeEvents(Node source, EventType<T> eventType) {
		return Observable.create((Subscriber<? super T> subscriber) -> {
			EventHandler<T> handler = subscriber::onNext;

			source.addEventHandler(eventType, handler);

			subscriber.add(JavaFxSubscriptions.unsubscribeInEventDispatchThread(() ->
					source.removeEventHandler(eventType, handler)));
		}).subscribeOn(JavaFxScheduler.getInstance());
	}

	public static <T> Observable<T> fromProperty(ObservableValue<T> fxObservable) {
		return Observable.create((Subscriber<? super T> subscriber) -> {
			subscriber.onNext(fxObservable.getValue());

			ChangeListener<T> listener = (obs, prev, current) -> subscriber.onNext(current);

			fxObservable.addListener(listener);

			subscriber.add(JavaFxSubscriptions.unsubscribeInEventDispatchThread(() ->
					fxObservable.removeListener(listener)));
		});
	}
	
	public static <T> Observable<List<? extends T>> fromObservableList(ObservableList<T> fxObservable) {
		return Observable.create((Subscriber<? super List<? extends T>> subscriber) -> {
			subscriber.onNext(fxObservable);
			
			ListChangeListener<T> listener = change -> subscriber.onNext(change.getList());

			fxObservable.addListener(listener);

			subscriber.add(JavaFxSubscriptions.unsubscribeInEventDispatchThread(() ->
					fxObservable.removeListener(listener)));
		});
	}
}
