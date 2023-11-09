package com.example.currencycoverter.ui.main.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.currencycoverter.R;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

// For drawing pop up marker on activation graphs when user clicks on a point.
public class ReportsActivationCustomMarker extends MarkerView implements IMarker {

    private MPPointF mOffset;
    private RelativeLayout mMarkerLayout;
    private TextView mTvContent;
    private List<String> mXValues;
    private int mUiScreenWidth;
    private String mCurrency;

    public ReportsActivationCustomMarker(String currency, Context context, int layoutResource, List<String> activationChartXEntryValue) {
        super(context, layoutResource);
        // this marker view only displays a text view
        mCurrency = currency;
        mTvContent = findViewById(R.id.content_text);
        mMarkerLayout = findViewById(R.id.marker_layout);
        this.mXValues = activationChartXEntryValue;
        mUiScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    // callbacks every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        String activated = String.valueOf(e.getY());
        String date = mXValues.get((int) e.getX());

        mTvContent.setText("\n" + mCurrency + ": " + activated + "\nDate: " + date); // set the entry-value as the display text
        super.refreshContent(e, highlight);
    }

    // For drawing marker in such a way so it does not cut off from graph view.
    @Override
    public void draw(Canvas canvas, float posx, float posy) {
        // Check marker position and update offsets.
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // For setting appropriate background if selected point on graph is to the right of center of graph view.
        if (posx > (mUiScreenWidth / 2) + 50) {
            posx -= viewWidth;
            if (posy >= viewHeight) {
                // For setting appropriate background if selected point on graph is below center of graph view.
                mMarkerLayout.setBackgroundResource(R.drawable.marker_right_top);
                LayoutParams params = (LayoutParams) mTvContent.getLayoutParams();
                params.setMargins(5, 0, 5, 0);
                mTvContent.setLayoutParams(params);
                posy -= 200;
            } else {
                // For setting appropriate background if selected point on graph is above center of graph view.
                mMarkerLayout.setBackgroundResource(R.drawable.marker_right_bottom);
                LayoutParams params = (LayoutParams) mTvContent.getLayoutParams();
                params.setMargins(5, 25, 5, 0);
                mTvContent.setLayoutParams(params);
            }

        } else {
            // For setting appropriate background if selected point on graph is to the left of center of graph view.
            if (posy >= viewHeight) {
                // For setting appropriate background if selected point on graph is below center of graph view.
                mMarkerLayout.setBackgroundResource(R.drawable.marker_left_top);
                LayoutParams params = (LayoutParams) mTvContent.getLayoutParams();
                params.setMargins(5, 0, 5, 0);
                mTvContent.setLayoutParams(params);
                posy -= 200;
            } else {
                // For setting appropriate background if selected point on graph is above center of graph view.
                mMarkerLayout.setBackgroundResource(R.drawable.marker_left_bottom);
                LayoutParams params = (LayoutParams) mTvContent.getLayoutParams();
                params.setMargins(5, 25, 5, 0);
                mTvContent.setLayoutParams(params);
            }
        }

        // translate to the correct position and draw
        canvas.translate(posx, posy);
        draw(canvas);
    }

    @Override
    public MPPointF getOffset() {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return mOffset;
    }
}
