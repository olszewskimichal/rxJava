
package pl.michal.olszewski.reactive.client.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Main {

  @SerializedName("temp")
  @Expose
  private BigDecimal temp;
  @SerializedName("pressure")
  @Expose
  private BigDecimal pressure;
  @SerializedName("humidity")
  @Expose
  private Integer humidity;
  @SerializedName("temp_min")
  @Expose
  private BigDecimal tempMin;
  @SerializedName("temp_max")
  @Expose
  private BigDecimal tempMax;

}
