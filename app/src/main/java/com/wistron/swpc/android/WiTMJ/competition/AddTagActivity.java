package com.wistron.swpc.android.WiTMJ.competition;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wistron.swpc.android.WiTMJ.LoginActivity;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.bean.Participants;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.http.HttpImpl;
import com.wistron.swpc.android.WiTMJ.http.response.GetUserResponse;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;
import com.wistron.swpc.android.WiTMJ.widget.FlowLayout;
import com.wistron.swpc.android.WiTMJ.widget.FlowTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WH1604025 on 2016/4/25.
 */
public class AddTagActivity extends BaseActivity {

    private ActionBar actionBar;
    private TextView titleTv;
    private PercentRelativeLayout backBtn;

    private FlowLayout fowLayout;

    private LinearLayout searchBtn;

    private ImageView searchImg;

    private AutoCompleteTextView keywordEdit;

    private ListView userListView;

    private UserAdapter userAdapter;

    private List<Participants> participantsList = new ArrayList<Participants>();

    private List<GetUserResponse> responseList = new ArrayList<GetUserResponse>();

    private String keyword;

    private boolean isClear = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.app_actionbar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void initView() {
        titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.titleTv);
        titleTv.setText("Add");
        backBtn = (PercentRelativeLayout) actionBar.getCustomView().findViewById(R.id.backBtn);

        fowLayout = (FlowLayout) findViewById(R.id.fowLayout);

        searchBtn = (LinearLayout) findViewById(R.id.searchBtn);
        searchImg = (ImageView) findViewById(R.id.searchImg);
        userListView = (ListView) findViewById(R.id.userListView);
        userAdapter = new UserAdapter();
        userListView.setAdapter(userAdapter);

        keywordEdit = (AutoCompleteTextView) findViewById(R.id.keywordEdit);
    }

//    CharSequence temp;
    @Override
    public void initEvent() {
        /*keywordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
                keywordEdit.setHint("");
                if(temp.length()>0){
                    searchImg.setBackgroundResource(R.drawable.cancel2);

                    keyword = keywordEdit.getText().toString();
                    String token = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, "");
                    HttpImpl.getInstance(AddTagActivity.this).getUsers(handler, token, keyword, "");
                }else{
                    searchImg.setBackgroundResource(R.drawable.search);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int before,
                                          int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK,new Intent().putExtra("participantsList",(Serializable) participantsList));
                finish();
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard(v);
                keyword = keywordEdit.getText().toString();
                if(!isClear){
                    String token = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, "");
                    HttpImpl.getInstance(AddTagActivity.this).getUsers(handler, token, keyword, "");
                    searchImg.setBackgroundResource(R.drawable.cancel2);
                    isClear = true;
                }else{
                    keywordEdit.setText("");
                    responseList.clear();
                    userAdapter.notifyDataSetChanged();
                    searchImg.setBackgroundResource(R.drawable.search);
                    isClear = false;
                }
            }
        });
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Participants participants = new Participants();
                participants.setUser_id(responseList.get(position).getId());
                participants.setUsername(responseList.get(position).getUsername());
                fowLayout.getParticipantsList().add(participants);
                FlowTextView tv = new FlowTextView(AddTagActivity.this,true,fowLayout);
                tv.setText(participants.getUsername());
                fowLayout.addView(tv);
                tv.setOnClickListener();
            }
        });
    }

    @Override
    public void initData() {
        participantsList = (List<Participants>) getIntent().getExtras().get("participantsList");
        fowLayout.setParticipantsList(participantsList);
        for (int i = 0; i < participantsList.size(); i++) {
            FlowTextView tv = new FlowTextView(this,true,fowLayout);
            tv.setText(participantsList.get(i).getUsername());
            fowLayout.setParticipantsList(participantsList);
            fowLayout.addView(tv);
            tv.setOnClickListener();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CodeType.SUCCESS:
                    if (msg.arg1 == CodeType.GETUSER) {
                        responseList = (List<GetUserResponse>) msg.obj;
                        if(responseList.size()==0){
                            T.showShort("No user found！");
                        }
                        userAdapter.notifyDataSetChanged();
                    }
                    break;
                case CodeType.TOKENERROR:
                    hideProgress();
                    showCustomDialog("Erro Signing In", "Your account has expired or other places to log in, please re verify user identity.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent().setClass(AddTagActivity.this, LoginActivity.class));
                            finish();
                        }
                    }, false);
                    break;
                case CodeType.NETWORN_NONE | CodeType.FAILED:
                    hideProgress();
                    break;
            }
        }
    };

    public class UserAdapter extends BaseAdapter{

        public UserAdapter() {
        }

        @Override
        public int getCount() {
            return responseList.size();
        }

        @Override
        public Object getItem(int position) {
            return responseList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(AddTagActivity.this).inflate(R.layout.item_add, parent, false);
                holder = new ViewHolder();

                holder.headImg = (ImageView) convertView.findViewById(R.id.headImg);
                holder.userNameTv = (TextView) convertView.findViewById(R.id.userNameTv);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            if(responseList!=null)
            {
                GetUserResponse response = responseList.get(position);
                setViewText(holder.userNameTv,response.getUsername());

                DisplayImageOptions options = new DisplayImageOptions.Builder()//加载头像
                        .showImageForEmptyUri(R.drawable.ppl_pink) // resource or drawable
                        .showImageOnFail(R.drawable.ppl_pink).build();

                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(response.getImage_url(),holder.headImg,options);
            }

            return convertView;
        }
    }

    public class ViewHolder{
        ImageView headImg;
        TextView userNameTv;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK,new Intent().putExtra("participantsList",(Serializable) participantsList));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
