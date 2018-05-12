package com.wistron.swpc.android.WiTMJ.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.http.response.ItemResult;
import com.wistron.swpc.android.WiTMJ.http.response.GroupResult;
import com.wistron.swpc.android.WiTMJ.http.response.WorkDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WH1604025 on 2016/5/23.
 */
public class ResultExpandableAdapter extends BaseExpandableListAdapter{

    List<GroupResult> groupList  = new ArrayList<GroupResult>();

    List<ArrayList<ItemResult>> itemList = new ArrayList<ArrayList<ItemResult>>();

    List<WorkDetailResponse.Participants> participantsList = new ArrayList<WorkDetailResponse.Participants>();

    private Context context;

    public ResultExpandableAdapter(Context context)
    {
        this.context = context;
/*        for(int i =0;i< 3; i++){
            GroupResult group = new GroupResult("0"+i ,"wendy","3:30 compelete");
            groupList.add(group);

            ArrayList<ItemResult> itemGroup = new ArrayList<ItemResult>();

            ItemResult item = new ItemResult("15","20","60","400");
            itemGroup.add(item);
            itemList.add(itemGroup);
        }*/
    }

    public void updateExpandebleList(List<WorkDetailResponse.Participants> participantsList){
        this.participantsList = participantsList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return participantsList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return participantsList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return participantsList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_competition_result_detail, null);
            groupHolder = new GroupHolder();
            groupHolder.numberTv = (TextView) convertView.findViewById(R.id.numberTv);
            groupHolder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
            groupHolder.contentTv = (TextView) convertView.findViewById(R.id.contentTv);
            convertView.setTag(groupHolder);
        }
        else
        {
            groupHolder = (GroupHolder)convertView.getTag();
        }

        if(groupPosition%2==0){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.result_bg_press));
        }else{
            convertView.setBackgroundColor(context.getResources().getColor(R.color.app_background));
        }

/*        if(groupList!=null){
            if(groupList.size()>0){
                GroupResult bean = groupList.get(groupPosition);
                groupHolder.numberTv.setText(bean.getNumber());
                groupHolder.nameTv.setText(bean.getName());
                groupHolder.contentTv.setText(bean.getContent());
            }
        }*/

        if(participantsList.size()>0){
            WorkDetailResponse.Participants bean = participantsList.get(groupPosition);
            groupHolder.numberTv.setText(bean.getRank()+"");
            groupHolder.nameTv.setText(bean.getUsername());
            groupHolder.contentTv.setText(bean.getEnd_time() + " completes");
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_result_chart, null);

            childHolder = new ChildHolder();
            childHolder.speedTv = (TextView) convertView.findViewById(R.id.speedTv);
            childHolder.distanceTv = (TextView) convertView.findViewById(R.id.distanceTv);
            childHolder.durationHrTv = (TextView) convertView.findViewById(R.id.durationHrTv);
            childHolder.durationMinTv = (TextView) convertView.findViewById(R.id.durationMinTv);
            childHolder.caloriesTv = (TextView) convertView.findViewById(R.id.caloriesTv);
            convertView.setTag(childHolder);
        }
        else
        {
            childHolder = (ChildHolder)convertView.getTag();
        }

/*        if(itemList.get(groupPosition).get(childPosition)!=null){
            if(itemList.get(groupPosition).size()>0){
                ItemResult bean = itemList.get(groupPosition).get(childPosition);
                childHolder.speedTv.setText(bean.getSpeed());
                childHolder.distanceTv.setText(bean.getDistance());
                childHolder.durationHrTv.setText(bean.getDuration());
                childHolder.caloriesTv.setText(bean.getCalories());
            }
        }*/
        if(participantsList.size()>0){
            setViewText(childHolder.speedTv,participantsList.get(groupPosition).getAvg_speed()+"");
            setViewText(childHolder.distanceTv,participantsList.get(groupPosition).getDistance()+"");
            setViewText(childHolder.durationHrTv,participantsList.get(groupPosition).getDuration()+"");
            setViewText(childHolder.caloriesTv,participantsList.get(groupPosition).getCalories()+"");
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder{
        TextView numberTv,nameTv,contentTv;
    }

    class ChildHolder{
        TextView speedTv,distanceTv,durationHrTv,durationMinTv,caloriesTv;
    }

    public void setViewText(TextView view,String text){
        if(TextUtils.isEmpty(text)){
            view.setText("");
        }else {
            view.setText(text);
        }
    }
}
