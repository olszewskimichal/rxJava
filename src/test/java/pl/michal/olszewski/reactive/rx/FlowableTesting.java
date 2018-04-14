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

/**
 * BackpressureStrategy.BUFFER: If upstream produces too many events, they are buffered in an unbounded queue. No events are lost, but your whole application most likely is. If you are lucky, OutOfMemoryError will save you. I got stuck on 5+ second long GC pauses.
 *
 * BackpressureStrategy.ERROR: If over-production of events is discovered, MissingBackpressureException will be thrown. It's a sane (and safe) strategy.
 *
 * BackpressureStrategy.LATEST: Similar to DROP, but remembers last dropped event. Just in case request for more data comes in but we just dropped everything - we at least have the last seen value.
 *
 * BackpressureStrategy.MISSING: No safety measures, deal with it. Most likely one of the downstream operators (like observeOn()) will throw MissingBackpressureException.
 *
 * BackpressureStrategy.DROP: drops events that were not requested.
 *
 *
 * The Flowable class that implements the Reactive-Streams Pattern and offers factory methods, intermediate operators and the ability to consume reactive dataflows.
 * Reactive-Streams operates with Publishers which Flowable extends. Many operators therefore accept general Publishers directly and allow direct interoperation with other Reactive-Streams implementations.
 *
 * The Flowable hosts the default buffer size of 128 elements for operators, accessible via bufferSize(), that can be overridden globally via the system parameter rx2.buffer-size. Most operators, however, have overloads that allow setting their internal buffer size explicitly.
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
