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
public class TrainCaloriesProgressBar extends ProgressBar {

    private Paint mPaint;
    private String text;
    private float rate;

    public TrainCaloriesProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TrainCaloriesProgressBar(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
    }

    public int getProgress() {
        String mCalories = PreferencesUtil.getPrefString(getContext(), PreferenceConstants.TRAINING_CYCLING_CALORIES, "");
        int calories = Integer.parseInt(mCalories);
        int totalCalories = 500;
        double i = (double) calories / totalCalories;
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
        String mCalories = PreferencesUtil.getPrefString(getContext(), PreferenceConstants.TRAINING_CYCLING_CALORIES, "");
        int cal = Integer.parseInt(mCalories);
        rate = progress * 1.0f / this.getMax();
        this.text = String.valueOf(cal) + " cal";
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