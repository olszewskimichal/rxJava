package pl.michal.olszewski.reactive.rx;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CombiningObservables {

  @Test
  void merge() {
    final TestObserver<String> test = Observable.merge(
        Observable.fromArray("Hello", "World"),
        Observable.fromArray("I love", "RxJava")
    ).test();

    test.assertValues("Hello", "World", "I love", "RxJava");
  }

  @Test
  void mergeDelayError() {
    final TestObserver<String> test = Observable.mergeDelayError(
        Observable.fromArray("hello", "world"),
        Observable.error(new RuntimeException("Some exception")),
        Observable.fromArray("rxjava")
    ).test();
    test.assertValues("hello", "world", "rxjava");
    test.assertError(RuntimeException.class);
  }

  @Test
  void zip() {
    final List<String> zippedStrings = new ArrayList<>();

    Observable.zip(
        Observable.fromArray("Simple", "Moderate", "Complex"),
        Observable.fromArray("Solutions", "Success", "Hierarchy"),
        (str1, str2) -> str1 + " " + str2).subscribe(zippedStrings::add);

    assertThat(zippedStrings).isNotEmpty();
    assertThat(zippedStrings.size()).isEqualTo(3);
    assertThat(zippedStrings).contains("Simple Solutions", "Moderate Success", "Complex Hierarchy");
  }

  @Test
  void zipInterval() {
    final TestScheduler testScheduler = new TestScheduler();
    final Observable<String> data = Observable.just("one", "two", "three", "four", "five");
    final Observable<Long> interval = Observable.interval(1L, SECONDS, testScheduler);

    final TestObserver<String> test = Observable
        .zip(data, interval, (str, tick) -> String.format("[%d]=%s", tick, str))
        .test();

    testScheduler.advanceTimeBy(5, SECONDS);

    test.assertComplete();
    test.assertValueCount(5);
  }

}
