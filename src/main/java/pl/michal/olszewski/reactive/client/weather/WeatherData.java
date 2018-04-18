
package pl.michal.olszewski.reactive.client.weather;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WeatherData {

  private Coord coord;
  private List<Weather> weather = null;
  private String base;
  private Main main;
  private Double visibility;
  private Wind wind;
  private Clouds clouds;
  private BigDecimal dt;
  private Sys sys;
  private BigDecimal id;
  private String name;
  private BigDecimal cod;

}
