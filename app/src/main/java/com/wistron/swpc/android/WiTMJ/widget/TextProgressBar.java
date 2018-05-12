package com.wistron.swpc.android.WiTMJ.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by WH1603043 on 20/5/2016.
 */
public class TextProgressBar extends ProgressBar {

    private Paint mPaint;
    private String text;
    private float rate;

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TextProgressBar(Context context) {
        super(context);
        initView();
    }

    private void initView() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
    }

    @Override
    public synchronized void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);
    }

    private void setText(int progress) {
        rate = progress * 1.0f / this.getMax();
        int i = (int) (rate * 100);
        this.text = String.valueOf(i) + "%";
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, this.text.length(), rect);
        int x = rect.left;
        int y = (getHeight()/2) - rect.centerY();
        //int y = (getHeight() / 2) - rect.top;
        mPaint.setTextSize(22);
        canvas.drawText(text, x, y, mPaint);
    }

}