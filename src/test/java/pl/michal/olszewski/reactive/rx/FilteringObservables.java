package pl.michal.olszewski.reactive.rx;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;

public class FilteringObservables {

  @Test
  void filter() {
    final Observable<Integer> range = Observable.range(1, 10);
    final TestObserver<Integer> test = range.filter(i -> i % 2 != 0)
        .test();

    test.assertValues(1, 3, 5, 7, 9);
  }

  @Test
  void take() {
    final Observable<Integer> range = Observable.range(1, 10);
    final TestObserver<Integer> testObserver = range
        .take(3)
        .test();
    testObserver.assertValues(1, 2, 3);
  }

  @Test
  void takeWhile() {
    final TestObserver<Integer> testObserver = Observable.range(1, 10)
        .takeWhile(i -> i < 4)
        .test();
    testObserver.assertValues(1, 2, 3);
  }

  @Test
  void takeLast() {
    final TestObserver<Integer> testObserver = Observable.just(1, 2, 3, 4, 5, 7, 6)
        .takeLast(1)
        .test();
    testObserver.assertValue(6);
  }

  @Test
  void first() {
    final TestObserver<Integer> test = Observable.range(1, 10)
        .first(0)
        .test();
    test.assertValue(1);
  }

  @Test
  void last() {
    final TestObserver<Integer> test = Observable.range(1, 10)
        .last(0)
        .test();
    test.assertValue(10);
  }

  @Test
  void elementAt() {
    final TestObserver<Integer> test = Observable.range(1, 10)
        .elementAt(4)
        .test();
    test.assertValue(5);
  }

  @Test
  void elementAtOrDefault() {
    final TestObserver<Integer> test = Observable.range(1, 10)
        .elementAt(10, -1)
        .test();
    test.assertValue(-1);
  }

  @Test
  void ofType() {
    final TestObserver<String> test = Observable.just(1, "two", 3, "five", 5.0)
        .ofType(String.class)
        .test();
    test.assertValues("two", "five");
  }

  @Test
  void skip() {
    final TestObserver<Integer> test = Observable.range(1, 10)
        .skip(4)
        .test();
    test.assertValues(5, 6, 7, 8, 9, 10);
  }

  @Test
  void skipWhile() {
    final TestObserver<Integer> test = Observable.just(1, 2, 3, 4, 5, 4, 3, 2, 1)
        .skipWhile(i -> i < 4)
        .test();
    test.assertValues(4, 5, 4, 3, 2, 1);
  }

  @Test
  void skipLast() {
    final TestObserver<Integer> testObserver = Observable.range(1, 10)
        .skipLast(5)
        .test();
    testObserver.assertValues(1, 2, 3, 4, 5);
  }

  @Test
  void distinct() {
    final TestObserver<Integer> testObserver = Observable.just(1, 1, 2, 2, 1, 3, 3, 1)
        .distinct()
        .test();
    testObserver.assertValues(1, 2, 3);
  }

  @Test
  void distinctUntilChanged() {
    final TestObserver<Integer> test = Observable
        .just(1, 1, 2, 2, 1, 3, 3, 1)
        .distinctUntilChanged()
        .test();
    test.assertValues(1, 2, 1, 3, 1);
  }

  @Test
  void ignoreElements() {
    final TestObserver<Void> test = Observable.range(1, 10)
        .ignoreElements()
        .test();
    test.assertNoValues();
  }

  @Test
  void timeFiltering() {
    final TestObserver<Integer> test = timedObservable
        .test();
    test.assertNoValues();
    testScheduler.advanceTimeBy(5, SECONDS);
    test.assertValues(1, 2, 3, 4, 5, 6);
  }

  @Test
  void sample() {
    final TestObserver<Integer> test = timedObservable
        .sample(2500L, TimeUnit.MILLISECONDS, testScheduler)
        .test();
    testScheduler.advanceTimeBy(7, SECONDS);
    test.assertValues(3, 5);
  }

  @Test
  void timeout() {
    final TestObserver<Integer> test = timedObservable
        .timeout(500L, TimeUnit.MILLISECONDS, testScheduler)
        .test();
    testScheduler.advanceTimeBy(7, SECONDS);
    test.assertError(TimeoutException.class);
    test.assertValues(1);
  }

  final TestScheduler testScheduler = new TestScheduler();
  Observable<Integer> timedObservable = Observable
      .just(1, 2, 3, 4, 5, 6)
      .zipWith(Observable.interval(
          0, 1, SECONDS, testScheduler), (item, time) -> item);
  Observable<Integer> delayedObservable = Observable.just(1)
      .delay(3, SECONDS, testScheduler);

  @Test
  void takeUntil() {
    final TestObserver<Integer> test = timedObservable
        .skipUntil(delayedObservable)
        .test();
    testScheduler.advanceTimeBy(7, SECONDS);

    test.assertValues(4, 5, 6);
  }

  @Test
  void skipUntil() {
    final TestObserver<Integer> test = timedObservable
        .takeUntil(delayedObservable)
        .test();
    testScheduler.advanceTimeBy(7, SECONDS);
    test.assertValues(1, 2, 3);
  }

}
