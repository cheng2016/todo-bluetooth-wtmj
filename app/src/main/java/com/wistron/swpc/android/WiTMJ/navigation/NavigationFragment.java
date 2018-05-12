package com.wistron.swpc.android.WiTMJ.navigation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.google.maps.android.PolyUtil;
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
import com.wistron.swpc.android.WiTMJ.bean.Records;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.BluetoothState;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.DeviceList;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.dao.DbWorkOutDao;
import com.wistron.swpc.android.WiTMJ.dao.RecordsDao;
import com.wistron.swpc.android.WiTMJ.http.response.AddWorkResponse;
import com.wistron.swpc.android.WiTMJ.http.response.GetWorkResponse;
import com.wistron.swpc.android.WiTMJ.http.response.Profile;
import com.wistron.swpc.android.WiTMJ.listener.onFragmentListener;
import com.wistron.swpc.android.WiTMJ.train.TrainEditFragment;
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
import retrofit2.Response;

/**
 * Created by WH1604041 on 2016/5/4.
 */

public class NavigationFragment extends BaseMapFragment implements RoutingListener {
    private final static String TAG = "NavigationFragment";
    private TextView tv_start;
    private TextView tv_complete;
    private TextView tv_cancel;
    private TextView tv_ok;
    private onFragmentListener listener;
    // private MarkerOptions marker;//我的位置
    private List<Route> mList;
    private ListView mListView;
    private List<Segment> mListSegment;
    private ListView mListSegmentView;
    private DbWorkOutDao workoutDao;
    private LegAdapter legAdapter;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private LinearLayout navigation_map_layout;
    private PreferencesUtil mPreferencesUtil;
    private GetWorkResponse workOut;
    private TmjConnection connection;
    private JsonObject jsonObject;//workout json
    private DbWorkOut dbWorkOut;
    private RecordsDao recordsDao;
    private ImageView nav_swap;
    private Boolean firstLocationUpdate = true;
    private long startTime;
    private long completeTime;

    private String srtTime;
    private Location currLocation;
    private Boolean naviDetailModel = false;
    private Marker marker;
    private Boolean isSendKeepModel = false;
    private List<LatLng> segmentPoints;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (onFragmentListener) activity;
    }

    private Route mRoute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        mFragment = getActivity().getLayoutInflater().inflate(R.layout.fragment_navigation, null);
        mapView = (MapView) mFragment.findViewById(R.id.navigation_mapview);
        mapView.onCreate(savedInstanceState);
        initView();
        initEvent();
        workoutDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getDbWorkOutDao();
        recordsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getRecordsDao();
        connection = TmjClient.create();
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
        mPreferencesUtil = new PreferencesUtil(getActivity(),
                PreferenceConstants.HOME_CYCLING_TABLE);
        starting = (AutoCompleteTextView) mFragment.findViewById(R.id.navigation_et_start);
        destination = (AutoCompleteTextView) mFragment.findViewById(R.id.navigation_et_end);
        tv_ok = (TextView) mFragment.findViewById(R.id.tv_ok);
        tv_start = (TextView) mFragment.findViewById(R.id.tv_start);
        tv_complete = (TextView) mFragment.findViewById(R.id.tv_complete);
        tv_cancel = (TextView) mFragment.findViewById(R.id.tv_cancel);
        mListView = (ListView) mFragment.findViewById(R.id.nav_list1);
        mListSegmentView = (ListView) mFragment.findViewById(R.id.nav_listsegment);
        navigation_map_layout = (LinearLayout) mFragment.findViewById(R.id.navigation_map_layout);
        nav_swap = (ImageView) mFragment.findViewById(R.id.nav_swap);
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
                //add by wr
                currLocation = location;
                if (JZLocationConverter.outOfChina(location.getLatitude(), location.getLongitude())) {

                }else {//如果在国内，对坐标进行纠偏
                    JZLocationConverter.LatLng ll = new JZLocationConverter.LatLng(location.getLatitude(), location.getLongitude());
                    JZLocationConverter.LatLng latLng = JZLocationConverter.wgs84ToGcj02(ll);
                    LatLng target = new LatLng(latLng.getLatitude(), latLng.getLongitude());//转换后的新坐标
                    currLocation.setLatitude(target.latitude);
                    currLocation.setLongitude(target.longitude);
                }
                navigation(currLocation);

                if(isSendKeepModel){
                    LatLng locationLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    LogUtils.e(TAG,"segmentPoints====="+segmentPoints.toString());
                    boolean isLocationOnPath =  PolyUtil.isLocationOnPath(locationLatLng, segmentPoints, true, 20);
                    if(isLocationOnPath){
                        MainActivity mainActivity = (MainActivity) NavigationFragment.this.getActivity();
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("type", "navigation");
                        jsonObject.addProperty("status", "update");
                        jsonObject.addProperty("nav_road", "0");
                        jsonObject.addProperty("nav_distance", "0");
                        jsonObject.addProperty("nav_direction", "keep");
                        LogUtils.e(TAG, jsonObject.toString());
                        mainActivity.getBluetoothSPP().send(jsonObject.toString(), true);

                        Toast.makeText(NavigationFragment.this.getContext(), "=======send keep=======", Toast.LENGTH_LONG).show();
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
                LocationManager.NETWORK_PROVIDER, 5000, 0,
                locationListener);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000, 0, locationListener);

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                android.view.ViewGroup.LayoutParams layoutParams = navigation_map_layout.getLayoutParams();
                //                layoutParams.height=320;
                if(TmjApplication.getInstance().isConneced()){
//                    if (!naviDetailModel && mRoute != null) {
//                        ((MainActivity) getActivity()).startFragmentAndFinish(NavigationFragment.this, TrainEditFragment.class);
//                        return;
//                    }

                    if (CommonUtil.Operations.isOnline(mContext)) {
                        route();
                    } else {
                        Toast.makeText(mContext, "No internet connectivity", Toast.LENGTH_SHORT).show();
                    }
                }else{
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
        nav_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapLocation();
            }
        });
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                workOut = new GetWorkResponse();
                final String startPlace = starting.getText().toString();
                final String endPlace = destination.getText().toString();

                new AsyncTask<Void, Void, Long>() {
                    @Override
                    protected void onPreExecute() {
                        showProgress("Post route information");//add by wr
                    }

                    @Override
                    protected Long doInBackground(Void... params) {
                        jsonObject = new JsonObject();
                        jsonObject.addProperty("tag", Constants.TAG_NAVIGATION);
                        jsonObject.addProperty("permission", Constants.PERMISSION_PUBLIC);
                        jsonObject.addProperty("name", "test");
                        jsonObject.addProperty("start_date", CommonUtil.getServerDateFormat(new Date()));
                        jsonObject.addProperty("end_date", CommonUtil.getServerDateFormat(new Date()));
                        jsonObject.addProperty("start_location", startPlace + "|" + mRoute.getStartPoint().longitude + "," + mRoute.getStartPoint().latitude);
                        jsonObject.addProperty("end_location", endPlace + "|" + mRoute.getEndPoint().longitude + "," + mRoute.getEndPoint().latitude);
                        // jsonObject.addProperty("route",mRoute.getRouteString());
                        jsonObject.addProperty("participants", "");
                        jsonObject.addProperty("note", "");
                        JsonParser parser = new JsonParser();
                        JsonElement je_mRoute = parser.parse(mRoute.getRouteString());
                        jsonObject.add("route", je_mRoute);
                        dbWorkOut = new DbWorkOut();
                        dbWorkOut.setJsonStr(jsonObject.toString());
                        dbWorkOut.setStatus(Constants.STATUS_INIT);
                        LogUtils.i(TAG,"JsonObject ========================="+jsonObject.toString());
                        LogUtils.d(TAG, "dbWorkOut : " + dbWorkOut);
                        LogUtils.d(TAG, "postWorkOut JsonObject : " + jsonObject.toString());
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
                                hideProgress();//add by wr
                                if (response.isSuccessful()) {
                                    L.i(TAG, "postWorkOut：" + response.code() + "\t" + response.message());
                                    dbWorkOut.setWorkoutId(response.body().getWorkout_id());
                                    dbWorkOut.setId(db_id);
                                    dbWorkOut.setRecordId(response.body().getRecord_id());
                                    dbWorkOut.setStatus(1);

                                    workoutDao.insertOrReplace(dbWorkOut);
                                    LogUtils.d(TAG, "isSuccessful response.body().getId()=================  : " + response.body().getWorkout_id());
                                    tv_start.setVisibility(View.GONE);
                                    tv_complete.setVisibility(View.VISIBLE);
                                    tv_cancel.setVisibility(View.VISIBLE);
                                    mListView.setVisibility(View.GONE);
                                    mListSegmentView.setVisibility(View.VISIBLE);
                                    legAdapter = new LegAdapter(mContext, mListSegment);
                                    mListSegmentView.setAdapter(legAdapter);
                                    //add by wr
                                    startTime = System.currentTimeMillis();
                                    naviDetailModel = true;
                                } else {
                                    L.d(TAG, "Failure=================  " + response.code() + " " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<AddWorkResponse> call, Throwable t) {
                                L.d(TAG, "Failure    " + t.getMessage());
                                hideProgress();//add by wr
                            }
                        });
                    }
                }.execute();
            }
        });
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog("Complete now?", "", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        new AsyncTask<Void, Void, Long>() {
                            @Override
                            protected Long doInBackground(Void... params) {
                                completeTime = System.currentTimeMillis();//add by wr
                                //modify by wr
//                                Double distance = new Double(mRoute.getDistanceValue());
                                double subDistance = 0;
                                int num = 0;
                                //获取已发送send的segment总个数
                                for (int i = 0; i < mRoute.getSegments().size(); i++) {
                                    Segment segment = mRoute.getSegments().get(i);
                                    if (segment.isSend() == true) {
                                        num++;
                                    }
                                }
                                //获取已发送send的segment的个数-1的距离
                                Double distance = Double.valueOf(0);
                                if (num == 0) {

                                }else{
                                    for(int i = 0; i < num-1; i++){
                                        Segment segment = mRoute.getSegments().get(i);
                                        subDistance += segment.getDistance();
                                    }
                                    //获取已发送send的最后一个segment
                                    Segment segment = mRoute.getSegments().get(num - 1);
                                    double lastPointDistance = CommonUtil.getDistance(currLocation.getLatitude(), currLocation.getLongitude()
                                            , segment.startPoint().latitude, segment.startPoint().longitude);
                                    distance = Double.valueOf(subDistance + lastPointDistance);
                                }

//                                Double duration = new Double(mRoute.getDurationValue());
                                double durationMillis = (completeTime - startTime) / 1000;
                                Double duration = Double.valueOf(durationMillis);
                                Double speed = distance / duration;
                                Double calories = distance;


                                final Records records = new Records();
                                records.setTag(Constants.TAG_NAVIGATION);
                                records.setCalories(calories + "");
                                records.setAvg_speed(speed + "");
                                records.setDuration(duration + "");
                                records.setDistance(distance + "");
                                LogUtils.d(TAG, "nav==duration=" + duration);
                                LogUtils.d(TAG, "nav==distance=" + distance);
                                LogUtils.d(TAG, "nav==speed=" + speed);
                                LogUtils.d(TAG, "nav==calories=" + calories);
                                records.setRoutes(mRoute.getRouteString());
                                String startTimeStrTmp = CommonUtil.getServerDateFormat(new Date());
                                srtTime = startTimeStrTmp;

                                records.setStart_time(srtTime);
                                records.setEnd_time(CommonUtil.getServerDateFormat(new Date()));

                                records.setRank(0);
                                records.setRecord_id(dbWorkOut.getRecordId());
                                long recordId = recordsDao.insertOrReplace(records);
                                Gson g2 = new Gson(); //new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//                                records.setRoutes(null);
                                LogUtils.d(TAG, "record json : " + g2.toJson(records));
                                JsonParser parser = new JsonParser();
                                JsonObject jo = parser.parse(g2.toJson(records)).getAsJsonObject();
                                String act = PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN, "");
                                Call<Message> call = connection.putRecord(act, records.getRecord_id(), jo);
                                call.enqueue(new Callback<Message>() {
                                    @Override
                                    public void onResponse(Call<Message> call, Response<Message> response) {
                                        L.d(TAG, "response=================  " + response.code() + " " + response.message());
                                        if (response.isSuccessful()) {
                                            CommonUtil.setRecordPlus(mPreferencesUtil, records);//写入累加数据到配置文件
                                        } else {
                                            L.d(TAG, "Failure=================  " + response.code() + " " + response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Message> call, Throwable t) {
                                        L.d(TAG, "Failure    " + t.getMessage());
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
                                i.putExtra("TAG_NAVIGATION_CMPLETE_TO_DETAIL", Constants.TAG_NAVIGATION_CMPLETE_TO_DETAIL);
                                bundle.putString("recordId", dbWorkOut.getRecordId());
                                locationManager.removeUpdates(locationListener);
                                dialog.dismiss();
                                i.putExtras(bundle);
                                i.setClass(getActivity(), NavigationDetailActivity.class);
                                startActivity(i);
                                naviDetailModel = false;//add by wr
                            }
                        }.execute();
                        //  BluetoothLeService bluetoothLeService = new BluetoothLeService();

                    }
                });

            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

            }
        });
    }



    public void route() {
        if (map == null) {
            Toast.makeText(this.getActivity(), "map is not ready.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (start == null || end == null) {
            if (start == null) {
                LogUtils.d(TAG, "start===" + start + "+++starting===" + starting.getText().toString());
                if (starting.getText().length() > 0) {
                    starting.setError("Choose location from dropdown.");

                } else {
                    Toast.makeText(this.getActivity(), "Please choose a starting point.", Toast.LENGTH_SHORT).show();
                    tv_start.setVisibility(View.GONE);
                }
            }
            if (end == null) {
                LogUtils.d(TAG, "end===" + end + "+++destination===" + destination.getText().toString());
                if (destination.getText().length() > 0) {
                    destination.setError("Choose location from dropdown.");
                } else {
                    Toast.makeText(this.getActivity(), "Please choose a destination.", Toast.LENGTH_SHORT).show();
                    tv_ok.setVisibility(View.VISIBLE);
                    tv_start.setVisibility(View.GONE);
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
    public void onRoutingSuccess(List<Route> route, int shortestRouteIndex) {
        hideProgress();
        //modify by wr
//        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
//        map.moveCamera(center);
//        map.animateCamera(zoom);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) navigation_map_layout.getLayoutParams();
        layoutParams.height = 600;

        drawLine(route.get(0));

        mList = new ArrayList<Route>();
        mListSegment = new ArrayList<Segment>();
        mList.addAll(route);
        mListSegment.addAll(route.get(0).getSegments());
        mListView.setVisibility(View.VISIBLE);
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

        tv_ok.setVisibility(View.GONE);
        tv_start.setVisibility(View.VISIBLE);
        tv_complete.setVisibility(View.GONE);
        tv_cancel.setVisibility(View.GONE);

        ((MainActivity) getActivity()).startNavigationMode("navigation", "init", "");
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
        if (map != null) {
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);
            //Toast.makeText(ApplicationHolder.getApplication(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            moveRouteInCameraCenter(route);
        }
    }

    //将路线设置在地图可视范围内 add by WR
    private void moveRouteInCameraCenter(Route route) {
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
            if (firstLocationUpdate) { //modify by wr
                map.moveCamera(center);
                map.animateCamera(zoom);
                firstLocationUpdate = false;
            }

        } else {
//            JZLocationConverter.LatLng ll = new JZLocationConverter.LatLng(location.getLatitude(), location.getLongitude());
//            JZLocationConverter.LatLng latLng = JZLocationConverter.wgs84ToGcj02(ll);
//            LatLng target = new LatLng(latLng.getLatitude(), latLng.getLongitude());//转换后的新坐标
//            //add by wr
//            currLocation.setLatitude(target.latitude);
//            currLocation.setLongitude(target.longitude);
            LatLng target = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
            CameraUpdate center = CameraUpdateFactory.newLatLng(target);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

            CameraPosition position = map.getCameraPosition();

            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(12);
            builder.target(target);
            if (marker != null) {
                marker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions().position(target);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            marker = map.addMarker(markerOptions);

            if (firstLocationUpdate) {  //modify by wr
                map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                map.moveCamera(center);
                map.animateCamera(zoom);
                firstLocationUpdate = false;
            }
        }

        if (mRoute != null && naviDetailModel == true) { //modify by wr
            LogUtils.i(TAG,"nav==mroute=="+mRoute);
            LogUtils.i(TAG,"nav==getDistanceText=="+mRoute.getDistanceText());
            LogUtils.i(TAG,"nav==getDistanceValue=="+mRoute.getDistanceValue());
            LogUtils.i(TAG,"nav==getDurationText=="+mRoute.getDurationText());
            LogUtils.i(TAG,"zlb==getDurationValue=="+mRoute.getDurationValue());

            for (int i = 0; i < mRoute.getSegments().size(); i++) {
                Segment segment = mRoute.getSegments().get(i);
//                if (segment.getManeuver() == null || segment.isSend()) continue;
                //modify by wr
                if (segment.isSend()) continue;
                double distance = CommonUtil.getDistance(currLocation.getLatitude(), currLocation.getLongitude()
                        , segment.startPoint().latitude, segment.startPoint().longitude);
                if (distance <= 100) {
                    MainActivity mainActivity = (MainActivity) this.getActivity();
//                    if (!TextUtils.isEmpty(segment.getManeuver())) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", "navigation");
                    if (i == 0) {
                        jsonObject.addProperty("status", "init");
                    } else {
                        jsonObject.addProperty("status", "update");
                    }
                    jsonObject.addProperty("nav_road", segment.getInstruction());
                    jsonObject.addProperty("nav_distance", "" + (int)distance);
                    if (!TextUtils.isEmpty(segment.getManeuver())) {
                        jsonObject.addProperty("nav_direction", CommonUtil.rePlaceManeuver(segment.getManeuver()));
                        LogUtils.e(TAG, jsonObject.toString());
                        mainActivity.getBluetoothSPP().send(jsonObject.toString(), true);

                        isSendKeepModel = true;
                        segmentPoints = segment.getPoints();
                    }
                    segment.setSend(true);
                    //modify by wr
                    if (legAdapter != null && segment.getManeuver() != null) {
//                            legAdapter.notifyDataSetChanged();
                        legAdapter.updateLegItem(i, mListSegmentView);
                    }
                    break;
//                    }
                }
            }
        }
    }

    public void finishNavigation() {

    }

    /**
     * TrainEditFragment中查看Navigation progress
     *
     * @param mRoute
     */
    public void setShowTrainProgress(final Route mRoute, Context context) {
        this.mRoute = mRoute;
        L.i(TAG, "setTrainProgress");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) navigation_map_layout.getLayoutParams();
                layoutParams.height = 600;

                String startPlace = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_START, "");
                String endPlace = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.TRAINING_CYCLING_END, "");
                starting.setText(startPlace);
                destination.setText(endPlace);
                drawLine(mRoute);
                mListView.setVisibility(View.GONE);
                mListSegmentView.setVisibility(View.VISIBLE);
                mListSegment = new ArrayList<Segment>();
                mListSegment.addAll(mRoute.getSegments());
                legAdapter = new LegAdapter(mContext, mListSegment);
                mListSegmentView.setAdapter(legAdapter);

                tv_start.setVisibility(View.VISIBLE);
                tv_start.setText("Ok");
                tv_complete.setVisibility(View.GONE);
                tv_cancel.setVisibility(View.GONE);
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) getActivity()).startFragmentAndFinish(NavigationFragment.this, TrainEditFragment.class);
                        T.showShort("tv_ok");
                    }
                });
            }
        },2000);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        L.i(TAG,"onDestroy");
        //add by wr
        firstLocationUpdate = true;
        naviDetailModel = false;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
    }

    @Override
    public void initData() {
        super.initData();
    }
}