package com.wistron.swpc.android.WiTMJ.train;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wistron.swpc.android.WiTMJ.CommonUtil;
import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.LogUtils;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.ScreenShoot;
import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.util.FB;
import com.wistron.swpc.android.WiTMJ.util.JZLocationConverter;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;

import java.util.Date;

/**
 * Created by WH1604041 on 2016/4/27.
 */

public class TrainingActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ImageView iv_back;
    private ImageView train_iv_fb;
    private ImageView Train_iv_line;
    private MapView mapView;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String TAG = "TrainingActivity";
    private ProgressBar train_time_pb;
    private ProgressBar train_speed_pb;
    private ProgressBar train_distance_pb;
    private ProgressBar train_cal_pb;
    private int mSpeed;
    private int mDistance;
    private int mCal;
    private int mDuration;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LoginManager manager;
    private CallbackManager callbackManager;
    private boolean fb_flag = false;

    private TextView mSpdTv;
    private TextView mDisTv;
    private TextView mDurHrsTv;
    private TextView mDurMinsTv;
    private TextView mCalTv;

    private TextView titleTv;
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
/*        FacebookSdk.sdkInitialize(TrainingActivity.this);
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("publish_actions");
        //this loginManager helps you eliminate adding a LoginButton to your UI
        manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, permissionNeeds);
        manager.registerCallback(callbackManager, null);*/
        setContentView(R.layout.activity_training);
        mapView = (MapView) findViewById(R.id.training_mapView);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(TrainingActivity.this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        initView();
        initEvent();
        initData();
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void initView() {
        titleTv = (TextView) findViewById(R.id.tv_title);
        train_iv_fb = (ImageView) findViewById(R.id.train_iv_fb);
        Train_iv_line = (ImageView) findViewById(R.id.Train_iv_line);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        train_time_pb = (ProgressBar) findViewById(R.id.train_time_pb);
        train_speed_pb = (ProgressBar) findViewById(R.id.train_speed_pb);
        train_distance_pb = (ProgressBar) findViewById(R.id.train_distance_pb);
        train_cal_pb = (ProgressBar) findViewById(R.id.train_cal_pb);
        mSpeed = Integer.parseInt(PreferencesUtil.getPrefString(this, PreferenceConstants.TRAINING_CYCLING_SPEED, "0"));
        mDistance = Integer.parseInt(PreferencesUtil.getPrefString(this, PreferenceConstants.TRAINING_CYCLING_DISTANCE, "0"));
        mCal = Integer.parseInt(PreferencesUtil.getPrefString(this, PreferenceConstants.TRAINING_CYCLING_CALORIES, "0"));
        String hr = PreferencesUtil.getPrefString(this, PreferenceConstants.TRAINING_CYCLING_DURATION_HRS, "0");
        int mHr = Integer.parseInt(hr);
        String min = PreferencesUtil.getPrefString(this, PreferenceConstants.TRAINING_CYCLING_DURATION_MINS, "0");
        int mMin = Integer.parseInt(min);
        mDuration = mHr * 60 * 60 + mMin * 60;
    }

    @Override
    public void initEvent() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                navigation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 0,
                locationListener);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000, 0, locationListener);
        if (getIntent().getExtras() != null) {
            Intent i = getIntent();
            if (i.getIntExtra("TAG_TRAINING_CMPLETE_TO_DETAIL", 0) == Constants.TAG_TRAINING_CMPLETE_TO_DETAIL) {

                if (mSpeed > 20) {
                    train_speed_pb.setProgressDrawable(this.getResources().getDrawable(R.drawable.train_pb_bg));
                }
                if (mDistance > 50) {
                    train_distance_pb.setProgressDrawable(this.getResources().getDrawable(R.drawable.train_pb_bg));
                }
                if (mCal > 500) {
                    train_cal_pb.setProgressDrawable(this.getResources().getDrawable(R.drawable.train_pb_bg));
                }
                if (mDuration > 2 * 60 * 60) {
                    train_time_pb.setProgressDrawable(this.getResources().getDrawable(R.drawable.train_pb_bg));
                }
            }
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        train_iv_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Bitmap bitmap = ScreenShoot.takeScreenShoot(TrainingActivity.this);
                if (AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")) {
                    FB.sharePhotoToFacebook(bitmap);
                } else {
                    if (fb_flag) {
                        FB.sharePhotoToFacebook(bitmap);
                    } else {
                        Toast.makeText(TrainingActivity.this, "No facebook publish Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Train_iv_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ScreenShoot.takeScreenShoot(TrainingActivity.this);
                shareLine(bitmap);
            }
        });


    }

    @Override
    public void initData() {
        /*if(getIntent().getExtras()!=null){
            String recordId = getIntent().getStringExtra("recordId");
            RecordsDao recordsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getRecordsDao();
            List<Records> list = recordsDao.queryBuilder().where(RecordsDao.Properties.Record_id.eq(recordId)).list();

            if(list!=null && list.size()>0){
                mSpeed = Integer.parseInt(list.get(0).getAvg_speed());
                mDistance = Integer.parseInt(list.get(0).getDistance());
                mCal = Integer.parseInt(list.get(0).getAvg_speed());
                mDuration = Integer.parseInt(list.get(0).getDuration());

                if (mSpeed > 20) {
                    train_speed_pb.setProgressDrawable(getResources().getDrawable(R.drawable.train_pb_bg));
                }
                if (mDistance > 50) {
                    train_distance_pb.setProgressDrawable(getResources().getDrawable(R.drawable.train_pb_bg));
                }
                if (mCal > 500) {
                    train_cal_pb.setProgressDrawable(getResources().getDrawable(R.drawable.train_pb_bg));
                }
                if (mDuration > 2 * 60 * 60) {
                    train_time_pb.setProgressDrawable(getResources().getDrawable(R.drawable.train_pb_bg));
                }
            }
        }*/

        if (getIntent().getExtras() != null) {
            Intent i = getIntent();

            if (i.getIntExtra(Constants.TAG_TRAINING_TO_DETAIL, 0) == Constants.TAG_TRAINING) {
                Bundle bundle = i.getExtras();
                String startTimeTmp = bundle.getString("startTime");
                String startTimeStr = "";
                if (startTimeTmp.endsWith("Z")) {
                    Date startDate = CommonUtil.getStrToDate(startTimeTmp);
                    startTimeStr = CommonUtil.getDetailActyFormat(startDate);
                } else {
                    Date startDate = CommonUtil.getBeforeStrToDate(startTimeTmp);
                    startTimeStr = CommonUtil.getDetailActyFormat(startDate);
                }
                durDL = Double.valueOf(bundle.getString("duration"));
                dur = CommonUtil.getDuration(durDL);

                disDL = Double.valueOf(bundle.getString("distance"));
                disDL = CommonUtil.getDistance(disDL);
                dis = String.valueOf(disDL);

                calDL = Double.valueOf(bundle.getString("calories"));
                cal = String.valueOf(CommonUtil.getCalories(calDL));

                spdDL = Double.valueOf(bundle.getString("speed"));
                spd = String.valueOf(CommonUtil.getSpeed(spdDL));

                titleTv.setText(startTimeStr);
/*                mDurHrsTv.setText(dur[0]);
                mDurMinsTv.setText(dur[1]);
                mDisTv.setText(dis);
                mCalTv.setText(cal);
                mSpdTv.setText(spd);*/
                if (disDL>0 ){
                    mDistance = Integer.parseInt(dis);
                }else{
                    mDistance = 0;
                }if (spdDL>0){
                    mSpeed = Integer.parseInt(spd);
                }else{
                    mSpeed = 0;
                }if (calDL>0){
                    mCal = Integer.parseInt(cal);
                }else{
                    mCal = 0;
                }if (durDL>0){
                    mDuration = Integer.parseInt(bundle.getString("duration"));
                }else{
                    durDL = 0;
                }
                PreferencesUtil.setPrefString(this, PreferenceConstants.TRAINING_CYCLING_SPEED, mSpeed+"");
                PreferencesUtil.setPrefString(this, PreferenceConstants.TRAINING_CYCLING_DISTANCE, mDistance+"");
                PreferencesUtil.setPrefString(this, PreferenceConstants.TRAINING_CYCLING_DURATION_HRS, dur[0] +"");
                PreferencesUtil.setPrefString(this, PreferenceConstants.TRAINING_CYCLING_DURATION_MINS, dur[1]+"");
                PreferencesUtil.setPrefString(this, PreferenceConstants.TRAINING_CYCLING_CALORIES, mCal+"");

                if (mSpeed > 20) {
                    train_speed_pb.setProgressDrawable(getResources().getDrawable(R.drawable.train_pb_bg));
                }if (mSpeed == 0) {
                    train_speed_pb.setProgress(0);
                }
                if (mDistance > 50) {
                    train_distance_pb.setProgressDrawable(getResources().getDrawable(R.drawable.train_pb_bg));
                }if (mDistance == 0) {
                    train_distance_pb.setProgress(0);
                }
                if (mCal > 500) {
                    train_cal_pb.setProgressDrawable(getResources().getDrawable(R.drawable.train_pb_bg));
                }if (mCal == 0) {
                    train_cal_pb.setProgress(0);
                }
                if (mDuration < 60) {
                    train_time_pb.setProgress(0);
                }
            }
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Gets to GoogleMap from the MapView and does initialization stuff
        LogUtils.e(TAG, "onMapReady..");
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            LogUtils.e(TAG, "latitude : " + String.valueOf(mLastLocation.getLatitude()));
            LogUtils.e(TAG, "longitude : " + String.valueOf(mLastLocation.getLongitude()));

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        LogUtils.e(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        LogUtils.e(TAG, "onConnectionFailed");
    }

    public void navigation(Location location) {
        if (map == null)
            return;
        if (JZLocationConverter.outOfChina(location.getLatitude(), location.getLongitude())) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
            map.moveCamera(center);
            map.animateCamera(zoom);
        } else {
            JZLocationConverter.LatLng ll = new JZLocationConverter.LatLng(location.getLatitude(), location.getLongitude());
            JZLocationConverter.LatLng latLng = JZLocationConverter.wgs84ToGcj02(ll);
            LatLng target = new LatLng(latLng.getLatitude(), latLng.getLongitude());//转换后的新坐标
            CameraUpdate center = CameraUpdateFactory.newLatLng(target);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
            CameraPosition position = map.getCameraPosition();
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(12);
            builder.target(target);
            MarkerOptions markerOptions = new MarkerOptions().position(target);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            map.addMarker(markerOptions);
            map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
            map.moveCamera(center);
            map.animateCamera(zoom);
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
        this.startActivity(Intent.createChooser(shareIntent, "TrainingActivity结果分享"));
    }
}
