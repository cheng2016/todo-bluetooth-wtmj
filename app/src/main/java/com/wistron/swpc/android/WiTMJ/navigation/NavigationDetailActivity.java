package com.wistron.swpc.android.WiTMJ.navigation;

import android.Manifest;
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
import com.google.android.gms.maps.model.Polyline;
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
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Route;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class NavigationDetailActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {
    private ActionBar actionBar;
    private TextView titleTv;
    private PercentRelativeLayout backBtn;
    private TextView mSpdTv;
    private TextView mDisTv;
    private TextView mDurHrsTv;
    private TextView mDurMinsTv;
    private TextView mCalTv;
    private RecordsDao recordsDao;
    private String recordId;
    public GoogleMap map;
    public MapView mapView;
    private ImageView physical_iv_fb;
    private ImageView physical_iv_line;
    public Route route;
    public MapFragment mapFragment;
    private LoginManager manager;
    private CallbackManager callbackManager;
    private boolean fb_flag = false;
    private PreferencesUtil mPreferencesUtil;
    public List<Polyline> polylines;

    private String dur[] = new String[2];
    private String dis;
    private String cal;
    private String spd;

    private double durDL;
    private double disDL;
    private double calDL;
    private double spdDL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fb_flag = false;
//        FacebookSdk.sdkInitialize(this);
//        callbackManager = CallbackManager.Factory.create();
//        List<String> permissionNeeds = Arrays.asList("publish_actions");
        //this loginManager helps you eliminate adding a LoginButton to your UI
//        manager = LoginManager.getInstance();
//        manager.logInWithPublishPermissions(this, permissionNeeds);
//        manager.registerCallback(callbackManager, null);
        setContentView(R.layout.activity_physical_detail);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void initView() {
        mPreferencesUtil = new PreferencesUtil(this,
                PreferenceConstants.HOME_CYCLING_TABLE);
        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.app_actionbar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.titleTv);
        backBtn = (PercentRelativeLayout) actionBar.getCustomView().findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        physical_iv_fb = (ImageView) findViewById(R.id.physical_iv_fb);
        physical_iv_line = (ImageView) findViewById(R.id.physical_iv_line);
        mSpdTv = (TextView) findViewById(R.id.spdDetailTv);
        mDisTv = (TextView) findViewById(R.id.disDetailTv);
        mDurHrsTv = (TextView) findViewById(R.id.durHrsDetailTv);
        mDurMinsTv = (TextView) findViewById(R.id.durMinsDetailTv);
        mCalTv = (TextView) findViewById(R.id.calDetailTv);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.detail_map);

       /* mapView = (MapView) findViewById(R.id.mapview);
        mapView.getMapAsync(this);*/
    }

    @Override
    public void initEvent() {
        physical_iv_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bitmap bitmap = ScreenShoot.takeScreenShoot(NavigationDetailActivity.this);
                if (AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")) {
                    FB.sharePhotoToFacebook(bitmap);
                } else {
                    if (fb_flag) {
                        FB.sharePhotoToFacebook(bitmap);
                    } else {
                        Toast.makeText(NavigationDetailActivity.this, "No facebook publish Permission", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        physical_iv_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ScreenShoot.takeScreenShoot(NavigationDetailActivity.this);
                shareLine(bitmap);
            }
        });

    }

    @Override
    public void initData() {
        if (getIntent().getExtras() != null) {
            Intent i = getIntent();
            if (i.getIntExtra(Constants.TAG_NAVIGATION_TO_DETAIL, 0) == Constants.TAG_NAVIGATION) {
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

            }
            if (i.getIntExtra("TAG_NAVIGATION_CMPLETE_TO_DETAIL", 0) == Constants.TAG_NAVIGATION_CMPLETE_TO_DETAIL) {
                Bundle bundle = i.getExtras();
                recordId = bundle.getString("recordId");
                recordsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getRecordsDao();

                new AsyncTask<String, Void, Records>() {
                    @Override
                    protected Records doInBackground(String... recordId) {
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
                            titleTv.setText(records.getStart_time());
                            Double duration = Double.valueOf(records.getDuration());
                            String dur[] = CommonUtil.getDuration(duration);
                            mDurHrsTv.setText(dur[0] + "");
                            mDurMinsTv.setText(dur[1] + "");
                            //titleTv.setText(bundle.getString("date"));
                            String spd = String.valueOf(records.getAvg_speed());
                            String strSpd = String.valueOf(CommonUtil.getSpeed(Double.valueOf(spd)));
                            String dis = String.valueOf(records.getDistance());
                            String strDis = String.valueOf(CommonUtil.getDistance(Double.valueOf(dis)));
                            String cal = String.valueOf(records.getCalories());
                            String strCal = String.valueOf(CommonUtil.getCalories(Double.valueOf(cal)));
                            mSpdTv.setText(strSpd);
                            mDisTv.setText(strDis);
                            mCalTv.setText(strCal);
                            mapFragment.getMapAsync(NavigationDetailActivity.this);
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
                finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LogUtils.e("", "onMapReady..");
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        try {
            if (route != null) {
                CommonUtil.drawOneLine(NavigationDetailActivity.this, map, route);
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
        this.startActivity(Intent.createChooser(shareIntent, "PhysicalDetailActivity结果分享"));
    }
}
