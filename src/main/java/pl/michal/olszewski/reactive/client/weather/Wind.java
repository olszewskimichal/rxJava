package pl.michal.olszewski.reactive.client.weather;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Wind {

  private BigDecimal speed;
  private BigDecimal deg;

}