package com.example.currencycoverter.network.repositories;

import android.annotation.SuppressLint;

import com.example.currencycoverter.data.liveRate.ExcahngeLiveRateResponse;
import com.example.currencycoverter.network.NetworkInterface;
import com.example.currencycoverter.utils.Constants;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

// this class is used for making api calls using retrofit client
public class NetworkRepository {

    private NetworkInterface mNetworkInterface;
    private static NetworkRepository mNetworkRepository;

    // for setting up retrofit object which is used in api calls
    private NetworkRepository() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.cache(null);
        builder.writeTimeout(60, TimeUnit.SECONDS);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        TimeZone tz = TimeZone.getDefault();
        builder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Accept", "*/*")
                        .header("Content-Type", "application/json")
                        .cacheControl(new CacheControl.Builder().noCache().build())
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        OkHttpClient okHttpClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        mNetworkInterface = retrofit.create(NetworkInterface.class);
    }

    public synchronized static NetworkRepository getInstance() {
        if (mNetworkRepository == null) {
            mNetworkRepository = new NetworkRepository();
        }
        return mNetworkRepository;
    }

    @SuppressLint("CheckResult")
    public Observable<ExcahngeLiveRateResponse> getLiveRate(String accessKey, String currencies, String sourceCurrency) {
        return mNetworkInterface.getLiveRate(accessKey, currencies, sourceCurrency);
    }

    @SuppressLint("CheckResult")
    public Observable<Response<ResponseBody>> getHistoryRate(String accessKey, String currencies, String sourceCurrency, String startDate, String endDate) {
        return mNetworkInterface.getHistoryRate(accessKey, currencies, sourceCurrency, startDate, endDate);
    }

}
