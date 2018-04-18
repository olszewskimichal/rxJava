
package pl.michal.olszewski.reactive.client.weather;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Weather {

  private BigDecimal id;
  private String main;
  private String description;
}
