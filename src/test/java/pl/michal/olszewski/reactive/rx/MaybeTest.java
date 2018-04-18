package pl.michal.olszewski.reactive.rx;

import io.reactivex.Maybe;
import org.junit.jupiter.api.Test;

/**
 * Maybe is a special kind of Observable which can only emit zero or one item, and report an error if the computation fails at some point.
 *
 * In this regard, it’s like a union of Single and Completable. All these reduced types, including Maybe, offer a subset of the Flowable operators. This means we can work with Maybe like a Flowable as long as the operation makes sense for 0 or 1 items.
 */
class MaybeTest {

  @Test
  void test1() {
    Maybe.just(1)
        .map(x -> x + 7)
        .filter(x -> x > 0)
        .test()
        .assertResult(8);
  }

  @Test
  void test2() {
    Maybe.just(1)
        .subscribe(
            x -> System.out.print("Emitted item: " + x),
            ex -> System.out.println("Error: " + ex.getMessage()),
            () -> System.out.println("Completed. No items.")
        );
  }

}
