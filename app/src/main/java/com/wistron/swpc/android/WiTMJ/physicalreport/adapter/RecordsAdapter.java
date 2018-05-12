
package com.wistron.swpc.android.WiTMJ.physicalreport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.CommonUtil;
import com.wistron.swpc.android.WiTMJ.LogUtils;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.bean.Records;

import java.util.Date;
import java.util.List;


/**
 * Created by WH1603043 on 8/4/2016.
 */

public class RecordsAdapter extends BaseAdapter {
    private String TAG = "RecordsAdapter";
    private List<Records> mList;
    private LayoutInflater mInflater;

    public RecordsAdapter(Context context,List<Records> list) {
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
            convertView = mInflater.inflate(R.layout.physical_data_item, null);
            viewHolder.tag = (ImageView)convertView.findViewById(R.id.tagIv);
            viewHolder.startTime = (TextView)convertView.findViewById(R.id.startTimeTv);
            viewHolder.endTime = (TextView)convertView.findViewById(R.id.endTimeTv);
            viewHolder.duration = (TextView)convertView.findViewById(R.id.durationTv);
            viewHolder.distance  = (TextView)convertView.findViewById(R.id.distanceTv);
            viewHolder.calories = (TextView)convertView.findViewById(R.id.caloriesTv);
            viewHolder.avgSpeed = (TextView)convertView.findViewById(R.id.avgSpeedTv);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position%2 == 0){
            convertView.setBackgroundResource(R.color.light_gray);
        }else {
            convertView.setBackgroundResource(R.color.app_background);
        }

        String endTimeTmp = mList.get(position).getEnd_time();
        String endTimeStr ="";
        if (endTimeTmp.endsWith("Z")){
            Date endDate = CommonUtil.getStrToDate(endTimeTmp);
            endTimeStr = CommonUtil.getDateToStr(endDate);
        }else {
            Date startDate = CommonUtil.getBeforeStrToDate(endTimeTmp);
            LogUtils.d(TAG,"startDate=="+startDate);
            endTimeStr = CommonUtil.getDateToStr(startDate);
        }
        viewHolder.endTime.setText(endTimeStr);

        String startTimeTmp = mList.get(position).getStart_time();
        String startTimeStr ="";
        if (startTimeTmp.endsWith("Z")){
            Date startDate = CommonUtil.getStrToDate(startTimeTmp);
            startTimeStr = CommonUtil.getDateToStr(startDate);
        }else {
            Date startDate = CommonUtil.getBeforeStrToDate(startTimeTmp);
            startTimeStr = CommonUtil.getDateToStr(startDate);
        }
        viewHolder.startTime.setText(startTimeStr);
        if (mList.get(position).getTag()!=null){
            if (mList.get(position).getTag() == 1) {
                viewHolder.tag.setImageResource(R.drawable.physical);
            } else if (mList.get(position).getTag() == 2) {
                viewHolder.tag.setImageResource(R.drawable.training_black);
            } else if (mList.get(position).getTag() == 3) {
                viewHolder.tag.setImageResource(R.drawable.competition_black);
            }
        }else {
            viewHolder.tag.setImageResource(R.mipmap.ic_launcher);
        }

        double dur = Double.valueOf(mList.get(position).getDuration());
        dur /= 60;
        int d = (int) dur;
        viewHolder.duration.setText(d+" mins");

        viewHolder.distance.setText(CommonUtil.getDistance(Double.valueOf(mList.get(position).getDistance()))
                .toString()+" km");
        viewHolder.avgSpeed.setText(CommonUtil.getSpeed(Double.valueOf(mList.get(position).getAvg_speed()))
                .toString()+" km/h");
        viewHolder.calories.setText(CommonUtil.getCalories(Double.valueOf(mList.get(position).getDistance()))
                .toString()+" cal");
        return convertView;
    }

    private class ViewHolder{
        private ImageView tag;
        private TextView startTime;
        private TextView endTime;
        private TextView duration;
        private TextView distance;
        private TextView calories;
        private TextView avgSpeed;
    }
}

