package pl.michal.olszewski.reactive.client;

import io.reactivex.Observable;
import java.math.BigDecimal;
import java.util.List;
import pl.michal.olszewski.reactive.client.stock.Company;
import pl.michal.olszewski.reactive.client.stock.Stock;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StockRxApi {

  @GET("stock/{companySymbol}/price")
  Observable<BigDecimal> getPriceForCompany(@Path("companySymbol") String companySymbol);

  @GET("stock/{companySymbol}/quote")
  Observable<Stock> getStockForCompany(@Path("companySymbol") String companySymbol);

  @GET("stock/{companySymbol}/company")
  Observable<Company> getCompanyInfoForName(@Path("companySymbol") String companySymbol);

  @GET("stock/market/list/mostactive")
  Observable<List<Stock>> getMostActiveStocks();

}