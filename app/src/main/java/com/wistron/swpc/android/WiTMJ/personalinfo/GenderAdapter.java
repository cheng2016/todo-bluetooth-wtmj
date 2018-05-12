package com.wistron.swpc.android.WiTMJ.personalinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.R;

import java.util.List;

/**
 * Created by Administrator on 2016/4/22.
 */
public class GenderAdapter extends BaseAdapter {
    private List<String> mList;
    private LayoutInflater mInflater;

    public GenderAdapter(Context context, List<String> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
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
        viewHolder.date.setText(mList.get(position));
        return convertView;
    }

    private class ViewHolder{
        private TextView date;

    }
}

