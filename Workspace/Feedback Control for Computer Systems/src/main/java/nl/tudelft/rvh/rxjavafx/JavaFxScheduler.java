package nl.tudelft.rvh.rxjavafx;

import static java.lang.Math.max;

import java.util.concurrent.TimeUnit;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public final class JavaFxScheduler extends Scheduler {

	private static final JavaFxScheduler INSTANCE = new JavaFxScheduler();

    /* package for unit test */JavaFxScheduler() {
    }

    public static JavaFxScheduler getInstance() {
        return INSTANCE;
    }

    @Override
    public Worker createWorker() {
        return new InnerJavaFxScheduler();
    }

    private static class InnerJavaFxScheduler extends Worker {

        private final CompositeSubscription innerSubscription = new CompositeSubscription();

        @Override
        public void unsubscribe() {
            innerSubscription.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {
            return innerSubscription.isUnsubscribed();
        }

        @Override
        public Subscription schedule(final Action0 action, long delayTime, TimeUnit unit) {
            final BooleanSubscription s = BooleanSubscription.create();

            final long delay = unit.toMillis(max(delayTime, 0));
            final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(delay), event -> {
                if (innerSubscription.isUnsubscribed() || s.isUnsubscribed()) {
                    return;
                }
                action.call();
                innerSubscription.remove(s);
            }));

            timeline.setCycleCount(1);
            timeline.play();

            innerSubscription.add(s);

            // wrap for returning so it also removes it from the 'innerSubscription'
            return Subscriptions.create(() -> {
                timeline.stop();
                s.unsubscribe();
                innerSubscription.remove(s);
            });
        }

        @Override
        public Subscription schedule(final Action0 action) {
            final BooleanSubscription s = BooleanSubscription.create();
            Platform.runLater(() -> {
                if (innerSubscription.isUnsubscribed() || s.isUnsubscribed()) {
                    return;
                }
                action.call();
                innerSubscription.remove(s);
            });

            innerSubscription.add(s);
            // wrap for returning so it also removes it from the 'innerSubscription'
            return Subscriptions.create(() -> {
                s.unsubscribe();
                innerSubscription.remove(s);
            });
        }
    }
}
