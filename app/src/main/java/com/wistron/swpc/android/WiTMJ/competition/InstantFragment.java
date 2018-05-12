package com.wistron.swpc.android.WiTMJ.competition;

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
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wistron.swpc.android.WiTMJ.ApplicationHolder;
import com.wistron.swpc.android.WiTMJ.CommonUtil;
import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.HomeFragment;
import com.wistron.swpc.android.WiTMJ.LogUtils;
import com.wistron.swpc.android.WiTMJ.MainActivity;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.base.BaseMapFragment;
import com.wistron.swpc.android.WiTMJ.bean.DbWorkOut;
import com.wistron.swpc.android.WiTMJ.bean.Message;
import com.wistron.swpc.android.WiTMJ.bean.Participants;
import com.wistron.swpc.android.WiTMJ.bean.Profile;
import com.wistron.swpc.android.WiTMJ.bean.Records;
import com.wistron.swpc.android.WiTMJ.bean.WorkOut;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.BluetoothState;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.DeviceList;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.dao.DbWorkOutDao;
import com.wistron.swpc.android.WiTMJ.dao.ProfileDao;
import com.wistron.swpc.android.WiTMJ.dao.RecordsDao;
import com.wistron.swpc.android.WiTMJ.http.response.AddWorkResponse;
import com.wistron.swpc.android.WiTMJ.http.response.GetUserResponse;
import com.wistron.swpc.android.WiTMJ.listener.onFragmentListener;
import com.wistron.swpc.android.WiTMJ.navigation.LegAdapter;
import com.wistron.swpc.android.WiTMJ.navigation.RouteAdapter;
import com.wistron.swpc.android.WiTMJ.physicalreport.CompetitionDetailActivity;
import com.wistron.swpc.android.WiTMJ.util.JZLocationConverter;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;
import com.wistron.swpc.android.WiTMJ.util.googlemap.AbstractRouting;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Route;
import com.wistron.swpc.android.WiTMJ.util.googlemap.RouteException;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Routing;
import com.wistron.swpc.android.WiTMJ.util.googlemap.RoutingListener;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Segment;
import com.wistron.swpc.android.WiTMJ.widget.FlowLayout;
import com.wistron.swpc.android.WiTMJ.widget.FlowTextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by WH1604025 on 2016/4/18.
 */
public class InstantFragment extends BaseMapFragment implements RoutingListener,View.OnClickListener{
    private final static String TAG = "InstantFragment";
    /*google map*/
    private String mLatitude;
    private String mLongitude;
    private Marker marker;
    private Boolean firstLocationUpdate = true;
    private Boolean naviDetailModel = false;

    private List<GetUserResponse> responseList = new ArrayList<GetUserResponse>();
    private List<String> usrNameList = new ArrayList<String>();
    private onFragmentListener listener;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private LegAdapter legAdapter;
    private Route mRoute;
    private ListView mlistView;
    private List<Segment> mListSegment;
    private ListView mlistSegmentView;
    private List<Route> mList;

    private RelativeLayout mOpponentMapLayout;
    private RelativeLayout mShowOppnentLayout;

    private View mFragment;
    private ImageView mMappingIcon;
    private Button mInstStart1Btn;
    private LinearLayout mCompleteLayout;
//    private LinearLayout mCmplteCncelLayout;
    private Button mCmplteBtn;
    private Button mCancelBtn;
    private Animation operatingAnim;
    private FlowLayout flowLayout;

    private RecordsDao mRecordsDao;
    private PreferencesUtil mPreferencesUtil;

    private TmjConnection connection;
    private JsonObject jsonObject;//workout json
    private DbWorkOut  dbWorkOut = new DbWorkOut();
    private DbWorkOutDao workoutDao;
    private String mRecordId;
    private String mWorkoutId;
    private List<Participants> participantList = new ArrayList<Participants>();
    private String participantListStr;

    private String startTime;

    private int opponentStatus;
    private TextView noOpponentTv;
    private boolean isRouteOk=false;
    private TextView mTimeTv1;
    private TextView mTimeTv2;

    private int tag = Constants.TAG_INSTANT;

    private WorkOut workOut;

    private boolean JANE_ZUO = true;
    private ProfileDao profileDao;
    private String userId;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtils.i(TAG,"zlb==onAttach==");
        listener = (onFragmentListener) activity;
        /*cmplte/cancel cancel后的监听listener 再看*/
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG,"zlb==onCreate==");
        mContext = this.getActivity();
        mFragment = getActivity().getLayoutInflater().inflate(R.layout.fragment_instant, null);
        mapView = (MapView) mFragment.findViewById(R.id.inst_mapview);
        mapView.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LogUtils.i(TAG,"zlb==onCreateView==");
        ViewGroup p = (ViewGroup) mFragment.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        initView();
        initEvent();
        initData();
        return mFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //add by wr
        firstLocationUpdate = true;
        naviDetailModel = false;
        JANE_ZUO = true;
        opponentStatus = Constants.INIT_OPPONENT;
        mCancelBtn.setVisibility(View.GONE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.i(TAG,"zlb==onActivityCreated==");
        if (savedInstanceState != null)
            return;
    }

    @Override
    public void initView() {
        opponentStatus = Constants.INIT_OPPONENT;
        mPreferencesUtil = new PreferencesUtil(getActivity(),
                PreferenceConstants.HOME_CYCLING_TABLE);
        mRecordsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getRecordsDao();
        workoutDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getDbWorkOutDao();
        connection = TmjClient.create();

        mOpponentMapLayout = (RelativeLayout) mFragment.findViewById(R.id.inst_opponent_layout);
        mShowOppnentLayout = (RelativeLayout) mFragment.findViewById(R.id.inst_oppnent_layout);

        mMappingIcon = (ImageView) mFragment.findViewById(R.id.mappingIcon);
        mInstStart1Btn = (Button) mFragment.findViewById(R.id.inst_start1_btn);
        mCompleteLayout = (LinearLayout) mFragment.findViewById(R.id.inst_complete_layout);
//        mCmplteCncelLayout = (LinearLayout) mFragment.findViewById(R.id.inst_cmplte_cncel_layout);

        noOpponentTv = (TextView) mFragment.findViewById(R.id.inst_no_opponent);
        mCmplteBtn = (Button) mFragment.findViewById(R.id.inst_cmplte_btn);
        mCmplteBtn.setOnClickListener(this);
        mCancelBtn = (Button) mFragment.findViewById(R.id.inst_cancel_btn);
        mCancelBtn.setOnClickListener(this);
        mInstStart1Btn.setOnClickListener(this);
        operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.widget_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        flowLayout = (FlowLayout) mFragment.findViewById(R.id.inst_flowlayout);

        starting = (AutoCompleteTextView) mFragment.findViewById(R.id.startRouteEt);
        destination = (AutoCompleteTextView) mFragment.findViewById(R.id.endRouteEt);

        mlistView = (ListView) mFragment.findViewById(R.id.inst_route_lv);
        mlistSegmentView = (ListView) mFragment.findViewById(R.id.inst_listsegment);
        legAdapter = new LegAdapter(mContext, mListSegment);
        mlistSegmentView.setAdapter(legAdapter);

        mTimeTv1 = (TextView) mFragment.findViewById(R.id.timeTv1);
        mTimeTv2 = (TextView) mFragment.findViewById(R.id.timeTv2);

        super.initView();
    }

    @Override
    public void initEvent() {
        super.initEvent();
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                navigation(location);
                mLatitude = String.valueOf(location.getLatitude());
                mLongitude = String.valueOf(location.getLongitude());
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
    }

    @Override
    public void initData() {
        profileDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getProfileDao();
        List<Profile> list = profileDao.queryBuilder().list();
/*
        if(PreferencesUtil.getPrefBoolean(getActivity(),PreferenceConstants.USER_FIRST_INIT,false)){
            String token = PreferencesUtil.getPrefString(getActivity().getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, "");
            //            showProgress("initialize...");
            HttpImpl.getInstance(getActivity()).getProfile(handler, token);
        }else{*/
            if(list.size()>0) {
                Profile profile = list.get(0);
                userId = profile.getProfile_id();
            }

        if(workOut!=null){
            starting.setText(workOut.getStart_location().split("\\|")[0]);
            destination.setText(workOut.getEnd_location().split("\\|")[0]);
            start = new LatLng(Double.parseDouble(workOut.getStart_location().split("\\|")[1].split(",")[0]),
                    Double.parseDouble(workOut.getStart_location().split("\\|")[1].split(",")[1]));

            end = new LatLng(Double.parseDouble(workOut.getEnd_location().split("\\|")[1].split(",")[0]),
                    Double.parseDouble(workOut.getEnd_location().split("\\|")[1].split(",")[1]));

            for (int i = 0; i < workOut.getParticipants().size(); i++) {
                FlowTextView tv = new FlowTextView(getActivity(), false);
                tv.setText(workOut.getParticipants().get(i).getUsername());
                flowLayout.getList().add(workOut.getParticipants().get(i).getUsername());
                flowLayout.addView(tv);
            }

            for (Participants p:workOut.getParticipants()) {
                if(p.getUser_id().equals(TmjApplication.getInstance().getUserid())){
                    mRecordId = p.getRecord_id();
                }
            }
            opponentStatus = Constants.EXIST_OPPONENT_FROM_SCHEDULE;
        }
        /*else{
            getUsers();
        }*/
        /*for (int i = 0; i < usrNameList.size(); i++) {
            FlowTextView tv = new FlowTextView(getActivity(),true,flowLayout);
            LogUtils.i(TAG,"zlb==== usrNameList ====="+usrNameList);
            tv.setText(usrNameList.get(i));
            flowLayout.getList().add(usrNameList.get(i));
            flowLayout.addView(tv);
            tv.setOnClickListener();
        }*/
        noOpponentTv.setVisibility(View.GONE);
        mCancelBtn.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.i(TAG, "zlb==onPause==");
        if (mMappingIcon != null && mMappingIcon.getAnimation() != null) {
            mMappingIcon.clearAnimation();
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    public void setWorkOut(final WorkOut workOut){
        this.workOut = workOut;
        JANE_ZUO = false;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                starting.setText(workOut.getStart_location().split("\\|")[0]);
                destination.setText(workOut.getEnd_location().split("\\|")[0]);
                start = new LatLng(Double.parseDouble(workOut.getStart_location().split("\\|")[1].split(",")[0]),
                        Double.parseDouble(workOut.getStart_location().split("\\|")[1].split(",")[1]));

                end = new LatLng(Double.parseDouble(workOut.getEnd_location().split("\\|")[1].split(",")[0]),
                        Double.parseDouble(workOut.getEnd_location().split("\\|")[1].split(",")[1]));

                for (int i = 0; i < workOut.getParticipants().size(); i++) {
                    FlowTextView tv = new FlowTextView(getActivity(), false);
                    tv.setText(workOut.getParticipants().get(i).getUsername());
                    flowLayout.getList().add(workOut.getParticipants().get(i).getUsername());
                    flowLayout.addView(tv);
                }

                for (Participants p:workOut.getParticipants()) {
                    if(p.getUser_id().equals(TmjApplication.getInstance().getUserid())){
                        mRecordId = p.getRecord_id();
                    }
                }

                if(start!=null && end != null){
                    route();
                }

                //            try{
//                if (TextUtils.isEmpty(workOut.getRoute())) {
//                    JSONObject json = new JSONObject(workOut.getRoute());
//                    CommonUtil.drawOneLine(getActivity(),map,CommonUtil.parseRoute(json));
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
                opponentStatus = Constants.EXIST_OPPONENT_FROM_SCHEDULE;
            }
        }, 2000);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.inst_start1_btn:
                noOpponentTv.setVisibility(View.GONE);
                legAdapter = new LegAdapter(mContext, mListSegment);
                mlistSegmentView.setAdapter(legAdapter);
                if(TmjApplication.getInstance().isConneced()){
                    if(isRouteOk){
                        if (opponentStatus == Constants.INIT_OPPONENT) {
                            //给TMJ设备发送数据
                            ((MainActivity)getActivity()).startCompetitionMode("navigation","init");
                            getUsers();

                        }else if (opponentStatus == Constants.EXIST_OPPONENT) {

                            final String startPlace = starting.getText().toString();
                            final String endPlace = destination.getText().toString();

                            DateFormat format1 = new java.text.SimpleDateFormat("HH:mm");
                            String time1Str = format1.format(new Date());
                            mTimeTv1.setText(time1Str);

                            int t1 = mRoute.getDurationValue();
                            String time2Str = CommonUtil.getLongToStr(new Date(),t1);
                            mTimeTv2.setText(time2Str);

                            participantList = flowLayout.getParticipantsList();

                            new  AsyncTask<Void,Void,Long>(){
                                @Override
                                protected void onPreExecute() {
                                    showProgress("Post route information");
                                }
                                @Override
                                protected Long doInBackground(Void... params) {
                                    jsonObject = new JsonObject();
                                    jsonObject.addProperty("tag",tag);
                                    jsonObject.addProperty("name","instant_test3");
                                    jsonObject.addProperty("permission",Constants.PERMISSION_PUBLIC);
                                    jsonObject.addProperty("start_date",CommonUtil.getServerDateFormat(new Date()));
                                    jsonObject.addProperty("end_date",CommonUtil.getServerDateFormat(new Date()));
                                    jsonObject.addProperty("start_location",startPlace+"|"+mRoute.getStartPoint().longitude+","+mRoute.getStartPoint().latitude);
                                    jsonObject.addProperty("end_location",endPlace+"|"+mRoute.getEndPoint().longitude+","+mRoute.getEndPoint().latitude);
                                    JsonParser parser = new JsonParser();
                                    JsonElement je_mRoute = parser.parse(mRoute.getRouteString());
                                    jsonObject.add("route", je_mRoute);
                                    Gson gson =new Gson();

                                    participantListStr = gson.toJson(participantList);

                                    JsonParser parser1 = new JsonParser();
                                    JsonElement jsonElement = parser1.parse(participantListStr);
                                    jsonObject.add("participants",jsonElement);
                                    jsonObject.addProperty("note","zlb test");
                                    dbWorkOut.setJsonStr(jsonObject.toString());
                                    dbWorkOut.setStatus(Constants.STATUS_INIT);
                                    LogUtils.i(TAG,"participantList json===="+participantListStr);
                                    LogUtils.i(TAG,"JsonObject =========="+jsonObject.toString());
                                    long db_id =  workoutDao.insertOrReplace(dbWorkOut);

                                    return db_id;
                                }
                                @Override
                                protected void onPostExecute(final Long db_id) {
                                    String token= PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN,"");
                                    LogUtils.i(TAG,"token =="+token);
                                    Call<AddWorkResponse> call = connection.postWorkOut(token,jsonObject);
                                    call.enqueue(new Callback<AddWorkResponse>() {
                                        @Override
                                        public void onResponse(Call<AddWorkResponse> call, Response<AddWorkResponse> response) {
                                            hideProgress();
                                            if(response.isSuccessful()){
                                                LogUtils.i(TAG,"mRecordId====="+response.body().getRecord_id());
                                                mRecordId = response.body().getRecord_id();
                                                mWorkoutId = response.body().getWorkout_id();
                                                dbWorkOut.setId(db_id);
                                                dbWorkOut.setWorkoutId(mWorkoutId);
                                                dbWorkOut.setRecordId(mRecordId);
                                                dbWorkOut.setStatus(Constants.STATUS_INIT);
                                                workoutDao.update(dbWorkOut);
                                                String startTimeStrTmp = CommonUtil.getServerDateFormat(new Date());
                                                startTime =startTimeStrTmp;
                                                noOpponentTv.setVisibility(View.GONE);
                                                mOpponentMapLayout.setVisibility(View.GONE);
                                                mShowOppnentLayout.setVisibility(View.GONE);
                                                mCompleteLayout.setVisibility(View.VISIBLE);
                                                mCmplteBtn.setVisibility(View.VISIBLE);
                                            }else{
                                                LogUtils.d(TAG,"Failure===="+response.code() + " " +response.message());
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<AddWorkResponse> call, Throwable t) {
                                            LogUtils.d(TAG,"Failure====" +t.getMessage());
                                        }
                                    });

                                }
                            }.execute();
                        }else if (opponentStatus == Constants.NO_OPPONENT){
                            /*add==zlb*/
                            noOpponentTv.setVisibility(View.VISIBLE);

                            opponentStatus = Constants.INIT_OPPONENT;
                            mInstStart1Btn.setVisibility(View.VISIBLE);
                        }else if(opponentStatus == Constants.EXIST_OPPONENT_FROM_SCHEDULE && !JANE_ZUO){

                            //给TMJ设备发送数据
                            ((MainActivity)getActivity()).startCompetitionMode("navigation","init");

                            noOpponentTv.setVisibility(View.GONE);
                            mOpponentMapLayout.setVisibility(View.GONE);
                            mShowOppnentLayout.setVisibility(View.GONE);
                            mCompleteLayout.setVisibility(View.VISIBLE);
                            mCmplteBtn.setVisibility(View.VISIBLE);

                            String startTimeStrTmp = CommonUtil.getServerDateFormat(new Date());
                            startTime =startTimeStrTmp;

                            participantList = flowLayout.getParticipantsList();
                        }

                    }else{
                        if (CommonUtil.Operations.isOnline(mContext)) {
                            route();
                        } else {
                            Toast.makeText(mContext, "No internet connectivity", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    showCustomDialog("Erro", "TMJ device not connected, please connect !", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent((MainActivity)getActivity(), DeviceList.class);
                            getActivity().startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                            dialog.dismiss();
                        }
                    }, false);
                }
                break;
            case R.id.inst_cmplte_btn:
                showCustomDialog("Complete now？", "",new android.content.DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        new  AsyncTask<Void,Void,Long>(){
                            @Override
                            protected Long doInBackground(Void... params) {
                               // dbWorkOut.setStatus(Constants.STATUS_UPLOADED);
                                //workoutDao.update(dbWorkOut);
                                Double distance = new Double(mRoute.getDistanceValue());
                                Double duration = new Double(mRoute.getDurationValue());
                                Double speed = distance / duration;//单位m/s,统一存原始值，到最后取出显示时再统一计算====zlb========
//                                Double spd = CommonUtil.getSpeed(speed);
//                                Double calories = CommonUtil.getCalories(distance);
//                                Double calories = distance*3.3/8.8;//此时单位m,需到时候显示时转为km=====zlb========
                                Double calories = distance;//暂时存为距离distance,单位m,需到时候显示时再换算并转为km=====zlb========
                                final Records records = new Records();
                                records.setTag(tag);
                                records.setCalories(calories + "");
                                records.setAvg_speed(speed + "");
                                records.setDuration(duration + "");
                                records.setDistance(distance + "");
                                records.setRoutes(mRoute.getRouteString());
                                records.setRank(0);
                                records.setRecord_id(mRecordId);
                                records.setStart_time(startTime);
//                                records.setWorkout_id(mWorkoutId);
//                                records.setRecord_id(dbWorkOut.getRecordId());
                                /*-----------*/
//                                records.setInvitation(1);

                                String endTimeStrTmp = CommonUtil.getServerDateFormat(new Date());
                                LogUtils.d(TAG,"endTimeStrTmp==== : "+endTimeStrTmp);
                                records.setEnd_time(endTimeStrTmp);
                                long recordId = mRecordsDao.insertOrReplace(records);
                                Gson g2 = new Gson();
                                JsonParser parser = new JsonParser();
                                JsonObject jo = parser.parse( g2.toJson(records)).getAsJsonObject();
                                String act = PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN, "");

                                Call<Message> call = connection.putRecord(act, records.getRecord_id(), jo);
                                call.enqueue(new Callback<Message>() {
                                    @Override
                                    public void onResponse(Call<Message> call, retrofit2.Response<Message> response) {
                                        LogUtils.d(TAG, "response====  " + response.code() + " " + response.message());
                                        if (response.isSuccessful()) {
                                            CommonUtil.setRecordPlus(mPreferencesUtil, records);//写入累加数据到配置文件
                                        } else {
                                            LogUtils.d(TAG, "Failure====  " + response.code() + " " + response.message());
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<Message> call, Throwable t) {
                                        LogUtils.d(TAG, "Failure===" + t.getMessage());
                                    }
                                });
                                return recordId;
                            }

                            @Override
                            protected void onPostExecute(Long recordId) {
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                Bundle bundle = new Bundle();
                                Intent i = new Intent();
                                i.putExtra("TAG_INSTANT_CMPLETE_TO_DETAIL",Constants.TAG_INSTANT_CMPLETE_TO_DETAIL);
                                bundle.putString("recordId", dbWorkOut.getRecordId());
                                locationManager.removeUpdates(locationListener);
                                dialog.dismiss();
                                i.putExtras(bundle);
                                i.setClass(getActivity(),CompetitionDetailActivity.class);
                                i.putExtra("JANE_ZUO",JANE_ZUO);
                                getActivity().startActivityForResult(i,Constants.INITVIEW);
                            }
                        }.execute();
                    }
                });
                break;
            case R.id.inst_cancel_btn:
                showCustomDialog("sure to cancel?", "(this data will not be record)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        locationManager.removeUpdates(locationListener);
                        dialog.dismiss();
                        listener.onStartFragment(HomeFragment.class);
                        naviDetailModel = false;//add by wr
                    }
                });
                break;
            /*public void onConfigurationChanged(Configuration newConfig) {
                super.onConfigurationChanged(newConfig);
                if (operatingAnim != null && infoOperatingIV != null && operatingAnim.hasStarted()) {
                    infoOperatingIV.clearAnimation();
                    infoOperatingIV.startAnimation(operatingAnim);
                }
            }*/
            case R.id.changeRouteIv:
                swapLocation();
                break;
        }
    }

    public void swapLocation(){
        isSwapClick = true;
        if (start == null || end == null) { //add by wr
            if (start == null) {
                if (starting.getText().length() > 0) {
                    starting.setError("Choose location from dropdown.");

                } else {
                    Toast.makeText(this.getActivity(), "Please choose a starting point.", Toast.LENGTH_SHORT).show();
                }
            }
            if (end == null) {
                if (destination.getText().length() > 0) {
                    destination.setError("Choose location from dropdown.");
                } else {
                    Toast.makeText(this.getActivity(), "Please choose a destination.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            String tempStr = starting.getText().toString();
            starting.setText(destination.getText().toString());
            destination.setText(tempStr);

            LatLng temp = start;
            start = end;
            end = temp;
            //add by wr
            starting.dismissDropDown();
            destination.dismissDropDown();
        }
    }

    public void route() {
        if (map == null) {
            Toast.makeText(this.getActivity(), "map is not ready.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (start == null || end == null) {
            if (start == null) {
                if (starting.getText().length() > 0) {
                    starting.setError("Choose location from dropdown.");
                } else {
                    Toast.makeText(this.getActivity(), "Please choose a starting point.", Toast.LENGTH_SHORT).show();
                }
            }
            if (end == null) {
                if (destination.getText().length() > 0) {
                    destination.setError("Choose location from dropdown.");
                } else {
                    Toast.makeText(this.getActivity(), "Please choose a destination.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .language("zh-CN")
                    .key(Constants.GOOGLE_DIRECTION_KEY)
                    .build();
            routing.execute();
        }
    }
    @Override
    public void onRoutingFailure(RouteException e) {
        hideProgress();
        if (e != null) {
            Toast.makeText(this.getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.getActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
        showProgress("Fetching route information");
    }

    @Override
    public void onRoutingSuccess(List<com.wistron.swpc.android.WiTMJ.util.googlemap.Route> route, int shortestRouteIndex) {
        hideProgress();

        isRouteOk = true;

//        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
//        map.moveCamera(center);
//        map.animateCamera(zoom);

        drawLine(route.get(0));
        mList = new ArrayList<Route>();
        mListSegment = new ArrayList<Segment>();
        mList.addAll(route);
        mListSegment.addAll(route.get(0).getSegments());
        mlistView.setAdapter(new RouteAdapter(mContext, mList));
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawLine(mList.get(position));
                mListSegment.clear();
                mListSegment.addAll(mList.get(position).getSegments());
            }
        });
        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        map.addMarker(options);
    }

    public void drawLine(Route route){
        mRoute =route;
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        polylines = new ArrayList<>();
        //add route(s) to the map.
        //In case of more than 5 alternative routes
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.width(10);
        polyOptions.color(getResources().getColor(R.color.blue));
        polyOptions.addAll(route.getPoints());
        Polyline polyline = map.addPolyline(polyOptions);
        polylines.add(polyline);
        moveRouteInCameraCenter(route);
    }

    //将路线设置在地图可视范围内 add by WR
    private void moveRouteInCameraCenter(Route route){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(route.getLatLgnBounds(), 50);
        map.moveCamera(cameraUpdate);
    }

    @Override
    public void onRoutingCancelled() {

    }
    public void navigation(Location location) {
        if (map == null)
            return;
        if (JZLocationConverter.outOfChina(location.getLatitude(), location.getLongitude())) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
            if(firstLocationUpdate){ //modify by wr
                map.moveCamera(center);
                map.animateCamera(zoom);
                firstLocationUpdate = false;
            }

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
            if(marker!=null){
                marker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions().position(target) ;
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            marker = map.addMarker(markerOptions);

            if(firstLocationUpdate){  //modify by wr
                map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                map.moveCamera(center);
                map.animateCamera(zoom);
                firstLocationUpdate = false;
            }
        }
        if(mRoute != null&& naviDetailModel == true){
            LogUtils.i(TAG,"zlb==mroute=="+mRoute);
            LogUtils.i(TAG,"zlb==getDistanceText=="+mRoute.getDistanceText());
            LogUtils.i(TAG,"zlb==getDistanceValue=="+mRoute.getDistanceValue());
            LogUtils.i(TAG,"zlb==getDurationText=="+mRoute.getDurationText());
            LogUtils.i(TAG,"zlb==getDurationValue=="+mRoute.getDurationValue());
            for (int i = 0;i<mRoute.getSegments().size();i++) {
                Segment segment =mRoute.getSegments().get(i);
                if(segment.getManeuver()==null||segment.isSend())continue;
                double distance = CommonUtil.getDistance(location.getLatitude(),location.getLongitude()
                        ,segment.getEndPoint().latitude,segment.getEndPoint().longitude);
                LogUtils.d(TAG," distance  "+distance + " getManeuver() "+segment.getManeuver());

                // if(distance <= 30){
                MainActivity mainActivity=  (MainActivity) this.getActivity();
                if(!TextUtils.isEmpty(segment.getManeuver())){
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("type","competition");
                    if (i==0){
                        jsonObject.addProperty("status","init");
                    }else{
                        jsonObject.addProperty("status","update");
                    }
                    jsonObject.addProperty("nav_road",segment.getInstruction());
                    jsonObject.addProperty("nav_distance",""+distance);
                    jsonObject.addProperty("nav_direction",segment.getManeuver());
//                    mainActivity.getBluetoothSPP().send(jsonObject.getAsString(), true);
                    segment.setSend(true);
                    if(legAdapter!=null){
                        legAdapter.notifyDataSetChanged();
                    }
                    break;
                }
            }
        }
    }

   private void getUsers(){
       mMappingIcon.setVisibility(View.VISIBLE);
       if (operatingAnim != null) {
           mMappingIcon.startAnimation(operatingAnim);
           //operatingAnim.setFillAfter(true);
       }
       String token = PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN, "");
       LogUtils.i(TAG,"token =" +token);
       LogUtils.i(TAG," mLongitude:mLatitude==="+mLongitude+":"+mLatitude);
       TmjConnection connection = TmjClient.create();
       Call<List<GetUserResponse>> call2 = connection.getNearbyUsers(token,"",mLongitude+":"+mLatitude);
       call2.enqueue(new Callback<List<GetUserResponse>>() {
           @Override
           public void onResponse(Call<List<GetUserResponse>> call, Response<List<GetUserResponse>> response) {
               hideProgress();
               if (response.isSuccessful()) {
                   responseList = response.body();
                   if (responseList.size() > 0) {
                       flowLayout.removeAllViews();
                       flowLayout.getParticipantsList().clear();
                       for (int i = 0; i < responseList.size(); i++) {
                           Participants participants = new Participants();
                           participants.setUser_id(responseList.get(i).getId());
                           participants.setUsername(responseList.get(i).getUsername());
                           if (!flowLayout.getParticipantsList().contains(participants)) {
                               flowLayout.getParticipantsList().add(participants);
                           }
                           FlowTextView tv = new FlowTextView(getActivity(), true, flowLayout);
                           if (!userId.equals(responseList.get(i).getId())){
                               tv.setText(participants.getUsername());
                               flowLayout.addView(tv);
                           }
                           tv.setOnClickListener();
                       }
                       noOpponentTv.setVisibility(View.GONE);
                       opponentStatus = Constants.EXIST_OPPONENT;
                   }else {
                       noOpponentTv.setVisibility(View.VISIBLE);
                       opponentStatus = Constants.NO_OPPONENT;
                   }
                   LogUtils.i(TAG,"responseList.size ===="+responseList.size());
                   LogUtils.i(TAG,"users size===== "+flowLayout.getParticipantsList().size());
                   LogUtils.i(TAG,"participantList ===== "+participantList.toString());
//                   flowLayout.setList(usrNameList);
               } else {
                   T.showLong(TmjApplication.getInstance(), response.code() + " " + response.message());
                   LogUtils.i(TAG, "Failure====  " + response.code() + " " + response.message());
                   hideProgress();
               }
           }

           @Override
           public void onFailure(Call<List<GetUserResponse>> call, Throwable t) {
               hideProgress();
               T.showLong(getActivity(),t.getMessage());
           }
       });
   }
}
