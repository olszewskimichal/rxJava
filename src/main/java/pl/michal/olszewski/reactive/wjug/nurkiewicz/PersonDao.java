package pl.michal.olszewski.reactive.wjug.nurkiewicz;

import io.reactivex.Observable;
import java.time.Instant;


public class PersonDao {


  public Person findById(int id) throws InterruptedException {
    System.out.println(Instant.now() + " Loading " + id);
    Thread.sleep(1000);
    return new Person();
  }

  public Observable<Person> findByIdRx(int id) {
    return Observable.fromCallable(() -> findById(id));
  }

}
