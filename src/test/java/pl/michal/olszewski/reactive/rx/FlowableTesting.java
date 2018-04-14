package pl.michal.olszewski.reactive.rx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/**
 * Observable sources don’t support backpressure.
 * Because of that, we should use it for sources that we merely consume and can’t influence.
 */
public class FlowableTesting {

  @Test
  void creating() {
    final Flowable<Integer> just = Flowable.just(1, 2, 3, 4);
  }

  @Test
  void flowableFromObservable() {
    final Flowable<Integer> flowable = Observable.just(1, 2, 3)
        .toFlowable(BackpressureStrategy.BUFFER);
  }

  @Test
  void flowableOnSubscribe() {
    final FlowableOnSubscribe<Integer> flowableOnSubscribe
        = flowable -> flowable.onNext(1);
    final Flowable<Integer> integerFlowable = Flowable
        .create(flowableOnSubscribe, BackpressureStrategy.BUFFER);
  }

  /**
   * The BackpressureStrategy is an enumeration, which defines the backpressure behavior that we’ll apply to our Flowable.
   *
   * If we use the BackpressureStrategy.BUFFER, the source will buffer all the events until the subscriber can consume them:
   */
  @Test
  void thenAllValuesAreBufferedAndReceived() {
    final List testList = IntStream.range(0, 100000)
        .boxed()
        .collect(Collectors.toList());

    final Observable observable = Observable.fromIterable(testList);
    final TestSubscriber<Integer> testSubscriber = observable
        .toFlowable(BackpressureStrategy.BUFFER)
        .observeOn(Schedulers.computation())
        .test();

    testSubscriber.awaitTerminalEvent();

    final List<Integer> receivedInts = testSubscriber.getEvents()
        .get(0)
        .stream()
        .mapToInt(object -> (int) object)
        .boxed()
        .collect(Collectors.toList());

    assertEquals(testList, receivedInts);
  }

  /**
   * We can use the BackpressureStrategy.DROP to discard the events that cannot be consumed instead of buffering them.
   */
  @Test
  void whenDropStrategyUsed_thenOnBackpressureDropped() {
    final List testList = IntStream.range(0, 100000)
        .boxed()
        .collect(Collectors.toList());

    final Observable observable = Observable.fromIterable(testList);
    final TestSubscriber<Integer> testSubscriber = observable
        .toFlowable(BackpressureStrategy.DROP)
        .observeOn(Schedulers.computation())
        .test();
    testSubscriber.awaitTerminalEvent();
    final List<Integer> receivedInts = testSubscriber.getEvents()
        .get(0)
        .stream()
        .mapToInt(object -> (int) object)
        .boxed()
        .collect(Collectors.toList());

    assertThat(receivedInts.size() < testList.size()).isTrue();
    assertThat(!receivedInts.contains(100000)).isTrue();
  }

  @Test
  void whenLatestStrategyUsed_thenTheLastElementReceived() {
    final List testList = IntStream.rangeClosed(0, 100000)
        .boxed()
        .collect(Collectors.toList());

    final Observable observable = Observable.fromIterable(testList);
    final TestSubscriber<Integer> testSubscriber = observable
        .toFlowable(BackpressureStrategy.LATEST)
        .observeOn(Schedulers.computation())
        .test();

    testSubscriber.awaitTerminalEvent();
    final List<Integer> receivedInts = testSubscriber.getEvents()
        .get(0)
        .stream()
        .mapToInt(object -> (int) object)
        .boxed()
        .collect(Collectors.toList());

    assertThat(receivedInts.size() < testList.size()).isTrue();
    assertThat(receivedInts.contains(100000)).isTrue();
    System.out.println(receivedInts);
  }

  /**
   * When we’re using the BackpressureStrategy.ERROR, we’re simply saying that we don’t expect backpressure to occur. Consequently, a MissingBackpressureException should be thrown if the consumer can’t keep up with the source:
   */
  @Test
  void whenErrorStrategyUsed_thenExceptionIsThrown() {
    final Observable observable = Observable.range(1, 100000);
    final TestSubscriber subscriber = observable
        .toFlowable(BackpressureStrategy.ERROR)
        .observeOn(Schedulers.computation())
        .test();

    subscriber.awaitTerminalEvent();
    subscriber.assertError(MissingBackpressureException.class);
  }

  /**
   * If we use the BackpressureStrategy.MISSING, the source will push elements without discarding or buffering.
   *
   * In our tests, we’re excepting MissingbackpressureException for both ERROR and MISSING strategies. As both of them will throw such exception when the source’s internal buffer is overflown.
   *
   * However, it’s worth to note that both of them have a different purpose.
   *
   * We should use the former one when we don’t expect backpressure at all, and we want the source to throw an exception in case if it occurs.
   *
   * The latter one could be used if we don’t want to specify a default behavior on the creation of the Flowable. And we’re going to use backpressure operators to define it later on.
   */
  @Test
  void whenMissingStrategyUsed_thenException() {
    final Observable observable = Observable.range(1, 100000);
    final TestSubscriber subscriber = observable
        .toFlowable(BackpressureStrategy.MISSING)
        .observeOn(Schedulers.computation())
        .test();
    subscriber.awaitTerminalEvent();
    subscriber.assertError(MissingBackpressureException.class);
  }

}
