package pl.michal.olszewski.reactive.client;

import io.reactivex.Observable;
import pl.michal.olszewski.reactive.client.weather.WeatherData;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherRxApi {

  @GET("weather?")
  Observable<WeatherData> getWeatherForCity(@Query("q") String city, @Query("appid") String appId);

}