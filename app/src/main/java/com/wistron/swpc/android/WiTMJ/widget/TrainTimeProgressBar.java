package com.wistron.swpc.android.WiTMJ.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;

/**
 * Created by WH1603043 on 20/5/2016.
 */
public class TrainTimeProgressBar extends ProgressBar {

    private Paint mPaint;
    private String text;
    private float rate;

    public TrainTimeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TrainTimeProgressBar(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
    }

    public int getProgress() {
        String hr = PreferencesUtil.getPrefString(getContext(), PreferenceConstants.TRAINING_CYCLING_DURATION_HRS, "");
        String min = PreferencesUtil.getPrefString(getContext(), PreferenceConstants.TRAINING_CYCLING_DURATION_MINS, "");
        int mTime = Integer.parseInt(hr) * 60 * 60 + Integer.parseInt(min) * 60;
        int totalTime = 2 * 60 * 60;
        double i = (double) mTime / totalTime;
        int progress = (int) (i * 100);
        return progress;
    }

    @Override
    public synchronized void setProgress(int progress) {
        int mProgress = getProgress();
        setText(mProgress);
        super.setProgress(mProgress);
    }

    private void setText(int progress) {
        String hr = PreferencesUtil.getPrefString(getContext(), PreferenceConstants.TRAINING_CYCLING_DURATION_HRS, "");
        String min = PreferencesUtil.getPrefString(getContext(), PreferenceConstants.TRAINING_CYCLING_DURATION_MINS, "");
        rate = progress * 1.0f / this.getMax();
        if (Integer.parseInt(min) < 10) {
            this.text = "0" + hr + ":" + "0" + min + ":" + "00";
        } else {
            this.text = "0" + hr + ":" + min + ":" + "00";
        }

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, this.text.length(), rect);
        int x = getWidth() / 4 + 30;
        int y = 100;
        mPaint.setTextSize(100);
        canvas.drawText(text, x, y, mPaint);
    }

}