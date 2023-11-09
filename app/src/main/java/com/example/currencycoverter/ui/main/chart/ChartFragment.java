package com.example.currencycoverter.ui.main.chart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.currencycoverter.R;
import com.example.currencycoverter.databinding.FragmentChartBinding;
import com.example.currencycoverter.network.Status;
import com.example.currencycoverter.ui.main.viewmodel.CurrencyChangeViewModel;
import com.example.currencycoverter.ui.main.viewmodel.CurrencyChangeViewModelListener;
import com.example.currencycoverter.utils.Utils;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ChartFragment extends Fragment implements CurrencyChangeViewModelListener {

    private FragmentChartBinding mBinding;

    private CurrencyChangeViewModel mViewModel;

    private double mConversionAmount = 1;

    protected Typeface tfLight;
    private ArrayList<ILineDataSet> mILineDataSets = new ArrayList<>();
    ArrayList<String> listOfDatesKey = new ArrayList<>();
    ArrayList<Double> eur_mxn_list = new ArrayList<Double>();
    ArrayList<Double> eur_hkd_list = new ArrayList<Double>();
    ArrayList<Double> eur_aud_list = new ArrayList<Double>();

    private String mCurrency = "MXN", mStartDate, mEndDate;

    public ChartFragment() {
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
        mBinding = FragmentChartBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(CurrencyChangeViewModel.class);
        mViewModel.setViewModelListener(this);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        tfLight = getResources().getFont(R.font.inter_regular);
        initViews();
        observeViewModel(mViewModel);

        return mBinding.getRoot();
    }

    private void initViews() {

        mBinding.chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // enable scaling and dragging
        mBinding.chart.setDragEnabled(true);
        mBinding.chart.setScaleEnabled(true);
        mBinding.chart.setDrawGridBackground(false);
        mBinding.chart.setHighlightPerDragEnabled(true);

        mBinding.chart.getXAxis().setDrawGridLines(true); // disable grid lines for the XAxis
        mBinding.chart.getAxisLeft().setDrawGridLines(true); // disable grid lines for the left YAxis
        mBinding.chart.getAxisRight().setDrawGridLines(true); // disable grid lines for the right YAxis
//        mBinding.chart.getAxisRight().setAxisMaximum(100);

        mBinding.chart.getLegend().setEnabled(false);
        mBinding.chart.getDescription().setEnabled(false);

        mBinding.chart.getAxisLeft().setDrawAxisLine(false);
        mBinding.chart.getAxisLeft().setDrawLabels(false);
//        mBinding.chart.getAxisLeft().setAxisMaximum(100);

        mBinding.chart.getAxisRight().setEnabled(false);

        mBinding.chart.getXAxis().setDrawAxisLine(false);
        mBinding.chart.getXAxis().setDrawLabels(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mBinding.chart.setPinchZoom(true);

        // set an alternative background color
        mBinding.chart.setBackgroundColor(Color.WHITE);
        mBinding.chart.animateX(1500);

        ArrayList<String> currencies = new ArrayList<>();
        currencies.add("MXN");
        currencies.add("AUD");
        currencies.add("HKD");
        ArrayAdapter typeAdapter = new ArrayAdapter(getActivity(), R.layout.list_item, currencies);
        mBinding.currencySelectDropEdt.setAdapter(typeAdapter);

        mBinding.currencySelectDropEdt.setText("MXN", false);

        mBinding.currencySelectDropEdt.setOnItemClickListener((parent, view, position, id) -> {
            //Do something here when item is selected
            if (!currencies.get(position).equals(mCurrency)) {
                mCurrency = currencies.get(position);
                setData();
            }
        });

        mBinding.selectionPeriodToggleBtnGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.selection_period_one_day) {
                        mEndDate = Utils.getToday();
                        mStartDate = Utils.getOneDate();
                        getHistoryExchangeRate(mStartDate, mEndDate);

                    } else if (checkedId == R.id.selection_period_one_month) {
                        mEndDate = Utils.getToday();
                        mStartDate = Utils.getOneMonth();
                        getHistoryExchangeRate(mStartDate, mEndDate);

                    } else if (checkedId == R.id.selection_period_one_year) {
                        mEndDate = Utils.getToday();
                        mStartDate = Utils.getOneYear();
                        getHistoryExchangeRate(mStartDate, mEndDate);
                    }
                }
            }
        });

        mEndDate = Utils.getToday();
        mStartDate = Utils.getOneMonth();

        getHistoryExchangeRate(mStartDate, mEndDate);
    }

    private void getHistoryExchangeRate(String startDate, String endDate) {
        if (Utils.connectionStatusOk(getActivity())) {
            clearAll();
            mViewModel.getHistoryRate("a8df25749c191d69591396c20d6f0d6c", "MXN,HKD,AUD", "EUR", startDate, endDate);
        }
    }

    private void observeViewModel(final CurrencyChangeViewModel viewModel) {
        viewModel.getCurrencyChangeLiveData().observe(getViewLifecycleOwner(), apiResponse -> {
            if (apiResponse != null) {
                mBinding.setIsLoading(false);
                if (apiResponse.status == Status.SUCCESS) {
                    Response<ResponseBody> responseBodyResponse = (Response<ResponseBody>) apiResponse.data;
                    gotHistoryRate(responseBodyResponse);
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
                    mBinding.chart.clear();

                }
            }
        });
    }

    private void gotHistoryRate(Response<ResponseBody> responseBodyResponse) {
        try {
            String s = responseBodyResponse.body().string();
            Object json = new JSONTokener(s).nextValue();
            if (json instanceof JSONObject) {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.has("success")) {
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        JSONObject quotesObject = jsonObject.getJSONObject("quotes");

                        Iterator<String> keys = quotesObject.keys();

                        while (keys.hasNext()) {
                            // Get the keys
                            String key = keys.next();
                            listOfDatesKey.add(key);

                            eur_mxn_list.add(Utils.getRoundedValue((Double) quotesObject.getJSONObject(key).get("EURMXN") * mConversionAmount, 2));
                            eur_hkd_list.add(Utils.getRoundedValue((Double) quotesObject.getJSONObject(key).get("EURHKD") * mConversionAmount, 2));
                            eur_aud_list.add(Utils.getRoundedValue((Double) quotesObject.getJSONObject(key).get("EURAUD") * mConversionAmount, 2));
                        }

                        setData();

                    } else {
                        mBinding.chart.clear();
                    }
                }
            }
        } catch (Exception e) {
            mBinding.chart.clear();
            e.printStackTrace();
        }
    }

    private void setData() {

        mBinding.chart.clear();
        mBinding.chart.invalidate();
        List<LineDataSet> activationChartLineDataSets = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Double> data = null;
        if (mCurrency.equals("MXN")) {
            data = eur_mxn_list;
        } else if (mCurrency.equals("HKD")) {
            data = eur_hkd_list;
        } else {
            data = eur_aud_list;
        }

        for (int j = 0; j < data.size(); j++) {
            double dataValue = data.get(j);
            entries.add(new Entry(j, (float) dataValue));
        }

        activationChartLineDataSets.add(new LineDataSet(entries, "adeel"));
        activationChartLineDataSets.get(0).setValueTextSize(9f);
        activationChartLineDataSets.get(0).setDrawValues(false);
        activationChartLineDataSets.get(0).setDrawValues(false);
        activationChartLineDataSets.get(0).setDrawCircles(false);
        activationChartLineDataSets.get(0).setDrawCircleHole(false);
//        activationChartLineDataSets.get(0).setCircleColor(Color.parseColor("#000000"));
        activationChartLineDataSets.get(0).setColor(Color.parseColor("#00415F"));
        activationChartLineDataSets.get(0).setLineWidth(.2f);

        LineDataSet lineDataSet = activationChartLineDataSets.get(0);
        lineDataSet.setFillDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.gradient));
        lineDataSet.setDrawFilled(true);

        mILineDataSets.clear();
        mILineDataSets.add(activationChartLineDataSets.get(0));

        drawGraph();
    }

    private void drawGraph() {

        mBinding.chart.clear();
        mBinding.chart.setNoDataTextColor(Color.BLACK);
        mBinding.chart.setNoDataText("No data to draw chart");

        if (!mILineDataSets.isEmpty()) {

            mBinding.chart.setData(new LineData(mILineDataSets));

            ReportsActivationCustomMarker mv = new ReportsActivationCustomMarker(mCurrency, getActivity(), R.layout.custom_marker_line, listOfDatesKey);
            mBinding.chart.setMarker(mv);
            mBinding.chart.setDrawMarkers(true);

            mBinding.chart.invalidate();
        }
    }

    public void setAmount(String amount) {
        mConversionAmount = Double.parseDouble(amount);
        getHistoryExchangeRate(mStartDate, mEndDate);
    }

    private void clearAll() {
        mBinding.chart.clear();
        mBinding.chart.invalidate();
        mILineDataSets.clear();
        listOfDatesKey.clear();
        eur_mxn_list.clear();
        eur_aud_list.clear();
        eur_hkd_list.clear();
    }

    @Override
    public void onConvertRate() {
        mBinding.setIsLoading(true);
    }
}