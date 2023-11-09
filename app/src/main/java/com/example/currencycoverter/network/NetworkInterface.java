package com.example.currencycoverter.network;

import com.example.currencycoverter.data.liveRate.ExcahngeLiveRateResponse;
import com.example.currencycoverter.utils.Constants;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Retrofit api request methods class which points to all api endpoints
public interface NetworkInterface {

    @GET(Constants.CONVERSION_RATE)
    Observable<ExcahngeLiveRateResponse> getLiveRate(@Query("access_key") String accessKey,
                                                     @Query("currencies") String currencies,
                                                     @Query("source") String sourceCurrency);

    @GET(Constants.CONVERSION_TIME_FRAME)
    Observable<Response<ResponseBody>> getHistoryRate(@Query("access_key") String accessKey,
                                                      @Query("currencies") String currencies,
                                                      @Query("source") String sourceCurrency,
                                                      @Query("start_date") String startDate,
                                                      @Query("end_date") String endDate);
}
