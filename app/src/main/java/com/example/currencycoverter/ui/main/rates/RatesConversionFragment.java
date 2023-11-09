package com.example.currencycoverter.ui.main.rates;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.currencycoverter.databinding.FragmentRatesConversionBinding;
import com.example.currencycoverter.data.liveRate.ExcahngeLiveRateResponse;
import com.example.currencycoverter.network.Status;
import com.example.currencycoverter.ui.main.viewmodel.CurrencyChangeViewModel;
import com.example.currencycoverter.ui.main.viewmodel.CurrencyChangeViewModelListener;
import com.example.currencycoverter.utils.Utils;

public class RatesConversionFragment extends Fragment implements CurrencyChangeViewModelListener {

    private FragmentRatesConversionBinding mBinding;

    private CurrencyChangeViewModel mViewModel;

    private CountDownTimer mResendTimer = null;
    private double mConversionAmount = -1;
    private double eur_mxn = -1, eur_aud = -1, eur_hkd = -1;
    private boolean mGotRate = false;

    public RatesConversionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentRatesConversionBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(CurrencyChangeViewModel.class);
        mViewModel.setViewModelListener(this);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        initViews();
        observeViewModel(mViewModel);
        getLiveExchangeRate();
        return mBinding.getRoot();
    }

    private void initViews() {

    }

    public void setAmount(String amount) {
        mConversionAmount = Double.parseDouble(amount);

        if (mGotRate) {
            mBinding.euroToMxnConvertedAmount.setText(Utils.getRoundedValue(mConversionAmount * eur_mxn, 2) + "");
            mBinding.euroToAudConvertedAmount.setText(Utils.getRoundedValue(mConversionAmount * eur_aud, 2) + "");
            mBinding.euroToHkdConvertedAmount.setText(Utils.getRoundedValue(mConversionAmount * eur_hkd, 2) + "");
        } else {
            getLiveExchangeRate();
        }
    }

    private void startTimer(long countDownTime) {
        mResendTimer = new CountDownTimer(countDownTime, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                getLiveExchangeRate();
            }
        };
        mResendTimer.start();
    }

    //cancel timer
    private void cancelTimer() {
        if (mResendTimer != null)
            mResendTimer.cancel();
    }

    private void getLiveExchangeRate() {
        if (Utils.connectionStatusOk(getActivity())) {
            mViewModel.getLiveRate("a8df25749c191d69591396c20d6f0d6c", "MXN,HKD,AUD", "EUR");
        }
    }

    // Observer to observe view model for any response from server
    private void observeViewModel(final CurrencyChangeViewModel viewModel) {
        viewModel.getCurrencyChangeLiveData().observe(getViewLifecycleOwner(), apiResponse -> {
            if (apiResponse != null) {
                mBinding.setIsLoading(false);
                if (apiResponse.status == Status.SUCCESS) {
                    if (apiResponse.data instanceof ExcahngeLiveRateResponse) {
                        gotLiveRate((ExcahngeLiveRateResponse) apiResponse.data);
                        startTimer(30000);
                    }
                } else if (apiResponse.status == Status.ERROR) {
                    apiResponse.error.fillInStackTrace();
                    Log.i("infoo", "observeViewModel: " + apiResponse.error.getMessage());
                    String error = Utils.errorType(apiResponse.error);
                    if (apiResponse.error.fillInStackTrace().toString().contains("JsonSyntaxException") ||
                            apiResponse.error.fillInStackTrace().toString().contains("JSONException") ||
                            apiResponse.error.fillInStackTrace().toString().contains("org.json")) {
                        error = "Server error. Please try again later.";

                    }
                    Utils.info(getActivity(), error, 4);
                    resetTexts();

                }
            }
        });
    }

    private void gotLiveRate(ExcahngeLiveRateResponse excahngeLiveRateResponse) {
        if (excahngeLiveRateResponse != null && excahngeLiveRateResponse.isSuccess()) {

            eur_mxn = Utils.getRoundedValue(excahngeLiveRateResponse.getQuotes().getEURMXN(), 2);
            eur_aud = Utils.getRoundedValue(excahngeLiveRateResponse.getQuotes().getEURAUD(), 2);
            eur_hkd = Utils.getRoundedValue(excahngeLiveRateResponse.getQuotes().getEURHKD(), 2);

//            eur_mxn = BigDecimal.valueOf(excahngeLiveRateResponse.getQuotes().getEURMXN()).setScale(2, RoundingMode.HALF_UP).doubleValue();
//            eur_aud = BigDecimal.valueOf(excahngeLiveRateResponse.getQuotes().getEURAUD()).setScale(2, RoundingMode.HALF_UP).doubleValue();
//            eur_hkd = BigDecimal.valueOf(excahngeLiveRateResponse.getQuotes().getEURHKD()).setScale(2, RoundingMode.HALF_UP).doubleValue();

            mBinding.euroToMxnRate.setText("1 EUR = " + eur_mxn + " MXN");
            mBinding.euroToAudRate.setText("1 EUR = " + eur_aud + " AUD");
            mBinding.euroToHkdRate.setText("1 EUR = " + eur_hkd + " HKD");

            double mxn_euro = Utils.getRoundedValue(1 / eur_mxn, 2);
            double aud_euro = Utils.getRoundedValue(1 / eur_aud, 2);
            double hkd_euro = Utils.getRoundedValue(1 / eur_hkd, 2);

//            double mxn_euro = BigDecimal.valueOf(1 / eur_mxn).setScale(2, RoundingMode.HALF_UP).doubleValue();
//            double aud_euro = BigDecimal.valueOf(1 / eur_aud).setScale(2, RoundingMode.HALF_UP).doubleValue();
//            double hkd_euro = BigDecimal.valueOf(1 / eur_hkd).setScale(2, RoundingMode.HALF_UP).doubleValue();

            mBinding.mxnToEuroRate.setText("1 MXN = " + mxn_euro + " MXN");
            mBinding.audToEuroRate.setText("1 AUD = " + aud_euro + " AUD");
            mBinding.hkdToEuroRate.setText("1 HKD = " + hkd_euro + " HKD");

            if (mConversionAmount != -1) {
                mBinding.euroToMxnConvertedAmount.setText(Utils.getRoundedValue(mConversionAmount * eur_mxn, 2) + "");
                mBinding.euroToAudConvertedAmount.setText(Utils.getRoundedValue(mConversionAmount * eur_aud, 2) + "");
                mBinding.euroToHkdConvertedAmount.setText(Utils.getRoundedValue(mConversionAmount * eur_hkd, 2) + "");

//                mBinding.euroToMxnConvertedAmount.setText(BigDecimal.valueOf(mConversionAmount * eur_mxn).setScale(2, RoundingMode.HALF_UP).doubleValue() + "");
//                mBinding.euroToAudConvertedAmount.setText(BigDecimal.valueOf(mConversionAmount * eur_aud).setScale(2, RoundingMode.HALF_UP).doubleValue() + "");
//                mBinding.euroToHkdConvertedAmount.setText(BigDecimal.valueOf(mConversionAmount * eur_hkd).setScale(2, RoundingMode.HALF_UP).doubleValue() + "");
            }

            mGotRate = true;
        } else {
            resetTexts();
            Utils.info(getActivity(), "Something went wrong", 4);
        }
    }

    private void resetTexts(){
        mBinding.euroToMxnRate.setText("-----");
        mBinding.euroToAudRate.setText("-----");
        mBinding.euroToHkdRate.setText("-----");

        mBinding.mxnToEuroRate.setText("-----");
        mBinding.audToEuroRate.setText("-----");
        mBinding.hkdToEuroRate.setText("-----");

        mBinding.euroToMxnConvertedAmount.setText("-----");
        mBinding.euroToAudConvertedAmount.setText("-----");
        mBinding.euroToHkdConvertedAmount.setText("-----");

        mGotRate = false;
    }

    @Override
    public void onConvertRate() {
        mBinding.setIsLoading(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelTimer();
    }
}