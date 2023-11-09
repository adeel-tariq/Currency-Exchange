package com.example.currencycoverter.ui.main;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.currencycoverter.R;
import com.example.currencycoverter.databinding.ActivityMainBinding;
import com.example.currencycoverter.ui.main.chart.ChartFragment;
import com.example.currencycoverter.ui.main.rates.RatesConversionFragment;

public class MainActivity extends AppCompatActivity {

    protected ActivityMainBinding mBinding;

    private RatesConversionFragment mRatesConversionFragment;
    private ChartFragment mChartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initViews();
    }

    private void initViews() {

        ColorStateList def;

        def = mBinding.item2.getTextColors();

        mBinding.item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.select.animate().x(0).setDuration(100);
                mBinding.item1.setTextColor(Color.BLACK);
                mBinding.item2.setTextColor(Color.WHITE);
                mBinding.item2.setTextColor(def);
                mBinding.viewPager.setCurrentItem(0);
            }
        });

        mBinding.item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.item1.setTextColor(def);
                mBinding.item2.setTextColor(Color.BLACK);
                mBinding.item1.setTextColor(Color.WHITE);
                int size = mBinding.item2.getWidth();
                mBinding.select.animate().x(size).setDuration(100);
                mBinding.viewPager.setCurrentItem(1);
            }
        });

        mBinding.rateConvertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.conversionAmountEdt.getText() != null) {
                    mRatesConversionFragment.setAmount(mBinding.conversionAmountEdt.getText().toString());
                    mChartFragment.setAmount(mBinding.conversionAmountEdt.getText().toString());
                }
            }
        });

        mBinding.conversionAmountEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

                    mBinding.conversionAmountEdt.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(mBinding.conversionAmountEdt.getWindowToken(), 0);

                    if (mBinding.conversionAmountEdt.getText() != null) {
                        mRatesConversionFragment.setAmount(mBinding.conversionAmountEdt.getText().toString());
                        mChartFragment.setAmount(mBinding.conversionAmountEdt.getText().toString());
                    }

                    return true;
                }
                return false;
            }
        });

        mRatesConversionFragment = new RatesConversionFragment();
        mChartFragment = new ChartFragment();

        CurrencyMainTabAdapter competitionsListsAdapter = new CurrencyMainTabAdapter(getSupportFragmentManager(), 1, 2);
        mBinding.viewPager.setAdapter(competitionsListsAdapter);
        mBinding.viewPager.setOffscreenPageLimit(2);
    }

    class CurrencyMainTabAdapter extends FragmentStatePagerAdapter {

        int mNumOfTabs;

        public CurrencyMainTabAdapter(@NonNull FragmentManager fm, int behavior, int tabs) {
            super(fm, behavior);
            this.mNumOfTabs = tabs;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mRatesConversionFragment;
                case 1:
                    return mChartFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

}