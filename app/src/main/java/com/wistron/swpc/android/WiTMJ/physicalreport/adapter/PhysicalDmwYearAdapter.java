package com.wistron.swpc.android.WiTMJ.physicalreport.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.R;

/**
 * Created by WH1603043 on 8/4/2016.
 */
public class PhysicalDmwYearAdapter extends BaseAdapter {
    private String [] mStr;
    private LayoutInflater mInflater;
    private int color = 0xFFFFFFFF;

    public PhysicalDmwYearAdapter(Context context, String [] str) {
        mStr = str;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mStr.length;
    }

    @Override
    public Object getItem(int position) {
        return mStr[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.physical_date_year_item, null);
            viewHolder.date = (TextView)convertView.findViewById(R.id.dwmyTv);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.date.setText(mStr[position]);
        viewHolder.date.setTextColor(color);
        viewHolder.date.setTextSize(15);
        viewHolder.date.setHeight(80);
        viewHolder.date.setPadding(5,5,5,5);
        viewHolder.date.setGravity(Gravity.CENTER_VERTICAL);

        return convertView;
    }

    private class ViewHolder{
        private TextView date;

    }
}
