package pl.michal.olszewski.reactive.client;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import pl.michal.olszewski.reactive.client.weather.WeatherData;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherClient {

  public static final String APP_ID = "90faa1669716319e787ca1ab5da48cbc";

  static public void main(final String[] args) throws InterruptedException {
    final Retrofit retrofit = new Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://api.openweathermap.org/data/2.5/")
        .build();

    final WeatherRxApi weatherService = retrofit.create(WeatherRxApi.class);

    Observable.just("Bydgoszcz", "Toruń", "Gdańsk")
        .zipWith(Observable.interval(1, TimeUnit.SECONDS), (e1, e2) -> e1)
        .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(4)))
        .flatMap(v -> weatherService.getWeatherForCity(v, APP_ID))
        .subscribe(v -> System.out.println(Thread.currentThread() + " " + v));
    Observable.just("Sosnowiec", "Katowice", "Tychy")
        .zipWith(Observable.interval(1, TimeUnit.SECONDS), (e1, e2) -> e1)
        .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(4)))
        .flatMap(v -> weatherService.getWeatherForCity(v, APP_ID))
        .subscribe(v -> System.out.println(Thread.currentThread() + " " + v));
    Observable.just("Białystok", "Warszawa", "Gdynia")
        .zipWith(Observable.interval(1, TimeUnit.SECONDS), (e1, e2) -> e1)
        .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(4)))
        .flatMap(v -> weatherService.getWeatherForCity(v, APP_ID))
        .subscribe(v -> System.out.println(Thread.currentThread() + " " + v));

    final Observable<WeatherData> responseOneObservable = weatherService.getWeatherForCity("Kraków", APP_ID)
        .zipWith(Observable.interval(1, TimeUnit.SECONDS), (e1, e2) -> e1)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation());

    final Observable<WeatherData> responseTwoObservable = weatherService.getWeatherForCity("Zakopane", APP_ID)
        .zipWith(Observable.interval(1, TimeUnit.SECONDS), (e1, e2) -> e1)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation());

    final Observable<String> stringObservable = responseOneObservable.zipWith(responseTwoObservable, (w, p) -> w + " : " + p);
    stringObservable.subscribe(System.out::println);

    Thread.sleep(6000);

    System.exit(0);
  }
}
