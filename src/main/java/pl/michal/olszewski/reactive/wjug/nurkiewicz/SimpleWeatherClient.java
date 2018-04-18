package pl.michal.olszewski.reactive.wjug.nurkiewicz;

import io.reactivex.Observable;
import java.time.Instant;

public class SimpleWeatherClient {

  public SimpleWeather fetch(final String city) throws InterruptedException {
    System.out.println(Instant.now() + " Loading for " + city);
    Thread.sleep(900);
    return new SimpleWeather();
  }

  public Observable<SimpleWeather> rxFetch(final String city) throws InterruptedException {
    return Observable.fromCallable(() -> fetch(city));
  }
}
