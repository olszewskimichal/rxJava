package pl.michal.olszewski.reactive.rx;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class StockInfo {

  private final String ticker;
  private final BigDecimal price;

  public StockInfo(final String symbol, final BigDecimal thePrice) {
    ticker = symbol;
    price = thePrice;
  }

  public String toString() {
    return String.format("ticker: %s price: %g", ticker, price);
  }

  public static StockInfo fetch(String ticker) {
    int value = ThreadLocalRandom.current().nextInt(0, 10);
    if (value > 5) {
      throw new RuntimeException("Ooops exception");
    }
    return new StockInfo(ticker, BigDecimal.valueOf(value));
  }

  public BigDecimal getPrice() {
    return price;
  }
}