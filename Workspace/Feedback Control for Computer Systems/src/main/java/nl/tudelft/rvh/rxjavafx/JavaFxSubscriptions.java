package nl.tudelft.rvh.rxjavafx;

import javafx.application.Platform;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public enum JavaFxSubscriptions {
	; // no instance

	/**
	 * Create an Subscription that always runs <code>unsubscribe</code> in the event dispatch
	 * thread.
	 *
	 * @param unsubscribe the action to be performed in the ui thread at un-subscription
	 * @return an Subscription that always runs <code>unsubscribe</code> in the event dispatch
	 *         thread.
	 */
	public static Subscription unsubscribeInEventDispatchThread(Action0 unsubscribe) {
		return Subscriptions.create(() -> {
			if (Platform.isFxApplicationThread()) {
				unsubscribe.call();
			}
			else {
				final Worker inner = JavaFxScheduler.getInstance().createWorker();
				inner.schedule(() -> {
						unsubscribe.call();
						inner.unsubscribe();
				});
			}
		});
	}
}
