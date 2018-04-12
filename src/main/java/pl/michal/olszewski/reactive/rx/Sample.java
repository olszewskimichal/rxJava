package pl.michal.olszewski.reactive.rx;

import io.reactivex.Observable;
import java.math.BigDecimal;
import java.util.List;

public class Sample {

  public static void main(String[] args) {
    final List<String> symbols = List.of("AAPL", "GOOG", "MSFT", "INTC");
    Observable<StockInfo> feed = StockServer.getFeed(symbols);
    System.out.println("Got Observable");
    feed
        .filter(v -> v.getPrice().compareTo(BigDecimal.valueOf(2)) > 0)
        .subscribe(
            System.out::println,
            System.out::println,
            () -> System.out.println("DONE")
        );
  }

}
