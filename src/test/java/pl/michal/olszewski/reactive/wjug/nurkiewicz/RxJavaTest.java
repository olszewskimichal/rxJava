package pl.michal.olszewski.reactive.wjug.nurkiewicz;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;

public class RxJavaTest {

  @Test
  void wjug1() throws ExecutionException, InterruptedException {
    final CompletableFuture<String> future = CompletableFuture.completedFuture("42");
    final String s42 = future.get();
    final CompletableFuture<Integer> futInt = future.thenApply(s -> s.length() + 1);
    final CompletableFuture<Double> doubleCompletableFuture = futInt.thenApply(x -> x * 2.0);

    final CompletableFuture<Double> doubleCompletableFuture1 = future.thenApply(s -> s.length() + 1)
        .thenApply(x -> x * 2.0);
  }

  @Test
  void wjug2() {
    final Observable<String> obs = Observable.just("42", "43", "44");
    obs.subscribe(this::print);
  }

  SimpleWeatherClient simpleWeatherClient = new SimpleWeatherClient();

  @Test
  void wjug3() throws InterruptedException {
    print(simpleWeatherClient.fetch("Warsaw"));
  }

  @Test
  void wjug4() throws InterruptedException {
    final Observable<SimpleWeather> weatherObservable = simpleWeatherClient.rxFetch("Warsaw");
    weatherObservable.subscribe(this::print);
  }

  @Test
  void wjug5() throws InterruptedException {
    final Observable<SimpleWeather> weatherObservable = simpleWeatherClient.rxFetch("Warsaw");
    weatherObservable.timeout(1, TimeUnit.SECONDS).subscribe(this::print);
  }

  @Test
  void wjug6() throws InterruptedException {
    final Observable<SimpleWeather> weatherObservable = simpleWeatherClient.rxFetch("Warsaw");
    weatherObservable.timeout(790, MILLISECONDS).subscribe(this::print);
  }

  @Test
  void wjug7() throws InterruptedException {
    final Observable<SimpleWeather> warsaw = simpleWeatherClient.rxFetch("Warsaw");
    final Observable<SimpleWeather> radom = simpleWeatherClient.rxFetch("Radom");
    final Observable<SimpleWeather> weatherObservable = warsaw.mergeWith(radom);
    //zwroci 2 obiekty

  }

  @Test
  void wjug8() throws InterruptedException {
    final Observable<SimpleWeather> warsaw = simpleWeatherClient.rxFetch("Warsaw");
    final Observable<SimpleWeather> radom = simpleWeatherClient.rxFetch("Radom");

    warsaw.subscribe(this::print);
    //900ms pozniej ... -> domyslnie rx nie jest asynchroniczny
    radom.subscribe(this::print);
  }

  private final PersonDao personDao = new PersonDao();

  @Test
  void wjug9() throws InterruptedException {
    final Observable<SimpleWeather> warsaw = simpleWeatherClient.rxFetch("Warsaw")
        .subscribeOn(Schedulers.io());  // pozwala to wykonac w innym wątku niz kliencki
    // nie uzywaj io() -
    final Observable<Person> personObservable = personDao.findByIdRx(42)
        .subscribeOn(Schedulers.io());

    final Observable<String> stringObservable = warsaw.zipWith(personObservable, (SimpleWeather w, Person p) -> w + " : " + p);
    stringObservable.subscribe(this::print);
    TimeUnit.SECONDS.sleep(2);
  }

  @Test
  void wjug10() {
    final Observable<String> strings = Observable.just("A", "B", "C");
    final Observable<Integer> range = Observable.range(1, 3).map(x -> x * 10);
    final Observable<String> observable = strings.zipWith(range, (s, n) -> s + n);
    observable.subscribe(this::print);
  }

  @Test
  void wjug11() {
    final Observable<String> strings = Observable.just("A", "B", "C").repeat(2);
    final Observable<Integer> range = Observable.range(1, 10).map(x -> x * 10);
    final Observable<String> observable = strings.zipWith(range, (s, n) -> s + n);
    observable.subscribe(this::print);
  }

  @Test
  void wjug12() {
    Schedulers.io();
    Schedulers.computation();
    Schedulers.from(Executors.newFixedThreadPool(10));
  }

  @Test
  void wjug13() throws InterruptedException {
    final CacheServer cacheServer = new CacheServer();
    final CacheServer cacheServer1 = new CacheServer();

    final Observable<String> byRx = cacheServer.findByRx(42);
    final Observable<String> byRx1 = cacheServer1.findByRx(42);

    byRx.mergeWith(byRx1).first("defaultValue").subscribe(this::print);
    TimeUnit.SECONDS.sleep(2);
  }

  @Test
  void wjug14() throws InterruptedException {
    Observable.interval(1, TimeUnit.SECONDS).subscribe(this::print);
    TimeUnit.SECONDS.sleep(5);
  }


  @Test
  void wjug15() throws IOException {
    childrenOf(new File("C:/")).
        subscribe(this::print);

  }

  @Test
  void wjug16() {
    Observable.interval(1, TimeUnit.SECONDS)
        .map(v -> childrenOfList(new File("C:/")))
        .blockingSubscribe(this::print);
  }

  @Test
  void wjug17() {
    Observable.interval(1, TimeUnit.SECONDS)
        .flatMap(x -> childrenOf(new File("C:/")))
        .distinct()
        .blockingSubscribe(this::print);
  }

  @Test
  void wjug18() {
    verySlowSoapService()
        .timeout(1, TimeUnit.SECONDS)
        .doOnError(ex -> System.out.println("Oops " + ex))
        .retry(4)
        .onErrorReturn(x -> BigDecimal.ONE.negate())
        .blockingSubscribe(this::print);
  }

  @Test
  void wjug19() {
    //Schedulers.test();
    final TestScheduler s = new TestScheduler();

    final TestObserver<BigDecimal> observable = verySlowSoapService()
        .timeout(1, TimeUnit.SECONDS, s)
        .doOnError(ex -> System.out.println("Oops " + ex))
        .retry(4)
        .onErrorReturn(x -> BigDecimal.ONE.negate()).test();

    observable.assertNoErrors();
    observable.assertNoValues();
    s.advanceTimeBy(4999, MILLISECONDS);
    observable.assertNoErrors();
    observable.assertNoValues();
    s.advanceTimeBy(1, MILLISECONDS);

    observable.assertValue(BigDecimal.ONE.negate());
  }

  @Test
  void wjug20() {
    final Observable<String> xlteam = Observable.just("shekhar", "sameer", "aditya", "ankur");
    xlteam.subscribe(
        name -> System.out.println("first .. I met " + name),
        error -> System.out.println("error " + error),
        () -> System.out.println("********first completed*******"));

    xlteam.subscribe(
        name -> System.out.println("second .. I met " + name),
        error -> System.out.println("error " + error),
        () -> System.out.println("********second completed*******"));

    final Observable<String> xlteamWithS = xlteam
        .map(String::toUpperCase)
        .filter(name -> name.startsWith("S"));
    xlteamWithS.subscribe(
        name -> System.out.println("I met XL team member with name starting with S. He is " + name),
        error -> System.out.println("error " + error),
        () -> System.out.println("********third completed*******"));

    xlteam.mergeWith(xlteamWithS)
        .subscribe(
            name -> System.out.println("I met all... " + name));
  }

  @Test
  void wjug21() {
    final Observable<Long> longObservable = Observable.<Long>create(subscriber -> {
      LongStream longStream = LongStream.iterate(1, val -> val + 1);
      longStream.forEach(val -> subscriber.onNext(val));
    }).subscribeOn(Schedulers.newThread());

    longObservable.take(10).subscribe(
        naturalNumber -> System.out.println(String.format("first {%s} -- {%s}", naturalNumber, Thread.currentThread().getName())));
    longObservable.skip(10).take(10).subscribe(naturalNumber -> System.out.println(String.format("second {%s} -- {%s}", naturalNumber, Thread.currentThread().getName())));
    longObservable.skip(20).take(10).subscribe(naturalNumber -> System.out.println(String.format("third {%s} -- {%s}", naturalNumber, Thread.currentThread().getName())));

  }
  
  private Observable<BigDecimal> verySlowSoapService() {
    return Observable.timer(1, TimeUnit.MINUTES)
        .map(v -> BigDecimal.ZERO);
  }

  List<String> childrenOfList(final File file) {
    return childrenOf(file).toList().blockingGet();
  }

  Observable<String> childrenOf(final File dir) {
    final File[] files = dir.listFiles();
    return Observable.fromArray(files)
        .map(File::getName);
  }

  void print(final Object obj) {
    System.out.println(Instant.now() + " " + obj);
  }

}
