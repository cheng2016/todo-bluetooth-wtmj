package com.wistron.swpc.android.WiTMJ.widget;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.listener.onCalendarSelectListener;

import java.text.SimpleDateFormat;

/**
 * Created by WH1604025 on 2016/4/22.
 */
public class CalendarDialog  {
    private View view;
    private Context context;
    private AlertDialog dialog;
    private LinearLayout lLayout_bg;
    private Display display;
    private int winWidth=0;//屏幕宽度
    private float density=0.0f;//屏幕密度

    SimpleDateFormat f = new SimpleDateFormat("yyyy年MM月dd日hh:mm:ss");

    onCalendarSelectListener listener;

    static int year,month,day;

    private TextView dateTv;

    private CalendarView calendarView;

    public CalendarDialog(Activity context) {
        this.context = context;

    }

    public CalendarDialog(Fragment context) {
        this.context = context.getActivity();
    }

    public CalendarDialog(Activity context, TextView dateTv) {
        this.context = context;
        this.dateTv = dateTv;
    }

    public CalendarDialog builder() {
        // 获取Dialog布局
        view = LayoutInflater.from(context).inflate(
                R.layout.dialog_calendar, null);

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                T.show(FORMATTER.format(view.getDate());
//                T.showShort(year+""+month+""+dayOfMonth);
                CalendarDialog.year = year;
                CalendarDialog.month = month;
                CalendarDialog.day = dayOfMonth;
            }
        });
        calendarView.setSelected(true);

        // 定义Dialog布局和参数
        dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onSelectedDayChange(calendarView,year,month,day);
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
        return this;
    }

    public CalendarDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public CalendarDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }


    public CalendarDialog setListener(onCalendarSelectListener listener){
        this.listener = listener;
        return this;
    }
}
