package com.wistron.swpc.android.WiTMJ.physicalreport;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.wistron.swpc.android.WiTMJ.ApplicationHolder;
import com.wistron.swpc.android.WiTMJ.CommonUtil;
import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.LogUtils;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.ScreenShoot;
import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.bean.Records;
import com.wistron.swpc.android.WiTMJ.dao.RecordsDao;
import com.wistron.swpc.android.WiTMJ.util.FB;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Route;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class CompetitionDetailActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener{
    private static final String TAG = "CompetitionDetailActivity";
    private ActionBar actionBar;
    private TextView titleTv;
    private PercentRelativeLayout backBtn;
    private TextView mSpdTv;
    private TextView mDisTv;
    private TextView mDurHrsTv;
    private TextView mDurMinsTv;
    private TextView mCalTv;
    private RecordsDao recordsDao;

    private String dur[] = new String[2];
    private String dis;
    private String cal;
    private String spd;
    private String startTimeStr = "";

    private double durDL;
    private double disDL;
    private double calDL;
    private double spdDL;

    String recordId;
    public GoogleMap map;
    public MapView mapView;
    public Route route;
    public MapFragment mapFragment;
    private ImageView competition_iv_fb;
    private ImageView competition_iv_line;
    private LoginManager manager;
    private CallbackManager callbackManager;
    private boolean fb_flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        fb_flag = false;
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("publish_actions");
        //this loginManager helps you eliminate adding a LoginButton to your UI
        manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, permissionNeeds);
        manager.registerCallback(callbackManager, null);*/
        setContentView(R.layout.activity_competition_detail);
        initView();
        initData();
        initEvent();
    }

    @Override
    public void initView() {
        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.app_actionbar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.titleTv);
        backBtn = (PercentRelativeLayout) actionBar.getCustomView().findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        competition_iv_fb = (ImageView) findViewById(R.id.competition_iv_fb);
        competition_iv_line = (ImageView) findViewById(R.id.competition_iv_line);
        mSpdTv = (TextView) findViewById(R.id.spdDetailTv);
        mDisTv = (TextView) findViewById(R.id.disDetailTv);
        mDurHrsTv = (TextView) findViewById(R.id.durHrsDetailTv);
        mDurMinsTv = (TextView) findViewById(R.id.durMinsDetailTv);
        mCalTv = (TextView) findViewById(R.id.calDetailTv);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.cmpte_dtal_map);

    }

    @Override
    public void initEvent() {
        competition_iv_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bitmap bitmap = ScreenShoot.takeScreenShoot(CompetitionDetailActivity.this);
                if (AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")) {
                    FB.sharePhotoToFacebook(bitmap);
                } else {
                    if (fb_flag) {
                        FB.sharePhotoToFacebook(bitmap);
                    } else {
                        Toast.makeText(CompetitionDetailActivity.this, "No facebook publish Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        competition_iv_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ScreenShoot.takeScreenShoot(CompetitionDetailActivity.this);
                shareLine(bitmap);
            }
        });

    }

    @Override
    public void initData() {
        if (getIntent().getExtras() != null) {
            Intent i = getIntent();

            if (i.getIntExtra(Constants.TAG_INSTANT_TO_DETAIL, 0) == Constants.TAG_INSTANT) {
                Bundle bundle = i.getExtras();/*
                String startTimeTmp = bundle.getString("startTime");
                Date startDate = CommonUtil.getBeforeStrToDate(startTimeTmp);
                startTimeStr = CommonUtil.getDetailActyFormat(startDate);*/
                String startTimeTmp = bundle.getString("startTime");
                String startTimeStr ="";
                if (startTimeTmp.endsWith("Z")){
                    Date startDate = CommonUtil.getStrToDate(startTimeTmp);
                    startTimeStr = CommonUtil.getDetailActyFormat(startDate);
                }else {
                    Date startDate = CommonUtil.getBeforeStrToDate(startTimeTmp);
                    startTimeStr = CommonUtil.getDetailActyFormat(startDate);
                }
                durDL = Double.valueOf(bundle.getString("duration"));
                dur = CommonUtil.getDuration(durDL);

                disDL = Double.valueOf(bundle.getString("distance"));
                disDL = CommonUtil.getDistance(disDL);
                dis = String.valueOf(disDL);

                /*存放的calories的值就是distance*/
                calDL = Double.valueOf(bundle.getString("calories"));
                cal = String.valueOf(CommonUtil.getCalories(calDL));

                spdDL = Double.valueOf(bundle.getString("speed"));
                spd = String.valueOf(CommonUtil.getSpeed(spdDL));

                titleTv.setText(startTimeStr);
                mDurHrsTv.setText(dur[0]);
                mDurMinsTv.setText(dur[1]);
                mDisTv.setText(dis);
                mCalTv.setText(cal);
                mSpdTv.setText(spd);

            } else if(i.getIntExtra("TAG_INSTANT_CMPLETE_TO_DETAIL", 0) == Constants.TAG_INSTANT_CMPLETE_TO_DETAIL) {
                Bundle bundle = i.getExtras();
                recordId = bundle.getString("recordId");
                recordsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getRecordsDao();
                new AsyncTask<String, Void, Records>() {
                    @Override
                    protected Records doInBackground(String... recordId) {
//                        List<Records> records = recordsDao.queryBuilder().where(RecordsDao.Properties.Record_id.eq(recordId[0]))
//                                .orderDesc(RecordsDao.Properties.Start_time)
//                                .build().list();
                        List<Records> records = recordsDao.queryBuilder().where(RecordsDao.Properties.Record_id.eq(recordId[0]))
                                .build().list();
                        return records.get(0);
                    }

                    @Override
                    protected void onPostExecute(Records records) {
                        if (records != null) {
                            try {
                                if (records.getRoutes() != null) {
                                    JSONObject json = new JSONObject(records.getRoutes());
                                    route = CommonUtil.parseRoute(json);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            LogUtils.i(TAG, "route = CommonUtil.parseRoute(json)==" + route);

                            String startTimeTmp = records.getStart_time();
                            Date startDate = CommonUtil.getBeforeStrToDate(startTimeTmp);
                            startTimeStr = CommonUtil.getDetailActyFormat(startDate);

                            durDL = Double.valueOf(records.getDuration());
                            dur = CommonUtil.getDuration(durDL);

                            disDL = Double.valueOf(records.getDistance());
                            disDL = CommonUtil.getDistance(disDL);
                            dis = String.valueOf(disDL);

                            /*cmplte后存放的calories的值就是distance*/
                            calDL = Double.valueOf(records.getCalories());
                            /*从distance直接换算为calories*/
                            cal = String.valueOf(CommonUtil.getCalories(calDL));

                            spdDL = Double.valueOf(records.getAvg_speed());
                            /*需将m/s转化为km/h*/
                            spd = String.valueOf(CommonUtil.getSpeed(spdDL));

                            mapFragment.getMapAsync(CompetitionDetailActivity.this);

                            titleTv.setText(startTimeStr);
                            mDurHrsTv.setText(dur[0]);
                            mDurMinsTv.setText(dur[1]);
                            mDisTv.setText(dis);
                            mCalTv.setText(cal);
                            mSpdTv.setText(spd);

                        }
                    }
                }.execute(recordId);
            }
        }



    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.backBtn:
                setResult(Activity.RESULT_OK);
                finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LogUtils.i(TAG, "onMapReady..");
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
        try {
            if (route != null) {
                CommonUtil.drawOneLine(CompetitionDetailActivity.this, map, route);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        this.startActivity(Intent.createChooser(shareIntent, "CompetitionDetailActivity结果分享"));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
