package com.wistron.swpc.android.WiTMJ.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Route;

import java.util.List;

public class RouteAdapter extends BaseAdapter {
    private List<Route> mList;

    private LayoutInflater mInflater;

    public RouteAdapter(Context context, List<Route> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        if(mList == null){
            return 0;
        }
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
            convertView = mInflater.inflate(R.layout.nav_route_item, null);
            viewHolder.nav_route1 = (TextView)convertView.findViewById(R.id.nav_route1);
            viewHolder.nav_time1 = (TextView)convertView.findViewById(R.id.nav_time1);
            viewHolder.nav_distance1 = (TextView)convertView.findViewById(R.id.nav_distance1);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nav_route1.setText(mList.get(position).getSummary());
        viewHolder.nav_time1.setText(mList.get(position).getDurationText());
        viewHolder.nav_distance1.setText(mList.get(position).getDistanceText());

        return convertView;
    }

    private class ViewHolder{
        private TextView nav_route1;
        private TextView nav_time1;
        private TextView nav_distance1;

    }
}
