package com.wistron.swpc.android.WiTMJ.train;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.android.PolyUtil;
import com.wistron.swpc.android.WiTMJ.ApplicationHolder;
import com.wistron.swpc.android.WiTMJ.CommonUtil;
import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.LogUtils;
import com.wistron.swpc.android.WiTMJ.MainActivity;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.base.BaseFragment;
import com.wistron.swpc.android.WiTMJ.bean.Message;
import com.wistron.swpc.android.WiTMJ.bean.Records;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.dao.RecordsDao;
import com.wistron.swpc.android.WiTMJ.listener.onFragmentListener;
import com.wistron.swpc.android.WiTMJ.navigation.NavigationFragment;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Route;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Segment;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by WH1604041 on 2016/5/5.
 */
public class TrainEditFragment extends BaseFragment {
    private final String TAG = "TrainEditFragment";

    private View mTrainFragment;
    private TextView tv_complete,tv_cancel,navigationProgressTv;
    public Activity mContext;

    private onFragmentListener listener;
    private ProgressBar train_time_pb;
    private ProgressBar train_speed_pb;
    private ProgressBar train_distance_pb;
    private ProgressBar train_cal_pb;

    private int mSpeed;
    private int mDistance;
    private int mCal;
    private int mDuration;

    private Records records;
    private RecordsDao recordsDao;
    private TmjConnection connection;
    private PreferencesUtil mPreferencesUtil;

    private boolean isTrain = true;

    private Route mRoute;

//    private final Timer timer = new Timer();
//    private TimerTask task;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Boolean isSendKeepModel = false;
    private List<LatLng> segmentPoints;

    private Location currLocation;

    private Long startTime,compeleteTime;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (onFragmentListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mTrainFragment = inflater.inflate(R.layout.fragment_trainedit, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup p = (ViewGroup) mTrainFragment.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mTrainFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        mContext = getActivity();
        initView();
        initEvent();
        initData();
        initLocation();
        //开始时间
        startTime = System.currentTimeMillis();
    }

    @Override
    public void initView() {
        connection = TmjClient.create();
        recordsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getRecordsDao();
        mPreferencesUtil = new PreferencesUtil(getActivity(), PreferenceConstants.HOME_CYCLING_TABLE);
        train_time_pb = (ProgressBar) mTrainFragment.findViewById(R.id.train_time_pb);
        train_speed_pb = (ProgressBar) mTrainFragment.findViewById(R.id.train_speed_pb);
        train_distance_pb = (ProgressBar) mTrainFragment.findViewById(R.id.train_distance_pb);
        train_cal_pb = (ProgressBar) mTrainFragment.findViewById(R.id.train_cal_pb);
        mSpeed = Integer.parseInt(PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_SPEED, "0"));
        mDistance = Integer.parseInt(PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DISTANCE, "0"));
        mCal = Integer.parseInt(PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_CALORIES, "0"));
        String hr = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DURATION_HRS, "0");
        int mHr = Integer.parseInt(hr);
        String min = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DURATION_MINS, "0");
        int mMin = Integer.parseInt(min);
        mDuration = mHr * 60 * 60 + mMin * 60;
        tv_complete = (TextView) mTrainFragment.findViewById(R.id.tv_complete);
        tv_cancel = (TextView) mTrainFragment.findViewById(R.id.tv_cancel);

        navigationProgressTv = (TextView) mTrainFragment.findViewById(R.id.navigationProgressTv);
    }

    @Override
    public void initEvent() {
        if (mSpeed > 20) {
            train_speed_pb.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.train_pb_bg));
        }
        if (mDistance > 50) {
            train_distance_pb.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.train_pb_bg));
        }
        if (mCal > 500) {
            train_cal_pb.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.train_pb_bg));
        }
        if (mDuration > 2 * 60 * 60) {
            train_time_pb.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.train_pb_bg));
        }
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog("Complete now?", "", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        dialog.dismiss();

                        new AsyncTask<Void, Void, Long>() {
                            protected void onPreExecute() {
                                showProgress("Post records information");//add by wr
                            }

                            @Override
                            protected Long doInBackground(Void... params) {
                                //完成时间
                                compeleteTime = System.currentTimeMillis();
                                double subDistance = 0;
                                int num=0;

                                for (Segment segment:mRoute.getSegments()) {
                                    if (segment.isSend() == true) {
                                        subDistance =+ segment.getDistance();
                                        num++;
                                    }
                                }

                                //获取已发送send的segment的个数-1的距离
                                Double distance = Double.valueOf(0);
                                if(num != 0){
                                    //获取已发送send的最后一个segment
                                    Segment segment = mRoute.getSegments().get(num - 1);
                                    double lastPointDistance = CommonUtil.getDistance(currLocation.getLatitude(), currLocation.getLongitude()
                                            , segment.startPoint().latitude, segment.startPoint().longitude);
                                    distance = Double.valueOf(subDistance + lastPointDistance);
                                }

                                double durationMillis = (compeleteTime - startTime) / 1000;
                                Double duration = Double.valueOf(durationMillis);
                                Double speed = distance / duration;
                                Double calories = distance;

                                String record_id = PreferencesUtil.getPrefString(getActivity(), "Record_id", "");
                                String mRoute = PreferencesUtil.getPrefString(getActivity(), "mRoute", "");

                                double hour = (int) durationMillis / 60;//计算小时
                                double min = 0;
                                if(hour!=0){
                                    min = durationMillis % 60;//计算分钟
                                }

                                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_SPEED, speed+"");
                                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DISTANCE, distance+"");
                                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DURATION_HRS, hour +"");
                                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DURATION_MINS, min+"");
                                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_CALORIES, calories+"");


//                                String calories = PreferencesUtil.getPrefString(getActivity(), "HOME_CYCLING_CALORIES", "0");
//                                Double realCalories = Double.parseDouble(calories);
//                                String speed = PreferencesUtil.getPrefString(getActivity(), "HOME_CYCLING_SPEED", "0");
//                                Double realSpeed = Double.parseDouble(speed);
//                                String duration = PreferencesUtil.getPrefString(getActivity(), "HOME_CYCLING_DURATION", "0");
//                                Double realDuration = Double.parseDouble(duration);
//                                String distance = PreferencesUtil.getPrefString(getActivity(), "HOME_CYCLING_DISTANCE", "0");
//                                Double realDistance = Double.parseDouble(distance);

                                records = new Records();
                                records.setTag(Constants.TAG_TRAINING);
                                records.setCalories(calories + "");
                                records.setAvg_speed(speed + "");
                                records.setDuration(duration + "");
                                records.setDistance(distance + "");
                                records.setRoutes(mRoute);
                                records.setStart_time(new Date() + "");
                                records.setEnd_time(new Date() + "");
                                records.setRank(0);
                                records.setRecord_id(record_id);
                                long recordId = recordsDao.insertOrReplace(records);
                                return recordId;
                            }

                            @Override
                            protected void onPostExecute(final Long recordId) {
                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }

                                Gson g2 = new Gson(); //new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                                records.setRoutes(null);
                                LogUtils.d(TAG, "record json : " + g2.toJson(records));
                                JsonParser parser = new JsonParser();
                                JsonObject jo = parser.parse(g2.toJson(records)).getAsJsonObject();
                                String act = PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN, "");
                                Call<Message> call = connection.putRecord(act, records.getRecord_id(), jo);
                                call.enqueue(new Callback<Message>() {
                                    @Override
                                    public void onResponse(Call<Message> call, Response<Message> response) {
                                        L.d(TAG, "putRecord   " + response.code() + " " + response.message());
                                        hideProgress();//add by wr
                                        if (response.isSuccessful()) {
                                            CommonUtil.setRecordPlus(mPreferencesUtil, records);//写入累加数据到配置文件

                                            //成功后跳转
                                            Intent intent = new Intent();
                                            intent.putExtra("TAG_TRAINING_CMPLETE_TO_DETAIL",Constants.TAG_TRAINING_CMPLETE_TO_DETAIL);
                                            intent.putExtra("recordId",recordId);
                                            intent.setClass(getActivity(),TrainingActivity.class);
                                            startActivity(intent);

                                        } else {
                                            L.d(TAG, "Failure" + response.code() + " " + response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Message> call, Throwable t) {
                                        hideProgress();
                                        L.d(TAG, "Failure    " + t.getMessage());
                                    }
                                });
                            }
                        }.execute();
                    }
                });
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog("sure to cancel?", "(this data will not be record)", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        listener.onStartFragment(TrainFragment.class);
                        isTrain = false;//关闭训练模式
                    }
                });
            }
        });

        navigationProgressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStartFragmentWithData(NavigationFragment.class,mRoute);
            }
        });
    }

    void initLocation(){
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currLocation = location;
                navigation(location);

                L.i(TAG,"locationListener is excute ");

                if(isSendKeepModel){
                    LatLng locationLatLng = new LatLng(location.getLatitude(),location.getLongitude());
//                    LogUtils.e(TAG,"segmentPoints====="+segmentPoints.toString());
                    boolean isLocationOnPath =  PolyUtil.isLocationOnPath(locationLatLng, segmentPoints, true, 20);
                    if(isLocationOnPath){
                        MainActivity mainActivity = (MainActivity) TrainEditFragment.this.getActivity();
                        JsonObject jsonObject = new JsonObject();

                        jsonObject.addProperty("type","train");
                        jsonObject.addProperty("status","update");

                        String custom_speed = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_SPEED, "0");
                        String custom_distance = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DISTANCE, "0");
                        String custom_cal = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_CALORIES, "0");

                        jsonObject.addProperty("speed",custom_speed);
                        jsonObject.addProperty("distance",custom_distance);
                        jsonObject.addProperty("calories",custom_cal);
                        jsonObject.addProperty("duration",mRoute.getDistanceValue()+"");

                        jsonObject.addProperty("calories_over",custom_cal);
                        jsonObject.addProperty("distance_over",custom_distance);
                        jsonObject.addProperty("speed_over",custom_speed);


                        jsonObject.addProperty("nav_road","0");
                        jsonObject.addProperty("nav_distance",""+"0");
                        jsonObject.addProperty("nav_direction","keep");

                        LogUtils.e(TAG, jsonObject.toString());

                        mainActivity.getBluetoothSPP().send(jsonObject.toString(), true);

                        Toast.makeText(TrainEditFragment.this.getContext(), "=======send keep=======", Toast.LENGTH_LONG).show();
                        isSendKeepModel = false;
                        segmentPoints = null;
                    }
                }
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
                LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    @Override
    public void initData() {

    }

    public void setmRoute(Route mRoute) {
        this.mRoute = mRoute;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    void navigation(Location location){
        if (mRoute != null && isTrain && TmjApplication.getInstance().isConneced()) { //modify by wr
            L.i(TAG,"navigation is excute!");

            for (int i = 0; i < mRoute.getSegments().size(); i++) {
                L.i(TAG,"segment is excute!");

                Segment segment = mRoute.getSegments().get(i);
                if (segment.isSend()) continue;
                double distance = CommonUtil.getDistance(currLocation.getLatitude(), currLocation.getLongitude()
                        , segment.startPoint().latitude, segment.startPoint().longitude);
                L.i(TAG,"distance is "+distance);

                if (distance <= 100) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("type","train");
                    if (i==0){
                        jsonObject.addProperty("status","init");
                    }else{
                        jsonObject.addProperty("status","update");
                    }

                    String custom_speed = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_SPEED, "0");
                    String custom_distance = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DISTANCE, "0");
                    String custom_cal = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_CALORIES, "0");

                    jsonObject.addProperty("speed",custom_speed);
                    jsonObject.addProperty("distance",custom_distance);
                    jsonObject.addProperty("calories",custom_cal);

                    jsonObject.addProperty("duration",mRoute.getDistanceValue()+"");

                    jsonObject.addProperty("calories_over",custom_cal);
                    jsonObject.addProperty("distance_over",custom_distance);
                    jsonObject.addProperty("speed_over",custom_speed);


                    jsonObject.addProperty("nav_road",segment.getInstruction());
                    jsonObject.addProperty("nav_distance",""+distance);

                    if (!TextUtils.isEmpty(segment.getManeuver())){
                        LogUtils.e(TAG,jsonObject.toString());

                        jsonObject.addProperty("nav_direction",CommonUtil.rePlaceManeuver(segment.getManeuver()));

                        mainActivity.getBluetoothSPP().send(jsonObject.toString(), true);

                        isSendKeepModel = true;
                        segmentPoints = segment.getPoints();
                    }
                    segment.setSend(true);
                }
                break;
            }
        }
    }
}
