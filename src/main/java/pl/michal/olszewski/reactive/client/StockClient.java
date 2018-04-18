package pl.michal.olszewski.reactive.client;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockClient {

  static public void main(final String[] args) throws InterruptedException {
    final Retrofit retrofit = new Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.iextrading.com/1.0/")
        .build();

    final ExecutorService executorService = Executors.newFixedThreadPool(4);
    final StockRxApi stockService = retrofit.create(StockRxApi.class);

    stockService.getMostActiveStocks()
        .subscribeOn(Schedulers.from(executorService))
        .flatMapIterable(v -> v)
        .flatMap(v -> {
          System.out.println(Thread.currentThread() + " " + v);
          return stockService.getPriceForCompany(v.symbol);
        })
        .subscribe(System.out::println);

    Observable.just("AAPL", "MSFT", "GOOGL", "NFLX")
        .subscribeOn(Schedulers.from(executorService))
        .flatMap(stockService::getCompanyInfoForName)
        .subscribe(v -> System.out.println(Thread.currentThread() + " " + v));

    Observable.just("AAPL", "MSFT", "GOOGL", "NFLX")
        .subscribeOn(Schedulers.from(executorService))
        .flatMap(stockService::getStockForCompany)
        .subscribe(v -> System.out.println(Thread.currentThread() + " " + v));
    
  }
}
