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
public class TrainSpeedProgressBar extends ProgressBar {

    private Paint mPaint;
    private String text;
    private float rate;

    public TrainSpeedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TrainSpeedProgressBar(Context context) {
        super(context);
        initView();
    }

    private void initView() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
    }

    public int getProgress() {
        String mSpeed = PreferencesUtil.getPrefString(getContext(),PreferenceConstants.TRAINING_CYCLING_SPEED, "");
        int speed = Integer.parseInt(mSpeed);
        int totalSpeed = 20;
        double i = (double) speed / totalSpeed;
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
        String mSpeed = PreferencesUtil.getPrefString(getContext(),PreferenceConstants.TRAINING_CYCLING_SPEED, "");
        int speed =Integer.parseInt(mSpeed);
        rate = progress * 1.0f / this.getMax();
        this.text = String.valueOf(speed) + " km/hr";
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, this.text.length(), rect);
        int x = 10;
        int y = (getHeight() / 2) - rect.centerY();
        mPaint.setTextSize(40);
        canvas.drawText(text, x, y, mPaint);
    }

}