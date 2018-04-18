
package pl.michal.olszewski.reactive.client.weather;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Sys {

  private BigDecimal type;
  private BigDecimal id;
  private BigDecimal message;
  private String country;
  private BigDecimal sunrise;
  private BigDecimal sunset;

}
