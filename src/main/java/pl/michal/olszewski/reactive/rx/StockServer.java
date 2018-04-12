package pl.michal.olszewski.reactive.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import java.util.List;

public class StockServer {

  public static Observable<StockInfo> getFeed(final List<String> symbols) {
    System.out.println("Created Observable");
    return Observable.create(emitter -> emit(emitter, symbols));
  }

  private static void emit(ObservableEmitter<StockInfo> emitter, List<String> symbols) {
    System.out.println("Start emitting");
    symbols.stream()
        .map(StockInfo::fetch)
        .forEach(emitter::onNext);
    emitter.onComplete();
  }

}
