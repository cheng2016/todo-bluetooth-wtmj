package com.wistron.swpc.android.WiTMJ.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.LogUtils;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Segment;

import java.util.List;

public class LegAdapter extends BaseAdapter {
    private List<Segment> mList;

    private LayoutInflater mInflater;

    public LegAdapter(Context context, List<Segment> list) {
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
            convertView = mInflater.inflate(R.layout.nav_leg_item, null);
            viewHolder.nav_intru = (TextView)convertView.findViewById(R.id.nav_intru);
            viewHolder.nav_send = (TextView)convertView.findViewById(R.id.nav_send);
            viewHolder.nav_turn = (TextView)convertView.findViewById(R.id.nav_turn);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position%2 == 0){
            convertView.setBackgroundResource(R.color.light_gray);
        }else {
            convertView.setBackgroundResource(R.color.app_background);
        }
        viewHolder.nav_intru.setText(mList.get(position).getInstruction());

       if(mList.get(position).getManeuver()!=null){
           viewHolder.nav_turn.setText(mList.get(position).getManeuver());
           if(mList.get(position).isSend()){
               viewHolder.nav_send.setText("send");
           }else{//add by wr
               viewHolder.nav_send.setText("");
           }
       }else{//add by wr
           viewHolder.nav_send.setText("");
           viewHolder.nav_turn.setText("");
       }


        return convertView;
    }

    private class ViewHolder{
        private TextView nav_intru;
        private TextView nav_send;
        private TextView nav_turn;

    }

    //局部刷新某一个item    add by wr
    public void updateLegItem(int position, ListView listView){
//        LogUtils.e("updateLegItem","updateLegItem====="+position);
        int visibleFirstPosi =  listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if(position >= visibleFirstPosi && position <= visibleLastPosi){
            View view = listView.getChildAt(position - visibleFirstPosi);
            ViewHolder holder = (ViewHolder) view.getTag();
            if (mList.get(position).isSend()) {
                holder.nav_send.setText("send");
            } else {
                holder.nav_send.setText("");
            }
        }
    }

}
