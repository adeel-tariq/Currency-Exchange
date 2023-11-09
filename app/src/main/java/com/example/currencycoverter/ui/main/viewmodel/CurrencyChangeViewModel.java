package com.example.currencycoverter.ui.main.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.currencycoverter.network.ApiResponse;
import com.example.currencycoverter.network.repositories.NetworkRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CurrencyChangeViewModel extends AndroidViewModel {

    private CurrencyChangeViewModelListener mCurrencyChangeViewModelListener;

    // Live data for storing api response data and sending it back to view
    private MutableLiveData<ApiResponse> mCurrencyChangeResponseMutableLiveData;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public CurrencyChangeViewModel(@NonNull Application application) {
        super(application);
    }

    public void setViewModelListener(CurrencyChangeViewModelListener listener) {
        this.mCurrencyChangeViewModelListener = listener;
    }

    // For providing live data to view
    public LiveData<ApiResponse> getCurrencyChangeLiveData() {
        if (mCurrencyChangeResponseMutableLiveData == null) {
            mCurrencyChangeResponseMutableLiveData = new MutableLiveData<>();
        }
        return mCurrencyChangeResponseMutableLiveData;
    }

    @SuppressLint("CheckResult")
    public void getLiveRate(String accessKey, String currencies, String sourceCurrency) {
        mCurrencyChangeViewModelListener.onConvertRate();
        mDisposables.add(NetworkRepository.getInstance().getLiveRate(accessKey, currencies, sourceCurrency)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mCurrencyChangeResponseMutableLiveData.setValue(ApiResponse.success(result)),
                        throwable -> mCurrencyChangeResponseMutableLiveData.setValue(ApiResponse.error(throwable))
                ));
    }

    @SuppressLint("CheckResult")
    public void getHistoryRate(String accessKey, String currencies, String sourceCurrency, String startDate, String endDate) {
        mCurrencyChangeViewModelListener.onConvertRate();
        mDisposables.add(NetworkRepository.getInstance().getHistoryRate(accessKey, currencies, sourceCurrency, startDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mCurrencyChangeResponseMutableLiveData.setValue(ApiResponse.success(result)),
                        throwable -> mCurrencyChangeResponseMutableLiveData.setValue(ApiResponse.error(throwable))
                ));
    }
}
