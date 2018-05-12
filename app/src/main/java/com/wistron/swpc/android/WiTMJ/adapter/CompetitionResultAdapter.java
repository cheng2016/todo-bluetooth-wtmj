package com.wistron.swpc.android.WiTMJ.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.bean.Participants;
import com.wistron.swpc.android.WiTMJ.bean.WorkOut;
import com.wistron.swpc.android.WiTMJ.http.response.CompetitionResult;
import com.wistron.swpc.android.WiTMJ.http.response.WorkDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WH1604025 on 2016/5/23.
 */
public class CompetitionResultAdapter extends BaseAdapter{

    private Context context;
    private List<WorkOut> list = new ArrayList<WorkOut>();

    private boolean top =true;

    public CompetitionResultAdapter(Context context, List<WorkOut> list) {
        this.context = context;
        this.list = list;
    }

    public CompetitionResultAdapter(Context context,boolean top) {
        this.context = context;
        this.top = top;
    }


    public void update(List<WorkOut> list){
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_competition_result, null);
            holder.dateTv = (TextView) convertView.findViewById(R.id.dateTv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
            holder.contentTv = (TextView) convertView.findViewById(R.id.contentTv);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        if(position%2==0){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.result_bg_press));
        }else{
            convertView.setBackgroundColor(context.getResources().getColor(R.color.app_background));
        }

        if(list.size()>0){
            WorkOut bean = list.get(position);
            if (!TextUtils.isEmpty(bean.getEnd_date())) {
                if (bean.getEnd_date().split("T").length > 1) {
                    String date = bean.getEnd_date().split("T")[0];
                    String time = bean.getEnd_date().split("T")[1].substring(0,8);
                    holder.dateTv.setText(date);
                    holder.timeTv.setText(time);

                }else if(bean.getEnd_date().split(" ").length>1){
                    String date = bean.getEnd_date().split(" ")[0];
                    String time = bean.getEnd_date().split(" ")[1];
                    holder.dateTv.setText(date);
                    holder.timeTv.setText(time);
                }
            }
            if(top){
                holder.contentTv.setText(bean.getName());
            }else{
                StringBuilder sb = new StringBuilder();
                for (Participants p:bean.getParticipants()) {
                    sb.append(p.getUsername());
                }
                holder.contentTv.setText(sb.toString());
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView dateTv,timeTv,contentTv;
    }
}
