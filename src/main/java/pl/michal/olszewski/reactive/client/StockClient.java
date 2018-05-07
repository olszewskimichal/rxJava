package pl.michal.olszewski.reactive.client;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockClient {

  static Map<String, BigDecimal> prices = new ConcurrentHashMap<>();

  static public void main(final String[] args) {
    final Retrofit retrofit = new Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.iextrading.com/1.0/")
        .build();

    final ExecutorService executorService = Executors.newFixedThreadPool(4);
    final StockRxApi stockService = retrofit.create(StockRxApi.class);

   /* stockService.getMostActiveStocks()
        .subscribeOn(Schedulers.from(executorService))
        .flatMapIterable(v -> v)
        .flatMap(v -> {
          System.out.println(Thread.currentThread() + " " + v);
          return stockService.getPriceForCompany(v.symbol);
        })
        .subscribe(System.out::println);

    Observable.just("AAPL", "MSFT", "GOOGL", "NFLX").repeat()
        .subscribeOn(Schedulers.from(executorService))
        .flatMap(stockService::getCompanyInfoForName)
        .subscribe(v -> System.out.println(Thread.currentThread() + " " + v));*/

   /* Observable.interval(1, TimeUnit.SECONDS).startWith(0L)
        .zipWith(Observable.just("AAPL", "MSFT", "GOOGL", "NFLX").repeat(), (e1, e2) -> e2)
        .subscribeOn(Schedulers.from(executorService))
        .flatMap(stockService::getStockForCompany)
        .subscribe(System.out::println);*/

    Observable.just("AAPL", "MSFT", "GOOGL", "NFLX")
        .flatMap(stockService::getStockForCompany)
        .subscribeOn(Schedulers.from(executorService))
        .subscribe(v -> System.out.println(v.symbol + " " + v.getLatestPrice()));

    Observable.just("AAPL", "MSFT", "GOOGL", "NFLX")
        .flatMap(stockService::getPriceForCompany)
        .subscribeOn(Schedulers.from(executorService))
        .subscribe(System.out::println);

    Observable.just("AAPL", "MSFT", "GOOGL", "NFLX")
        .flatMap(stockService::getStockForCompany)
        .subscribeOn(Schedulers.from(executorService))
        .blockingSubscribe(v -> prices.put(v.symbol, v.getDelayedPrice()));

    Observable.interval(1, TimeUnit.SECONDS).startWith(0L)
        .zipWith(Observable.just("AAPL", "MSFT", "GOOGL", "NFLX").repeat(), (e1, e2) -> e2)
        .subscribeOn(Schedulers.from(executorService))
        .flatMap(stockService::getStockForCompany)
        .map(v -> {
          BigDecimal before = prices.get(v.symbol);
          if (v.getLatestPrice().compareTo(before) != 0) {
            prices.put(v.symbol, v.getLatestPrice());
            return Optional.ofNullable(v);
          }
          return Optional.empty();
        }).subscribe(key -> key.ifPresent(val -> System.out.println("Nowa wartosc " + val)));

  }
}
