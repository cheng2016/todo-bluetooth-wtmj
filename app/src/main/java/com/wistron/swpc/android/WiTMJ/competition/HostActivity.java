package com.wistron.swpc.android.WiTMJ.competition;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.timessquare.CalendarPickerView;
import com.wistron.swpc.android.WiTMJ.ApplicationHolder;
import com.wistron.swpc.android.WiTMJ.CommonUtil;
import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.LoginActivity;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.adapter.SpinnerArrayAdapter;
import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.bean.Host;
import com.wistron.swpc.android.WiTMJ.bean.Participants;
import com.wistron.swpc.android.WiTMJ.bean.WorkOut;
import com.wistron.swpc.android.WiTMJ.dao.ParticipantsDao;
import com.wistron.swpc.android.WiTMJ.dao.WorkOutDao;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.http.HttpImpl;
import com.wistron.swpc.android.WiTMJ.http.request.WorkOutRequest;
import com.wistron.swpc.android.WiTMJ.http.response.AddWorkResponse;
import com.wistron.swpc.android.WiTMJ.listener.onCalendarSelectListener;
import com.wistron.swpc.android.WiTMJ.listener.onTimePickerSelectListener;
import com.wistron.swpc.android.WiTMJ.util.DateUtil;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;
import com.wistron.swpc.android.WiTMJ.util.googlemap.AbstractRouting;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Route;
import com.wistron.swpc.android.WiTMJ.util.googlemap.RouteException;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Routing;
import com.wistron.swpc.android.WiTMJ.util.googlemap.RoutingListener;
import com.wistron.swpc.android.WiTMJ.widget.CalendarDialog;
import com.wistron.swpc.android.WiTMJ.widget.CustomDialog;
import com.wistron.swpc.android.WiTMJ.widget.FlowLayout;
import com.wistron.swpc.android.WiTMJ.widget.FlowTextView;
import com.wistron.swpc.android.WiTMJ.widget.TimePickerDialog;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by WH1604025 on 2016/4/21.
 */
public class HostActivity extends BaseActivity implements  View.OnClickListener, RoutingListener {
    private final static String TAG = "HostActivity";

    private ActionBar actionBar;

    private final static int REQUEST_PLACE_PICKER_START = 201;
    private final static int REQUEST_PLACE_PICKER_END = 202;
    private final static int REQUEST_ADD_USER = 203;

    private boolean addHost = true;//是否为host界面，true代表是host界面，false代表是edit界面

    private TextView  titleTv, startPlaceTv, endPlaceTv;

    private TextView start_dateTv, end_dateTv,start_timeTv,end_timeTv;

    private EditText nameTv, noteEdit;

    private PercentRelativeLayout backBtn;

    private ImageView addImg;

    private Button okBtn, deleteBtn;

    private FlowLayout fowLayout;


    private Spinner mSpinner;

    private LatLng startLaLng, endLaLng;

    private WorkOutDao workOutDao;
    private ParticipantsDao participantsDao;

    private WorkOut workOut = new WorkOut();

    private int tag = 4, permission = 0;

    private String name, note;

    private String start_date, end_date,start_time,end_time;

    private String start_date_detail,end_date_detail;

    private String startPlace, endPlace;

    private String custom_cal = "", custom_distance = "", custom_speed = "", custom_duration = "";

    private List<Participants> participantsList = new ArrayList<Participants>();

    private String routes;

    private long durationValue = 3000000;

    private String workout_id,accessToken;

    private Calendar nextYear = Calendar.getInstance();
    private Calendar lastYear = Calendar.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_test);

        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.app_actionbar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);

        initView();
        initEvent();
        initData();

        lastYear.add(Calendar.YEAR, -1);
        nextYear.add(Calendar.YEAR, 1);
    }

    @Override
    public void initView() {
        titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.titleTv);

        start_dateTv = (TextView) findViewById(R.id.startdateTv);
        end_dateTv = (TextView) findViewById(R.id.enddateTv);
        start_timeTv = (TextView) findViewById(R.id.starttimeTv);
        end_timeTv = (TextView) findViewById(R.id.endtimeTv);


        backBtn = (PercentRelativeLayout) actionBar.getCustomView().findViewById(R.id.backBtn);
        addImg = (ImageView) findViewById(R.id.addImg);

        okBtn = (Button) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(this);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(this);

        fowLayout = (FlowLayout) findViewById(R.id.fowLayout);

        mSpinner = (Spinner) findViewById(R.id.spinner);

        String[] mStringArray = getResources().getStringArray(R.array.hobby);
        //使用自定义的ArrayAdapter
        ArrayAdapter<String> mAdapter = new SpinnerArrayAdapter(this, mStringArray);
        //设置下拉列表风格(这句不些也行)
        //mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        //监听Item选中事件
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                permission = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startPlaceTv = (TextView) findViewById(R.id.startPlaceTv);
        endPlaceTv = (TextView) findViewById(R.id.endPlaceTv);
        startPlaceTv.setOnClickListener(this);
        endPlaceTv.setOnClickListener(this);

        nameTv = (EditText) findViewById(R.id.nameTv);
        noteEdit = (EditText) findViewById(R.id.noteEdit);
    }



    @Override
    public void initEvent() {
        fowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HostActivity.this, AddTagActivity.class);
                intent.putExtra("participantsList", (Serializable) participantsList);
                startActivityForResult(intent, REQUEST_ADD_USER);
            }
        });

        start_dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CalendarDialog(HostActivity.this).builder().setListener(new onCalendarSelectListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                        if(month<10){
                            start_dateTv.setText(year + "-" + "0" + ++month + "-" + dayOfMonth);
                            if(dayOfMonth<10){
                                start_dateTv.setText(year + "-" + "0" + ++month + "-" + "0" +dayOfMonth);
                            }
                        }else{
                            start_dateTv.setText(year + "-" + ++month + "-" + dayOfMonth);
                            if(dayOfMonth<10){
                                start_dateTv.setText(year + "-" + ++month + "-" + "0" +dayOfMonth);
                            }
                        }
                    }
                });

//                showCalendarInDialog("", R.layout.dialog_multi_calendar);
//                dialogView.init(new Date(), nextYear.getTime()) //
//                        .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
//                        .withSelectedDate(new Date());
            }
        });

        end_dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CalendarDialog(HostActivity.this).builder().setListener(new onCalendarSelectListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                        if(month<10){
                            end_dateTv.setText(year + "-" + "0" + ++month + "-" + dayOfMonth);
                            if(dayOfMonth<10){
                                end_dateTv.setText(year + "-" + "0" + ++month + "-" + "0" +dayOfMonth);
                            }
                        }else{
                            end_dateTv.setText(year + "-" + ++month + "-" + dayOfMonth);
                            if(dayOfMonth<10){
                                end_dateTv.setText(year + "-" + ++month + "-" + "0" +dayOfMonth);
                            }
                        }
                    }
                });
            }
        });

        start_timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(HostActivity.this).builder().setListener(new onTimePickerSelectListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        String time="";
                        if(hourOfDay<10){
                            time = "0" + hourOfDay + ":" + minute + ":00";
                            if(minute<10){
                                time = "0" + hourOfDay + ":0" + minute + ":00";
                            }
                        }else{
                            time = hourOfDay + ":" + minute + ":00";
                            if(minute<10){
                                time = hourOfDay + ":0" + minute + ":00";
                            }
                        }
                        start_timeTv.setText(time);
                    }
                });
            }
        });

        end_timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(HostActivity.this).builder().setListener(new onTimePickerSelectListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        String time="";
                        if(hourOfDay<10){
                            time = "0" + hourOfDay + ":" + minute + ":00";
                            if(minute<10){
                                time = "0" + hourOfDay + ":0" + minute + ":00";
                            }
                        }else{
                            time = hourOfDay + ":" + minute + ":00";
                            if(minute<10){
                                time = hourOfDay + ":0" + minute + ":00";
                            }
                        }
                        end_timeTv.setText(time);
                    }
                });
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HostActivity.this, AddTagActivity.class);
                intent.putExtra("participantsList", (Serializable) participantsList);
                startActivityForResult(intent, REQUEST_ADD_USER);
            }
        });
    }

    private CalendarPickerView dialogView;
    private AlertDialog theDialog;

    private void showCalendarInDialog(String title, int layoutResId) {
        dialogView = (CalendarPickerView) getLayoutInflater().inflate(layoutResId, null, false);

        theDialog = new AlertDialog.Builder(this) //
//                .setTitle(title)
                .setView(dialogView)
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        StringBuilder dateBuilder = new StringBuilder();
                        for (Date date:dialogView.getSelectedDates()) {
                            dateBuilder.append(sdf.format(date)+" ");
                        }
                        T.showLong(HostActivity.this,dateBuilder.toString());
                    }
                })
                .create();
        theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                Log.d(TAG, "onShow: fix the dimens!");
                dialogView.fixDialogDimens();
            }
        });
        theDialog.show();
    }

    @Override
    public void initData() {
        accessToken = PreferencesUtil.getPrefString(HostActivity.this, PreferenceConstants.ACCESS_TOKEN, "");

        workOutDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getWorkOutDao();

        participantsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getParticipantsDao();

        if (getIntent().getExtras() != null) {
            addHost = false;
            titleTv.setText(getResources().getString(R.string.edit));
            okBtn.setText(getResources().getString(R.string.update));
            workout_id = getIntent().getExtras().getString("workout_id");
            workOut = workOutDao.queryBuilder().where(WorkOutDao.Properties.Workout_id.eq(workout_id)).unique();
            initWorkOut(workOut);
        } else {
            addHost = true;
            titleTv.setText(getResources().getString(R.string.host));
            okBtn.setText(getResources().getString(R.string.ok));
            workOut.setStart_location("金融港");
            workOut.setEnd_location("软件园");
            initWorkOut(workOut);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.startPlaceTv:
                choosePlace(REQUEST_PLACE_PICKER_START);
                break;
            case R.id.endPlaceTv:
                choosePlace(REQUEST_PLACE_PICKER_END);
                break;
            case R.id.okBtn:
                hideKeyBoard(v);
                //点击update或者ok时先进行线路规划
                route();

                break;
            case R.id.deleteBtn:
                showProgress("delete...");
                HttpImpl.getInstance(HostActivity.this).deleteWorkout(handler,accessToken,workout_id);
                break;
        }
    }

    void route() {
        start_date = start_dateTv.getText().toString();
        end_date = end_dateTv.getText().toString();

        start_time = start_timeTv.getText().toString();
        end_time = end_timeTv.getText().toString();

        name = nameTv.getText().toString();
//        startPlace = startPlaceTv.getText().toString();
//        endPlace = endPlaceTv.getText().toString();
        note = noteEdit.getText().toString();

        start_date_detail = DateUtil.subDateAndTime(start_date, start_time);
        end_date_detail = DateUtil.subDateAndTime(end_date, end_time);

        if (TextUtils.isEmpty(name)) {
            T.showShort("name can not be null !");return;
        } else if (TextUtils.isEmpty(start_date_detail)) {
            T.showShort("date can not be null !");return;
        } else if (TextUtils.isEmpty(end_date_detail)) {
            T.showShort("time can not be null !");return;
        } else if (TextUtils.isEmpty(startPlace)) {
            T.showShort("startPlace can not be null !");return;
        } else if (TextUtils.isEmpty(endPlace)) {
            T.showShort("endPlace can not be null !");return;
        }

        if(startLaLng==null){
            T.showShort("起始地点有误，请重新选择！");
            return;
        }
        if(endLaLng==null){
            T.showShort("结束地点有误，请重新选择！");
            return;
        }
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(startLaLng, endLaLng)
                .language("zh-CN")
                .key(Constants.GOOGLE_DIRECTION_KEY)
                .build();
        routing.execute();
    }

    void choosePlace(int requestCode) {
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, requestCode);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
            e.printStackTrace();
            T.showShort("Google Play Services not repairable");
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
            e.printStackTrace();
            T.showShort("Google Play Services not available");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PLACE_PICKER_START || requestCode == REQUEST_PLACE_PICKER_END) {
                final Place place = PlacePicker.getPlace(data, this);

                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();

                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;

                String attributions = PlacePicker.getAttributions(data);
                if (attributions == null) {
                    attributions = "";
                }
                L.i(TAG, name + "\t\n" + address + "\t\n" + attributions + "\t\n" + latitude + "\t\n" + longitude);
                if (requestCode == REQUEST_PLACE_PICKER_START) {
                    startPlaceTv.setText(name.toString().replace("(", "").replace(")", ""));
                    startLaLng = place.getLatLng();
                    startPlace = name.toString() + "|" +latitude +"," + longitude;
                } else {
                    endPlaceTv.setText(name.toString().replace("(", "").replace(")", ""));
                    endLaLng = place.getLatLng();
                    endPlace = name.toString() + "|" +latitude +"," + longitude;
                }
            }
            if (requestCode == REQUEST_ADD_USER) {
                participantsList = (List<Participants>) data.getExtras().get("participantsList");
                initParticipant(participantsList);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        hideProgress();
        if (e != null) {
            T.showShort("Error: " + e.getMessage());
        } else {
            T.showShort("Something went wrong, Try again");
        }
    }

    @Override
    public void onRoutingStart() {
        showProgress("Fetching route information");
    }

    @Override
    public void onRoutingSuccess(List<Route> route, int shortestRouteIndex) {
//        hideProgress();
        if (route != null) {
            if (route.size() > 0) {
                String durationText = route.get(0).getDurationText();

                durationValue = route.get(0).getDurationValue();
                L.i(TAG, durationValue + "");
                L.i(TAG, durationText);

                JsonParser parser = new JsonParser();
                JsonElement je_mRoute = parser.parse(route.get(0).getRouteString());

                routes = je_mRoute.toString();
                L.i(TAG,routes);

                requestNetWork(je_mRoute);//路线规划成功后请求网络
            } else {
                hideProgress();
                T.showShort("抱歉！未找到有效路线！");
            }
        } else {
            hideProgress();
            T.showShort("抱歉！请确认输入起始地无误！");
        }
    }

    @Override
    public void onRoutingCancelled() {
        hideProgress();
        L.i(TAG, "onRoutingCancelled()");
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgress();
            switch (msg.what) {
                case CodeType.SUCCESS:
                    if (msg.arg1 == CodeType.ADDWORKOUT) {
                        AddWorkResponse response = (AddWorkResponse) msg.obj;
                        workout_id = response.getWorkout_id();
                        insertOrReplaceWorkOut(workOut);
                        setResult(Activity.RESULT_OK);
                        finish();
                        L.i(TAG,"ADD SUCCESS work_id is "+workout_id);
                    } else if (msg.arg1 == CodeType.UPDATEWORKOUT) {
                        insertOrReplaceWorkOut(workOut);
                        setResult(Activity.RESULT_OK);
                        finish();
                        L.i(TAG,"UPDATEWORKOUT SUCCESS");
                    } else if (msg.arg1 == CodeType.DELETEWORKOUT) {
                        if (workOut != null && !TextUtils.isEmpty(workout_id)) {
                            workOutDao.deleteByKey(workOut.getWorkout_id());
                        }
                        setResult(Activity.RESULT_OK);
                        finish();
                        L.i(TAG,"DELETEWORKOUT SUCCESS");
                    }
                    break;
                case CodeType.TOKENERROR:
                    showCustomDialog("Erro Signing In", "Your account has expired or other places to log in, please re verify user identity.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent().setClass(HostActivity.this, LoginActivity.class));
                            finish();
                        }
                    }, false);
                    break;
                case CodeType.ERRORRQUEST:
                    if (msg.arg1 == CodeType.DELETEWORKOUT) {
                        if (workOut != null && !TextUtils.isEmpty(workout_id)) {
                            workOutDao.deleteByKey(workOut.getWorkout_id());
                        }
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                    break;
                default:
                    T.showShort(""+msg.what);
                    break;
            }
        }
    };

    public void initWorkOut(WorkOut workOut) {
        if (!addHost) {
            participantsList = workOut.getParticipants();
        }
        initParticipant(participantsList);

        mSpinner.setSelection(permission);

        name = workOut.getName();

        start_date_detail = DateUtil.convertServerDate(workOut.getStart_date());
        end_date_detail = DateUtil.convertServerDate(workOut.getEnd_date());

        startPlace = workOut.getStart_location();
        endPlace = workOut.getEnd_location();

        routes = workOut.getRoute();

        note = workOut.getNote();

        setViewText(nameTv, name);
        if (!TextUtils.isEmpty(workOut.getStart_date())) {
            if (workOut.getStart_date().split("T").length > 1) {
                String start_date = workOut.getStart_date().split("T")[0];
                setViewText(start_dateTv, start_date);
                String start_time = workOut.getStart_date().split("T")[1].substring(0, 8);
                setViewText(start_timeTv, start_time);

                String end_date = workOut.getEnd_date().split("T")[0];
                setViewText(end_dateTv, end_date);
                String end_time = workOut.getEnd_date().split("T")[1].substring(0, 8);
                setViewText(end_timeTv, end_time);
            }
        }

        if(startPlace.split("\\|").length>1){
            setViewText(startPlaceTv, startPlace.split("\\|")[0]);
            if(startPlace.split("\\|")[1].split(",").length>1){
                startLaLng = new LatLng(Double.parseDouble(startPlace.split("\\|")[1].split(",")[0]),
                        Double.parseDouble(startPlace.split("\\|")[1].split(",")[1]));
            }
        }else{
            setViewText(startPlaceTv, startPlace);
        }

        if(endPlace.split("\\|").length>1){
            setViewText(endPlaceTv, endPlace.split("\\|")[0]);
            if(endPlace.split("\\|")[1].split(",").length>1){
                endLaLng = new LatLng(Double.parseDouble(endPlace.split("\\|")[1].split(",")[0]),
                        Double.parseDouble(endPlace.split("\\|")[1].split(",")[1]));
            }
        }else{
            setViewText(endPlaceTv, endPlace);
        }

        setViewText(noteEdit, note);
    }

    void requestNetWork(JsonElement  je_mRoute){
        List<WorkOutRequest.ParticipantsRequest> participantRequestList = new ArrayList<WorkOutRequest.ParticipantsRequest>();
        for (int i = 0; i <participantsList.size() ; i++) {
            WorkOutRequest.ParticipantsRequest participantRequest = new WorkOutRequest.ParticipantsRequest(participantsList.get(i).getUser_id(),participantsList.get(i).getUsername());
            participantRequestList.add(participantRequest);
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tag", tag);
        jsonObject.addProperty("permission", permission);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("start_date", start_date_detail);
        jsonObject.addProperty("end_date", end_date_detail);
        jsonObject.addProperty("start_location", startPlace );
        jsonObject.addProperty("end_location", endPlace);
        // jsonObject.addProperty("route",mRoute.getRouteString());
//        jsonObject.addProperty("participants", "");
        jsonObject.addProperty("note", note);
        jsonObject.add("route", je_mRoute);

        JsonParser parser1 = new JsonParser();
        JsonElement jsonElement = parser1.parse(new Gson().toJson(participantRequestList));

        jsonObject.add("participants",jsonElement);

        if (addHost) {
            getProgress().setMessage("host...");
//                showProgress("host...");

//            WorkOutRequest request = new WorkOutRequest(tag,permission,name,start_date_detail,end_date_detail,startPlace,endPlace,
//                    custom_cal,custom_distance,custom_speed,custom_duration,routes,participantRequestList,note);
//            String WorkOutRequestString = new Gson().toJson(request);
//            L.i(TAG,"WorkOutRequestString ："+WorkOutRequestString);

            L.i(TAG,"WorkOutRequestString ："+start_date_detail);
            L.i(TAG,"WorkOutRequestString ："+end_date_detail);

            HttpImpl.getInstance(HostActivity.this).addWorkout(handler, accessToken, jsonObject);
        } else {
            getProgress().setMessage("update...");
//                showProgress("update...");
            L.i(TAG,"WorkOutRequestString ："+start_date_detail);
            L.i(TAG,"WorkOutRequestString ："+end_date_detail);

//            WorkOutRequest request = new WorkOutRequest(tag,permission,name,start_date_detail,end_date_detail,startPlace,endPlace,
//                    custom_cal,custom_distance,custom_speed,custom_duration,routes,participantRequestList,note);
//            String WorkOutRequestString = new Gson().toJson(request);
//            L.i(TAG,"WorkOutRequestString ："+WorkOutRequestString);
            L.i(TAG,workout_id);

            HttpImpl.getInstance(HostActivity.this).updateWorkout(handler, accessToken, workout_id, jsonObject);
        }
    }

    public void insertOrReplaceWorkOut(WorkOut workOut) {
            workOut.setWorkout_id(workout_id);
            workOut.setTag(tag);
            workOut.setPermission(permission);
            workOut.setName(name);
            workOut.setStart_date(start_date_detail);
            workOut.setEnd_date(start_date_detail);
            workOut.setStart_location(startPlace);
            workOut.setEnd_location(endPlace);
            workOut.setNote(note);
            workOut.setHost_id(TmjApplication.getInstance().getUserid());

            if(addHost){
                Host host = new Host(TmjApplication.getInstance().getUserid(),
                        TmjApplication.getInstance().getUserName(),
                        TmjApplication.getInstance().getImage_url(),
                        TmjApplication.getInstance().getUserEmail());
                ApplicationHolder.getApplication().getDbHelper().getDaoSession().getHostDao().insertOrReplace(host);
            }

            for (Participants p:participantsDao.queryBuilder().where(ParticipantsDao.Properties.Workout_id.eq(workout_id)).list() ) {
                participantsDao.delete(p);
            }

            for (Participants participants:participantsList) {
                participants.setWorkout_id(workout_id);
                participantsDao.insertOrReplace(participants);
            }
            workOutDao.insertOrReplace(workOut);
    }

    public void initParticipant(List<Participants> participantsList) {
        fowLayout.removeAllViews();
        fowLayout.getParticipantsList().clear();
        for (int i = 0; i < participantsList.size(); i++) {
            FlowTextView tv = new FlowTextView(this, false);
            tv.setText(participantsList.get(i).getUsername());
            fowLayout.getList().add(participantsList.get(i).getUsername());
            fowLayout.addView(tv);
        }
    }

    @Override
    public void onEventMainThread(Object obj){
        super.onEventMainThread(obj);
        if(obj instanceof AddWorkResponse){
            T.showShort("onEventMainThread is Execute !");
            L.i(TAG,"onEventMainThread is Execute !");
        }
    }
}
