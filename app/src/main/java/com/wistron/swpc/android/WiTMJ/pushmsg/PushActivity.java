package com.wistron.swpc.android.WiTMJ.pushmsg;

import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.bean.Invitation;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by WH1603043 on 20/4/2016.
 */
public class PushActivity extends BaseActivity implements View.OnClickListener{
    private final String TAG = "PushActivity";
    private PushAdapter mPushAdapter;
    private List<Invitation> mInvitations = new ArrayList<Invitation>();
    private ListView mPushLv;
    private ActionBar actionBar;
    private TextView titleTv;
    private PercentRelativeLayout backBtn;
    public Integer mUnReadNum;
    private TmjConnection connection;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.app_actionbar);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        initView();
        initData();
        initEvent();
    }

    @Override
    public void initView() {
        connection = TmjClient.create();

        mPushLv = (ListView) findViewById(R.id.pushLv);
        titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.titleTv);
        backBtn = (PercentRelativeLayout) actionBar.getCustomView().findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void initEvent() {

    }

    public void initData() {
        if (getIntent()!=null){
            mInvitations = (List<Invitation>) getIntent().getSerializableExtra("invitationList");
        }
        titleTv.setText("Push");
        if (mInvitations.size()>0){
            mUnReadNum = mInvitations.size();
        }else {
            mUnReadNum = 0;
        }
        mPushAdapter = new PushAdapter(PushActivity.this,mInvitations);
        mPushLv.setAdapter(mPushAdapter);
        mPushAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.backBtn:
                Intent i = new Intent();
                L.i("mUnReadNum size = "+mUnReadNum);
                /*---zlb----*/
                i.putExtra("unReadNum",mUnReadNum);
                setResult(Constants.PUSH_RESULT_CODE,i);
                finish();
                break;
        }
    }
}

  class PushAdapter extends BaseAdapter {
    private List<Invitation> mList;
    private LayoutInflater mInflater;
    private int index = 0;
    private PushActivity mcontext;
    public PushAdapter(PushActivity context, List<Invitation> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mcontext =context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.activity_push_item, null);
            viewHolder.pushTv = (TextView)convertView.findViewById(R.id.pushTv);
            viewHolder.okBtn = (Button)convertView.findViewById(R.id.pushOkBtn);
            viewHolder.denyBtn = (Button)convertView.findViewById(R.id.pushDenyBtn);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.pushTv.setText(mList.get(position).getHostname()+" invite you to join "+mList.get(position).getName());

        viewHolder.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token= PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN,"");
                TmjConnection connection  = TmjClient.create();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("invitation",Constants.INVITATION_OK);

                Call<ResponseBody> call = connection.putInvitation(token,mList.get(position).getRecord_id(),jsonObject);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            mList.remove(position);
                            notifyDataSetChanged();
                            mcontext.mUnReadNum  = mcontext.mUnReadNum-1;
                        }else{
                            System.out.println("== "+response.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        System.out.println("onFailure== "+t.getMessage());
                    }
                });


            }
        });
        viewHolder.denyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token= PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN,"");
                TmjConnection connection  = TmjClient.create();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("invitation",Constants.INVITATION_DENY);

                Call<ResponseBody> call = connection.putInvitation(token,mList.get(position).getRecord_id(),jsonObject);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            mList.remove(index);
                            notifyDataSetChanged();
                            mcontext.mUnReadNum  = mcontext.mUnReadNum-1;
                        }else{
                            System.out.println("== "+response.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        System.out.println("onFailure== "+t.getMessage());
                    }
                });
            }
        });

        return convertView;
    }

    private class ViewHolder{
        private TextView pushTv;
        private Button okBtn;
        private Button denyBtn;

    }

}
