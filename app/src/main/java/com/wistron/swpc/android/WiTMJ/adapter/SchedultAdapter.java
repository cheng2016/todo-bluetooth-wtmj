package com.wistron.swpc.android.WiTMJ.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.bean.Participants;
import com.wistron.swpc.android.WiTMJ.bean.WorkOut;
import com.wistron.swpc.android.WiTMJ.competition.HostActivity;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.http.HttpImpl;
import com.wistron.swpc.android.WiTMJ.http.request.InvitationRequest;
import com.wistron.swpc.android.WiTMJ.http.response.Records;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.util.DateUtil;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;

import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WH1604025 on 2016/4/27.
 */
public class SchedultAdapter extends BaseAdapter{
    private List<WorkOut> list = new ArrayList<WorkOut>();
    private Fragment context;


    public SchedultAdapter(List<WorkOut> list, Fragment context) {
        this.list = list;
        this.context = context;
    }

    public SchedultAdapter(Fragment context) {
        this.context = context;
   }

    public void updata(List<WorkOut> list){
        this.list = list;
        this.notifyDataSetChanged();
    }

    public void add(WorkOut workOut){
        list.add(workOut);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context.getActivity()).inflate(R.layout.item_host, parent, false);
            holder = new ViewHolder();
            holder.mainView = (RelativeLayout) convertView.findViewById(R.id.mainView);

            holder.contentTitleTv = (TextView) convertView.findViewById(R.id.contentTitleTv);
            holder.contentDateTv = (TextView) convertView.findViewById(R.id.contentDateTv);
            holder.contentTimeTv = (TextView) convertView.findViewById(R.id.contentTimeTv);
            holder.contentDurationTv = (TextView) convertView.findViewById(R.id.contentDurationTv);
            holder.contentNumTv = (TextView) convertView.findViewById(R.id.contentNumTv);
            holder.editImg = (ImageView) convertView.findViewById(R.id.editImg);
            holder.editImg = (ImageView) convertView.findViewById(R.id.editImg);
            holder.buttonLayout = (LinearLayout) convertView.findViewById(R.id.buttonLayout);
            holder.okBtn = (Button) convertView.findViewById(R.id.okBtn);
            holder.cancelBtn = (Button) convertView.findViewById(R.id.cancelBtn);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(list!=null) {
            WorkOut bean = list.get(position);
            setViewText(holder.contentTitleTv, bean.getName());

            String start_date_detail,end_date_detail;

            L .i("time",bean.getStart_date() +"\n" + bean.getEnd_date());

            start_date_detail = DateUtil.convertServerDate(bean.getStart_date());
            end_date_detail = DateUtil.convertServerDate(bean.getEnd_date());

            L .i("time convert",start_date_detail +"\n" + end_date_detail);

            if(start_date_detail.split("T").length > 1 && end_date_detail.split("T").length > 1){
                String startDate = start_date_detail.split("T")[0];
                String endDate = end_date_detail.split("T")[0];

                if(startDate.equals(endDate)){
                    setViewText(holder.contentDateTv,startDate);//设定日期
                }else{
                    setViewText(holder.contentDateTv,startDate+" ~ "+ endDate);//设定日期
                }

                String startTime = start_date_detail.split("T")[1];
                String endTime = end_date_detail.split("T")[1];

                setViewText(holder.contentTimeTv,startTime.substring(0,5) + " ~ " +endTime.substring(0,5));//设定经历的开始时间和结束时间

                String duration = DateUtil.getDistanceTime(start_date_detail,end_date_detail);
                setViewText(holder.contentDurationTv,"( " + duration +" )");//设定经历的时间
            }

          if(bean.getHost().getHost_id().equals(TmjApplication.getInstance().getUserid())){
                holder.editImg.setVisibility(View.VISIBLE);
            }else{
                holder.editImg.setVisibility(View.GONE);
                  for (Participants p:bean.getParticipants()) {
                      if(p.getUser_id().equals(TmjApplication.getInstance().getUserid())){
                          if(p.getInvitation()!=null){
                              if(Integer.parseInt(p.getInvitation())==0){
                                  holder.buttonLayout.setVisibility(View.VISIBLE);
                              }else{
                                  holder.buttonLayout.setVisibility(View.GONE);
                              }
                          }
                      }
                  }
            }

            setViewText(holder.contentNumTv,bean.getParticipants().size() + " going");

            holder.editImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOperaterListener.editClickListener(position);
                }
            });

            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOperaterListener.showClickListener(position);
                }
            });

            holder.okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOperaterListener.okClickListener(position);
                }
            });
            holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOperaterListener.cancelClickListener(position);
                }
            });
        }
        return convertView;
    }

    public void setViewText(TextView view,String content){
        String text = TextUtils.isEmpty(content)||content.equals("null")?"":content;
        view.setText(text);
    }

    class ViewHolder{
        TextView contentTitleTv,contentDateTv,contentTimeTv,contentDurationTv,contentNumTv;
        ImageView editImg;
        LinearLayout buttonLayout;
        Button okBtn,cancelBtn;
        RelativeLayout mainView;
    }

    //接口类，代替listview onItemClick事件
    public OperaterListener mOperaterListener;
    public interface OperaterListener{
        public void showClickListener(int position);

        public void editClickListener(int position);

        public void okClickListener(int position);

        public void cancelClickListener(int position);
    }
    public void setOperaterListener(OperaterListener listener)
    {
        this.mOperaterListener = listener;
    }
}
