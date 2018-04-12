package pl.michal.olszewski.reactive.wjug.nurkiewicz;

import io.reactivex.Observable;
import java.time.Instant;

public class WeatherClient {

  public Weather fetch(String city) throws InterruptedException {
    System.out.println(Instant.now() + " Loading for " + city);
    Thread.sleep(900);
    return new Weather();
  }

  public Observable<Weather> rxFetch(String city) throws InterruptedException {
    return Observable.fromCallable(() -> fetch(city));
  }
}
