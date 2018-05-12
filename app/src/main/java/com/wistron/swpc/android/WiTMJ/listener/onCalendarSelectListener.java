package com.wistron.swpc.android.WiTMJ.listener;

import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

/**
 * Created by WH1604025 on 2016/4/26.
 */
public interface onCalendarSelectListener {
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth);
}
