package com.wistron.swpc.android.WiTMJ.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.wistron.swpc.android.WiTMJ.LoginActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.ScreenShoot;
import com.wistron.swpc.android.WiTMJ.adapter.ResultExpandableAdapter;
import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.http.HttpImpl;
import com.wistron.swpc.android.WiTMJ.http.response.WorkDetailResponse;
import com.wistron.swpc.android.WiTMJ.util.FB;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by WH1604025 on 2016/5/20.
 */
public class CompetitionResultDetailActivity extends BaseActivity {
    private ActionBar actionBar;
    private TextView titleTv,workNameTv;

    private ResultExpandableAdapter adapter;
    private PercentRelativeLayout backBtn;

    private ImageView competition_result_iv_fb;
    private ImageView competition_result_iv_line;

    ExpandableListView expandableListView;
    private LoginManager manager;
    private CallbackManager callbackManager;
    private boolean fb_flag = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fb_flag = false;
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("publish_actions");
        //this loginManager helps you eliminate adding a LoginButton to your UI
        manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, permissionNeeds);
        manager.registerCallback(callbackManager, null);
        setContentView(R.layout.activity_competition_result_detail);
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
        workNameTv = (TextView) findViewById(R.id.workNameTv);

        titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.titleTv);
        titleTv.setText("2016/05/01 05:10");
        backBtn = (PercentRelativeLayout) actionBar.getCustomView().findViewById(R.id.backBtn);
        competition_result_iv_fb = (ImageView) findViewById(R.id.competition_result_iv_fb);
        competition_result_iv_line = (ImageView) findViewById(R.id.competition_result_iv_line);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        adapter = new ResultExpandableAdapter(this);
        expandableListView.setAdapter(adapter);
    }

    @Override
    public void initEvent() {
        competition_result_iv_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bitmap bitmap = ScreenShoot.takeScreenShoot(CompetitionResultDetailActivity.this);
                if (AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")) {
                    FB.sharePhotoToFacebook(bitmap);
                } else {
                    if (fb_flag) {
                        FB.sharePhotoToFacebook(bitmap);
                    } else {
                        Toast.makeText(CompetitionResultDetailActivity.this, "No facebook publish Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        competition_result_iv_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ScreenShoot.takeScreenShoot(CompetitionResultDetailActivity.this);
                shareLine(bitmap);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        if (getIntent().getExtras() != null) {
            String work_id = getIntent().getExtras().getString("work_id", "");
            String accessToken = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, "");
            showProgress("wait...");
            HttpImpl.getInstance(CompetitionResultDetailActivity.this).getWorkDetail(handler, accessToken, work_id);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgress();
            switch (msg.what) {
                case CodeType.SUCCESS:
                    if (msg.arg1 == CodeType.WORKDETAIL) {
                        WorkDetailResponse response = (WorkDetailResponse) msg.obj;
                        setViewText(workNameTv,response.getName());
                        if (!TextUtils.isEmpty(response.getEnd_date())) {
                            if (response.getEnd_date().split("T").length > 0) {
                                String date = response.getEnd_date().split("T")[0];
                                String time = response.getEnd_date().split("T")[1].substring(0,8);
                                setViewText(titleTv,date+" "+time);
                            }
                        }
                        adapter.updateExpandebleList(response.getParticipants());
                    }
                    break;
                case CodeType.TOKENERROR:
                    showCustomDialog("Erro Signing In", "Your account has expired or other places to log in, please re verify user identity.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent().setClass(CompetitionResultDetailActivity.this, LoginActivity.class));
                            finish();
                        }
                    }, false);
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
    public void shareLine(Bitmap bitmap) {
        ComponentName cn = new ComponentName("jp.naver.line.android"
                , "jp.naver.line.android.activity.selectchat.SelectChatActivity");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, null, null));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        shareIntent.setComponent(cn);
        this.startActivity(Intent.createChooser(shareIntent, "CompetitionResultDetailActivity结果分享"));
    }

}
