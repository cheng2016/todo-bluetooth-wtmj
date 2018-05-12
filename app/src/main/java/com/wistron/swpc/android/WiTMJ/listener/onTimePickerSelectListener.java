package com.wistron.swpc.android.WiTMJ.listener;

import android.widget.TimePicker;

/**
 * Created by WH1604025 on 2016/4/26.
 */
public interface onTimePickerSelectListener {
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute);
}
