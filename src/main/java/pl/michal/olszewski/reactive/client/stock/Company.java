
package pl.michal.olszewski.reactive.client.stock;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Company {

  @SerializedName("symbol")
  @Expose
  public String symbol;
  @SerializedName("companyName")
  @Expose
  public String companyName;
  @SerializedName("exchange")
  @Expose
  public String exchange;
  @SerializedName("industry")
  @Expose
  public String industry;
  @SerializedName("website")
  @Expose
  public String website;
  @SerializedName("description")
  @Expose
  public String description;
  @SerializedName("CEO")
  @Expose
  public String cEO;
  @SerializedName("issueType")
  @Expose
  public String issueType;
  @SerializedName("sector")
  @Expose
  public String sector;

}
