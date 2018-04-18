
package pl.michal.olszewski.reactive.client.stock;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class Stock {

  @SerializedName("symbol")
  @Expose
  public String symbol;
  @SerializedName("companyName")
  @Expose
  public String companyName;
  @SerializedName("primaryExchange")
  @Expose
  public String primaryExchange;
  @SerializedName("sector")
  @Expose
  public String sector;
  @SerializedName("calculationPrice")
  @Expose
  public String calculationPrice;
  @SerializedName("open")
  @Expose
  public BigDecimal open;
  @SerializedName("openTime")
  @Expose
  public Long openTime;
  @SerializedName("close")
  @Expose
  public BigDecimal close;
  @SerializedName("closeTime")
  @Expose
  public Long closeTime;
  @SerializedName("high")
  @Expose
  public BigDecimal high;
  @SerializedName("low")
  @Expose
  public BigDecimal low;
  @SerializedName("latestPrice")
  @Expose
  public BigDecimal latestPrice;
  @SerializedName("latestSource")
  @Expose
  public String latestSource;
  @SerializedName("latestTime")
  @Expose
  public String latestTime;
  @SerializedName("latestUpdate")
  @Expose
  public Long latestUpdate;
  @SerializedName("latestVolume")
  @Expose
  public Integer latestVolume;
  @SerializedName("iexRealtimePrice")
  @Expose
  public BigDecimal iexRealtimePrice;
  @SerializedName("iexRealtimeSize")
  @Expose
  public Integer iexRealtimeSize;
  @SerializedName("iexLastUpdated")
  @Expose
  public Long iexLastUpdated;
  @SerializedName("delayedPrice")
  @Expose
  public BigDecimal delayedPrice;
  @SerializedName("delayedPriceTime")
  @Expose
  public Long delayedPriceTime;
  @SerializedName("previousClose")
  @Expose
  public BigDecimal previousClose;
  @SerializedName("change")
  @Expose
  public BigDecimal change;
  @SerializedName("changePercent")
  @Expose
  public BigDecimal changePercent;
  @SerializedName("iexMarketPercent")
  @Expose
  public BigDecimal iexMarketPercent;
  @SerializedName("iexVolume")
  @Expose
  public Integer iexVolume;
  @SerializedName("avgTotalVolume")
  @Expose
  public Integer avgTotalVolume;
  @SerializedName("iexBidPrice")
  @Expose
  public BigDecimal iexBidPrice;
  @SerializedName("iexBidSize")
  @Expose
  public Integer iexBidSize;
  @SerializedName("iexAskPrice")
  @Expose
  public BigDecimal iexAskPrice;
  @SerializedName("iexAskSize")
  @Expose
  public Integer iexAskSize;
  @SerializedName("marketCap")
  @Expose
  public Long marketCap;
  @SerializedName("peRatio")
  @Expose
  public BigDecimal peRatio;
  @SerializedName("week52High")
  @Expose
  public BigDecimal week52High;
  @SerializedName("week52Low")
  @Expose
  public BigDecimal week52Low;
  @SerializedName("ytdChange")
  @Expose
  public BigDecimal ytdChange;

}
