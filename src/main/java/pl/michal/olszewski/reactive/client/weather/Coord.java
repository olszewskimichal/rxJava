
package pl.michal.olszewski.reactive.client.weather;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
class Coord {

  private BigDecimal lon;
  private BigDecimal lat;

}
