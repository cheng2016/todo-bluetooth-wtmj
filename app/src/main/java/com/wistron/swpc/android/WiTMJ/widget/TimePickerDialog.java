package com.wistron.swpc.android.WiTMJ.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.listener.onTimePickerSelectListener;

/**
 * Created by WH1604025 on 2016/4/26.
 */
public class TimePickerDialog {
    private View view;
    private Context context;
    private AlertDialog dialog;
    private LinearLayout lLayout_bg;
    private Display display;
    private int winWidth=0;//屏幕宽度
    private float density=0.0f;//屏幕密度

    private TimePicker timePicker;

    onTimePickerSelectListener listener;

    public TimePickerDialog(BaseActivity context) {
        this.context = context;
    }

    public TimePickerDialog builder() {
        // 获取Dialog布局
        view = LayoutInflater.from(context).inflate(
                R.layout.dialog_timepicker, null);

        timePicker = (TimePicker) view.findViewById(R.id.timePicker);

        //是否使用24小时制
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimeListener());//设定监听

        // 定义Dialog布局和参数
        dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int h=timePicker.getCurrentHour();
                        int m=timePicker.getCurrentMinute();
                        listener.onTimeChanged(timePicker,h,m);
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
        return this;
    }

    public TimePickerDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public TimePickerDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    class TimeListener implements TimePicker.OnTimeChangedListener {
        /**
         * view 当前选中TimePicker控件
         *  hourOfDay 当前控件选中TimePicker 的小时
         * minute 当前选中控件TimePicker  的分钟
         */
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                System.out.println("h:"+ hourOfDay +" m:"+minute);
//            T.showShort("h:"+ hourOfDay +" m:"+minute);
//            listener.onTimeChanged(view,hourOfDay,minute);
        }
    }

    public TimePickerDialog setListener(onTimePickerSelectListener listener){
        this.listener = listener;
        return this;
    }
}
