package com.wistron.swpc.android.WiTMJ.train;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;
import com.wistron.swpc.android.WiTMJ.ApplicationHolder;
import com.wistron.swpc.android.WiTMJ.CommonUtil;
import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.LogUtils;
import com.wistron.swpc.android.WiTMJ.MainActivity;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.base.BaseMapFragment;
import com.wistron.swpc.android.WiTMJ.bean.DbWorkOut;
import com.wistron.swpc.android.WiTMJ.bean.Records;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.BluetoothState;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.DeviceList;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.dao.DbWorkOutDao;
import com.wistron.swpc.android.WiTMJ.dao.RecordsDao;
import com.wistron.swpc.android.WiTMJ.http.response.AddWorkResponse;
import com.wistron.swpc.android.WiTMJ.http.response.GetWorkResponse;
import com.wistron.swpc.android.WiTMJ.listener.onFragmentListener;
import com.wistron.swpc.android.WiTMJ.navigation.LegAdapter;
import com.wistron.swpc.android.WiTMJ.navigation.RouteAdapter;
import com.wistron.swpc.android.WiTMJ.util.JZLocationConverter;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;
import com.wistron.swpc.android.WiTMJ.util.googlemap.AbstractRouting;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Route;
import com.wistron.swpc.android.WiTMJ.util.googlemap.RouteException;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Routing;
import com.wistron.swpc.android.WiTMJ.util.googlemap.RoutingListener;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Segment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by WH1604041 on 2016/5/5.
 */
public class TrainFragment extends BaseMapFragment implements RoutingListener {
    private TextView start_training;
    private TextView ok_training;
    private View mFragment;
    private onFragmentListener listener;
    private final static String TAG = "TrainFragment";
    private EditText train_et_speed;
    private EditText train_et_distance;
    private EditText train_et_hr;
    private EditText train_et_min;
    private EditText train_et_cal;
    private RecordsDao recordsDao;
    private PreferencesUtil mPreferencesUtil;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GetWorkResponse workOut;
    private JsonObject jsonObject;//workout json
    private DbWorkOut dbWorkOut;
    private DbWorkOutDao workoutDao;
    private TmjConnection connection;
    private LegAdapter legAdapter;
    private Route mRoute;
    private ListView mListView;
    private List<Segment> mListSegment;
    private ListView mListSegmentView;
    private List<Route> mList;
    private Records records;
    private ImageView iv_transform;

    private Location currLocation;
    private Boolean firstLocationUpdate = true;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (onFragmentListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
        mContext = this.getActivity();
        mFragment = getActivity().getLayoutInflater().inflate(R.layout.fragment_train, null);
        mapView = (MapView) mFragment.findViewById(R.id.train_mapView);
        mapView.onCreate(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup p = (ViewGroup) mFragment.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            return;
    }

    @Override
    public void initView() {
        super.initView();
        connection = TmjClient.create();
        mPreferencesUtil = new PreferencesUtil(getActivity(), PreferenceConstants.HOME_CYCLING_TABLE);
        workoutDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getDbWorkOutDao();
        recordsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getRecordsDao();
        iv_transform = (ImageView) mFragment.findViewById(R.id.iv_transform);
        ok_training = (TextView) mFragment.findViewById(R.id.ok_training);
        start_training = (TextView) mFragment.findViewById(R.id.start_training);
        starting = (AutoCompleteTextView) mFragment.findViewById(R.id.train_et_start);
        destination = (AutoCompleteTextView) mFragment.findViewById(R.id.train_et_end);
        train_et_speed = (EditText) mFragment.findViewById(R.id.train_et_speed);
        train_et_distance = (EditText) mFragment.findViewById(R.id.train_et_distance);
        train_et_hr = (EditText) mFragment.findViewById(R.id.train_et_hr);
        train_et_min = (EditText) mFragment.findViewById(R.id.train_et_min);
        train_et_cal = (EditText) mFragment.findViewById(R.id.train_et_cal);
        mListView = (ListView) mFragment.findViewById(R.id.train_route_lv);
        mListSegmentView = (ListView) mFragment.findViewById(R.id.train_listsegment);
        legAdapter = new LegAdapter(mContext, mListSegment);
        mListSegmentView.setAdapter(legAdapter);
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
                if(firstLocationUpdate){
                    navigation(location);
                    firstLocationUpdate = false;
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
        iv_transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ok_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TmjApplication.getInstance().isConneced()) {
                    if (CommonUtil.Operations.isOnline(mContext)) {
                        route();
                    } else {
                        Toast.makeText(mContext, "No internet connectivity", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showCustomDialog("Erro", "TMJ device not connected, please connect !", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), DeviceList.class);
                            getActivity().startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                            dialog.dismiss();
                        }
                    }, false);
                }
            }
        });
        start_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mSpdNum = train_et_speed.getText().toString();
                String mDisNum = train_et_distance.getText().toString();
                String mDurHr = TextUtils.isEmpty(train_et_hr.getText().toString()) ? "0" : train_et_hr.getText().toString();
                String mDurMin = train_et_min.getText().toString();
                String mCalNum = train_et_cal.getText().toString();

                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_SPEED, mSpdNum);
                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DISTANCE, mDisNum);
                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DURATION_HRS, mDurHr);
                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DURATION_MINS, mDurMin);
                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_CALORIES, mCalNum);

                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_START, starting.getText().toString());
                PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_END, destination.getText().toString());

                workOut = new GetWorkResponse();
                final String startPlace = starting.getText().toString();
                final String endPlace = destination.getText().toString();

                new AsyncTask<Void, Void, Long>() {
                    protected void onPreExecute() {
                        showProgress("Post workout information");//add by wr
                    }

                    @Override
                    protected Long doInBackground(Void... params) {
                        String custom_speed = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_SPEED, "0");
                        String custom_distance = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DISTANCE, "0");
                        String custom_cal = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_CALORIES, "0");
                        String hr = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DURATION_HRS, "0");
                        String min = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_DURATION_MINS, "0");
                        int mHr = Integer.parseInt(hr);
                        int mMin = Integer.parseInt(min);
                        int mDuration = mHr * 60 + mMin;
                        String custom_duration = String.valueOf(mDuration);
                        jsonObject = new JsonObject();
                        jsonObject.addProperty("tag", Constants.TAG_TRAINING);
                        jsonObject.addProperty("permission", Constants.PERMISSION_PUBLIC);
                        jsonObject.addProperty("name", "test");
                        jsonObject.addProperty("start_date", CommonUtil.getServerDateFormat(new Date()));
                        jsonObject.addProperty("end_date", CommonUtil.getServerDateFormat(new Date()));
                        jsonObject.addProperty("start_location", startPlace + "|" + mRoute.getStartPoint().longitude + "," + mRoute.getStartPoint().latitude);
                        jsonObject.addProperty("end_location", endPlace + "|" + mRoute.getEndPoint().longitude + "," + mRoute.getEndPoint().latitude);
                        jsonObject.addProperty("route", mRoute.getRouteString());
                        jsonObject.addProperty("custom_speed", custom_speed);
                        jsonObject.addProperty("custom_distance", custom_distance);
                        jsonObject.addProperty("custom_cal", custom_cal);
                        jsonObject.addProperty("custom_duration", custom_duration);
                        jsonObject.addProperty("participants", "");
                        jsonObject.addProperty("note", "");
                        dbWorkOut = new DbWorkOut();
                        dbWorkOut.setJsonStr(jsonObject.toString());
                        dbWorkOut.setStatus(Constants.STATUS_INIT);
                        LogUtils.d(TAG, "dbWorkOut : " + dbWorkOut);
                        long db_id = workoutDao.insert(dbWorkOut);
                        return db_id;
                    }

                    @Override
                    protected void onPostExecute(final Long db_id) {

                        String act = PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN, "");
                        Call<AddWorkResponse> call = connection.postWorkOut(act, jsonObject);
                        call.enqueue(new Callback<AddWorkResponse>() {
                            @Override
                            public void onResponse(Call<AddWorkResponse> call, retrofit2.Response<AddWorkResponse> response) {
                                hideProgress();
                                if (response.isSuccessful()) {
                                    L.i(TAG, "postTrainingWorkOut：" + response.code() + "\t" + response.message());
                                    L.i(TAG, "postTrainingWorkOut JsonObject : " + jsonObject.toString());
                                    dbWorkOut.setWorkoutId(response.body().getWorkout_id());
                                    dbWorkOut.setId(db_id);
                                    dbWorkOut.setRecordId(response.body().getRecord_id());
                                    String Record_id = response.body().getRecord_id();
                                    PreferencesUtil.setPrefString(TmjApplication.getInstance(), "Record_id", Record_id);
                                    PreferencesUtil.setPrefString(TmjApplication.getInstance(), "mRoute", mRoute.getRouteString());
                                    Double realDistance = new Double(mRoute.getDistanceValue());
                                    Double realDuration = new Double(mRoute.getDurationValue());
                                    Double realSpeed = realDistance / realDuration;
                                    Double realCalories = CommonUtil.getCalories(realDistance);
                                    String distance = String.valueOf(realDistance);
                                    String duration = String.valueOf(realDuration);
                                    String speed = String.valueOf(realSpeed);
                                    String cal = String.valueOf(realCalories);
                                    PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.HOME_CYCLING_SPEED, distance);
                                    PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.HOME_CYCLING_DURATION, duration);
                                    PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.HOME_CYCLING_SPEED, speed);
                                    PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.HOME_CYCLING_CALORIES, cal);
                                    dbWorkOut.setStatus(1);
                                    workoutDao.insertOrReplace(dbWorkOut);
                                    LogUtils.d(TAG, "isSuccessful response.body().getId()  : " + response.body().getWorkout_id());

                                    //成功后跳转s
                                    listener.onStartFragmentWithData(TrainEditFragment.class, mRoute);

//                                    new AsyncTask<Void, Void, Long>() {
//                                        @Override
//                                        protected Long doInBackground(Void... params) {
//                                            Double realDistance = new Double(mRoute.getDistanceValue());
//                                            Double realDuration = new Double(mRoute.getDurationValue());
//                                            Double realSpeed = realDistance / realDuration;
//                                            Double realCalories = CommonUtil.getCalories(realDistance);
//                                            String distance = String.valueOf(realDistance);
//                                            String duration = String.valueOf(realDuration);
//                                            String speed = String.valueOf(realSpeed);
//                                            String cal = String.valueOf(realCalories);
//                                            PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.HOME_CYCLING_SPEED, distance);
//                                            PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.HOME_CYCLING_DURATION, duration);
//                                            PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.HOME_CYCLING_SPEED, speed);
//                                            PreferencesUtil.setPrefString(getActivity(), PreferenceConstants.HOME_CYCLING_CALORIES, cal);
//                                            records = new Records();
//                                            records.setTag(Constants.TAG_TRAINING);
//                                            records.setCalories(realCalories + "");
//                                            records.setAvg_speed(realSpeed + "");
//                                            records.setDuration(realDuration + "");
//                                            records.setDistance(realDistance + "");
//                                            records.setRoutes(mRoute.getRouteString());
//                                            records.setStart_time(new Date() + "");
//                                            records.setEnd_time(new Date() + "");
//                                            records.setRank(0);
//                                            records.setRecord_id(dbWorkOut.getRecordId());
//                                            long recordId = recordsDao.insertOrReplace(records);
//                                            Gson g2 = new Gson(); //new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//                                            records.setRoutes(null);
//                                            LogUtils.d(TAG, "record json : " + g2.toJson(records));
//                                            JsonParser parser = new JsonParser();
//                                            JsonObject jo = parser.parse(g2.toJson(records)).getAsJsonObject();
//                                            String act = PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN, "");
//                                            Call<Message> call = connection.putRecord(act, records.getRecord_id(), jo);
//                                            call.enqueue(new Callback<Message>() {
//                                                @Override
//                                                public void onResponse(Call<Message> call, retrofit2.Response<Message> response) {
//                                                    L.d(TAG, "response=================  " + response.code() + " " + response.message());
//                                                    if (response.isSuccessful()) {
//                                                        CommonUtil.setRecordPlus(mPreferencesUtil, records);//写入累加数据到配置文件
//                                                    } else {
//                                                        L.d(TAG, "Failure=================  " + response.code() + " " + response.message());
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onFailure(Call<Message> call, Throwable t) {
//                                                    L.d(TAG, "Failure    " + t.getMessage());
//                                                }
//                                            });
//                                            return recordId;
//                                        }
//
//                                        @Override
//                                        protected void onPostExecute(Long recordId) {
//                                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                                                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                                return;
//                                            }
//                                        }
//                                    }.execute();
                                } else {
                                    L.d(TAG, "Failure  " + response.code() + " " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<AddWorkResponse> call, Throwable t) {
                                hideProgress();
                                L.d(TAG, "Failure    " + t.getMessage());
                                T.showShort("Failure");
                            }
                        });
                    }
                }.execute();
            }
        });

    }

    @Override
    public void initData() {

    }

    public void drawLine(Route route) {
        mRoute = route;
        if (polylines.size() > 0) {
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
        //Toast.makeText(ApplicationHolder.getApplication(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
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
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onRoutingStart() {
        showProgress("Fetching route information");
    }

    @Override
    public void onRoutingSuccess(List<Route> route, int shortestRouteIndex) {
        Gson gson = new Gson();
        L.i(TAG,gson.toJson(route.get(0)));

        hideProgress();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        map.moveCamera(center);
        map.animateCamera(zoom);
        drawLine(route.get(0));
        mList = new ArrayList<Route>();
        mListSegment = new ArrayList<Segment>();
        mList.addAll(route);
        mListSegment.addAll(route.get(0).getSegments());
        mListView.setAdapter(new RouteAdapter(mContext, mList));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
        ok_training.setVisibility(View.GONE);
        start_training.setVisibility(View.VISIBLE);


        String mSpdNum = TextUtils.isEmpty(train_et_speed.getText().toString()) ? "0" : train_et_speed.getText().toString();
        String mDisNum = TextUtils.isEmpty(train_et_distance.getText().toString()) ? "0" : train_et_distance.getText().toString();

        String mDurHr = TextUtils.isEmpty(train_et_hr.getText().toString()) ? "0" : train_et_hr.getText().toString();
        String mDurMin = TextUtils.isEmpty(train_et_min.getText().toString()) ? "0" : train_et_min.getText().toString();


        String mCalNum = train_et_cal.getText().toString();

        int durationValue = (Integer.parseInt(mDurHr) * 60 * 60 * 1000) + (Integer.parseInt(mDurMin) * 60 * 1000);

        ((MainActivity) getActivity()).startTrainMode("train", "init", durationValue + "", mCalNum, mDisNum, mSpdNum);
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
    public void onRoutingCancelled() {

    }

    public void navigation(Location location) {
        currLocation = location;
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

            currLocation.setLatitude(target.latitude);
            currLocation.setLongitude(target.longitude);

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
            firstLocationUpdate = false;
        }

    }

    public void shareLine(Bitmap bitmap) {
        ComponentName cn = new ComponentName("jp.naver.line.android"
                , "jp.naver.line.android.activity.selectchat.SelectChatActivity");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null, null));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        shareIntent.setComponent(cn);
        getActivity().startActivity(Intent.createChooser(shareIntent, "TrainingActivity结果分享"));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        L.i(TAG,"onDestroy");
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }
}
