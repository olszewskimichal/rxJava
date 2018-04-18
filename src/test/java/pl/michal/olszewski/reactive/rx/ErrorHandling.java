package pl.michal.olszewski.reactive.rx;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.reactivex.Observable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class ErrorHandling {

  private static final Throwable UNKNOWN_ERROR = new IllegalArgumentException("unknown error");
  private static final Throwable UNKNOWN_EXCEPTION = new NullPointerException();

  @Test
  void whenChangeStateOnError_thenErrorThrown() {
    final TestObserver testObserver = new TestObserver();
    final AtomicBoolean state = new AtomicBoolean(false);
    Observable
        .error(UNKNOWN_ERROR)
        .doOnError(throwable -> state.set(true))
        .subscribe(testObserver);

    testObserver.assertError(UNKNOWN_ERROR);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();

    assertTrue(state.get());
  }

  /**
   * In case of an exception being thrown while performing the action, RxJava wraps the exception in a CompositeException:
   */
  @Test
  void whenExceptionOccurOnError_thenCompositeExceptionThrown() {
    final TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_ERROR)
        .doOnError(throwable -> {
          throw new RuntimeException("unexcepted");
        })
        .subscribe(testObserver);

    testObserver.assertError(CompositeException.class);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
  }

  @Test
  public void whenHandleOnErrorResumeItem_thenResumed() {
    final TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_ERROR)
        .onErrorReturnItem("singleValue")
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValueCount(1);
    testObserver.assertValue("singleValue");
  }

  @Test
  public void whenHandleOnErrorReturn_thenResumed() {
    final TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_ERROR)
        .onErrorReturn(Throwable::getMessage)
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValueCount(1);
    testObserver.assertValue("unknown error");
  }

  @Test
  public void whenHandleOnErrorResume_thenResumed() {
    final TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_ERROR)
        .onErrorResumeNext(Observable.just("one", "two"))
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValueCount(2);
    testObserver.assertValues("one", "two");
  }

  @Test
  public void whenHandleOnException_thenResumed() {
    final TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_EXCEPTION)
        .onExceptionResumeNext(Observable.just("exceptionResumed"))
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValueCount(1);
    testObserver.assertValue("exceptionResumed");
  }

  @Test
  public void whenRetryOnError_thenRetryConfirmed() {
    final TestObserver testObserver = new TestObserver();
    final AtomicInteger atomicCounter = new AtomicInteger(0);
    Observable
        .error(() -> {
          atomicCounter.incrementAndGet();
          return UNKNOWN_ERROR;
        })
        .retry(1)
        .subscribe(testObserver);

    testObserver.assertError(UNKNOWN_ERROR);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
    assertEquals(2, atomicCounter.get());
  }

  @Test
  void whenRetryConditionallyOnError_thenRetryConfirmed() {
    final TestObserver testObserver = new TestObserver();
    final AtomicInteger atomicCounter = new AtomicInteger(0);
    Observable
        .error(() -> {
          atomicCounter.incrementAndGet();
          return UNKNOWN_ERROR;
        })
        .retry((integer, throwable) -> integer < 4)
        .subscribe(testObserver);

    testObserver.assertError(UNKNOWN_ERROR);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
    assertEquals(4, atomicCounter.get());
  }

  @Test
  void whenRetryUntilOnError_thenRetryConfirmed() {
    final TestObserver testObserver = new TestObserver();
    final AtomicInteger atomicCounter = new AtomicInteger(0);
    Observable
        .error(UNKNOWN_ERROR)
        .retryUntil(() -> atomicCounter.incrementAndGet() > 3)
        .subscribe(testObserver);
    testObserver.assertError(UNKNOWN_ERROR);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
    assertEquals(4, atomicCounter.get());
  }

  @Test
  public void whenRetryWhenOnError_thenRetryConfirmed() {
    final TestObserver testObserver = new TestObserver();
    final Exception noretryException = new Exception("don't retry");
    Observable
        .error(UNKNOWN_ERROR)
        .retryWhen(throwableObservable -> Observable.error(noretryException))
        .subscribe(testObserver);

    testObserver.assertError(noretryException);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
  }

  @Test
  public void whenRetryWhenOnError_thenCompleted() {
    final TestObserver testObserver = new TestObserver();
    final AtomicInteger atomicCounter = new AtomicInteger(0);
    Observable
        .error(() -> {
          atomicCounter.incrementAndGet();
          return UNKNOWN_ERROR;
        })
        .retryWhen(throwableObservable -> Observable.empty())
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertNoValues();
    assertEquals(0, atomicCounter.get());
  }

  @Test
  public void whenRetryWhenOnError_thenResubscribed() {
    final TestObserver testObserver = new TestObserver();
    final AtomicInteger atomicCounter = new AtomicInteger(0);
    Observable
        .error(() -> {
          atomicCounter.incrementAndGet();
          return UNKNOWN_ERROR;
        })
        .retryWhen(throwableObservable -> Observable.just("anything"))
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertNoValues();
    assertEquals(1, atomicCounter.get());
  }

  @Test
  public void whenRetryWhenForMultipleTimesOnError_thenResumed() {
    final TestScheduler testScheduler = new TestScheduler();
    final TestObserver<Object> testObserver = Observable
        .error(UNKNOWN_ERROR)
        .retryWhen(throwableObservable -> throwableObservable
            .zipWith(Observable.range(1, 3), (throwable, integer) -> integer)
            .flatMap(integer -> Observable.timer(integer, TimeUnit.SECONDS, testScheduler)))
        .test();

    testObserver.assertNotComplete();
    testScheduler.advanceTimeBy(6, TimeUnit.SECONDS);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertNoValues();
  }
}
