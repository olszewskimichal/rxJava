package pl.michal.olszewski.reactive.wjug.nurkiewicz;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class CacheServer {

  public String findBy(final long key) throws InterruptedException {
    System.out.println(Instant.now() + " Loading from Memcached: " + key);
    TimeUnit.MILLISECONDS.sleep(100);
    return "<data>" + key + "</data>";
  }

  public Observable<String> findByRx(final long key) {
    return Observable.fromCallable(() -> findBy(key)).subscribeOn(Schedulers.io());
  }
}
