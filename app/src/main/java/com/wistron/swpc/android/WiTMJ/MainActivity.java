package com.wistron.swpc.android.WiTMJ;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wistron.swpc.android.WiTMJ.about.AboutFragment;
import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.bean.Invitation;
import com.wistron.swpc.android.WiTMJ.bean.Profile;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.BluetoothSPP;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.BluetoothState;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.DeviceList;
import com.wistron.swpc.android.WiTMJ.competition.CompetitionFragment;
import com.wistron.swpc.android.WiTMJ.dao.ProfileDao;
import com.wistron.swpc.android.WiTMJ.fragment.CompetitionResultFragment;
import com.wistron.swpc.android.WiTMJ.fragment.SettingFragment;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.http.HttpImpl;
import com.wistron.swpc.android.WiTMJ.http.request.CompetitionModeRequest;
import com.wistron.swpc.android.WiTMJ.http.request.CyclModeRequest;
import com.wistron.swpc.android.WiTMJ.http.request.LogoutRequest;
import com.wistron.swpc.android.WiTMJ.http.request.NavigationModeRequest;
import com.wistron.swpc.android.WiTMJ.http.request.TrainModeRequest;
import com.wistron.swpc.android.WiTMJ.http.response.BluetoothResponse;
import com.wistron.swpc.android.WiTMJ.listener.onFragmentListener;
import com.wistron.swpc.android.WiTMJ.navigation.NavigationFragment;
import com.wistron.swpc.android.WiTMJ.personalinfo.PersonalFragment;
import com.wistron.swpc.android.WiTMJ.physicalreport.PhysicalFragment;
import com.wistron.swpc.android.WiTMJ.pushmsg.PushActivity;
import com.wistron.swpc.android.WiTMJ.train.TrainEditFragment;
import com.wistron.swpc.android.WiTMJ.train.TrainFragment;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Route;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BaseActivity implements View.OnClickListener, onFragmentListener {
    private final static String  TAG = "MainActivity";
    private NavigationView mNavView;
    private ImageView mPushIv;
    private LinearLayout mHomeLayout;
    private LinearLayout mPhysicalLayout, settingLayout, logoutLayout, resultLayout;
    private Fragment mCurrent = null;
    private CompetitionFragment competitionFragment;
    private PhysicalFragment physicalFragment;
    private HomeFragment homeFragment;
    private PersonalFragment personalFragment;
    private AboutFragment aboutFragment;
    private SettingFragment settingFragment;
    private NavigationFragment navigationFragment;
    private TrainFragment trainFragment;
    private TrainEditFragment trainEditFragment;
    private ImageView userPhotoIv;
    private LinearLayout aboutLayout;
    private LinearLayout mResultLayout;
    private Toolbar toolbar;
    private TextView titleTv;
    private ImageView titleIcon;
    private ImageView toolbar_num;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private static BluetoothSPP bt;
    private TextView userNameTxt, userIdTxt, userEmailTv;
    private TextView mUnReadTv;
    private LinearLayout mUnReadLayout;
    private int mUnReadNum;
    private List<Invitation> invitationList = new ArrayList<Invitation>();


    private boolean isFirstConnected =false;

    public synchronized static BluetoothSPP getBluetoothSPP() {
        return bt;
    }

    private ProfileDao profileDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        initData();
        initGPS();

    }

    @Override
    public void initView() {
        mNavView = (NavigationView) findViewById(R.id.nav_view);
        mPushIv = (ImageView) mNavView.findViewById(R.id.pushIv);
        mHomeLayout = (LinearLayout) mNavView.findViewById(R.id.homeLayout);
        mPhysicalLayout = (LinearLayout) mNavView.findViewById(R.id.physicalLayout);
        mHomeLayout.setOnClickListener(this);
        mPhysicalLayout.setOnClickListener(this);
        mResultLayout = (LinearLayout) mNavView.findViewById(R.id.resultLayout);
        mPushIv.setOnClickListener(this);
        aboutLayout = (LinearLayout) mNavView.findViewById(R.id.aboutLayout);
        userPhotoIv = (ImageView) findViewById(R.id.userPhotoIv);
        mHomeLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);
        mPushIv.setOnClickListener(this);
        userPhotoIv.setOnClickListener(this);
        settingLayout = (LinearLayout) mNavView.findViewById(R.id.settingLayout);
        settingLayout.setOnClickListener(this);
        logoutLayout = (LinearLayout) mNavView.findViewById(R.id.logoutLayout);
        logoutLayout.setOnClickListener(this);
        resultLayout = (LinearLayout) mNavView.findViewById(R.id.resultLayout);
        resultLayout.setOnClickListener(this);
        mUnReadTv = (TextView) mNavView.findViewById(R.id.unReadTv);
        mUnReadLayout = (LinearLayout) mNavView.findViewById(R.id.unReadLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        titleTv = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleIcon = (ImageView) toolbar.findViewById(R.id.toolbar_icon);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        new Thread(new Runnable() {
            @Override
            public void run() {
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                homeFragment = new HomeFragment();
                ManagerFragment.getInstance().addfragment(homeFragment);
                transaction.add(R.id.flContent, homeFragment).commitAllowingStateLoss();
                mCurrent = homeFragment;
            }
        }).start();

        userNameTxt = (TextView) findViewById(R.id.userName);
        userIdTxt = (TextView) findViewById(R.id.userId);
        userEmailTv = (TextView) findViewById(R.id.userEmail);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.d(TAG,"==onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG,"==onResume");
        String token = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, "");
        HttpImpl.getInstance(MainActivity.this).getInvitation(handler, token);
    }


    @Override
    public void initEvent() {
        bt = new BluetoothSPP(MainActivity.this);

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
//                T.showShort("" + message);
                L.i("新消息",message);
                BluetoothResponse response =  new Gson().fromJson(message, new TypeToken<BluetoothResponse>(){}.getType());
                if(response.getStatus().equals("success")){
                    hideProgress();
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                dismissCustomDialog();
                T.showShort("Status : Not connect");
                TmjApplication.getInstance().setConneced(false);
                homeFragment.updataStatus(false);

                if(isFirstConnected){
                    showCustomDialog("Erro", "TMJ device not connected, please connect !", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, DeviceList.class);
                            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                            dialog.dismiss();
                        }
                    }, false);
                }
            }


            public void onDeviceConnectionFailed() {
                dismissCustomDialog();
                T.showShort("Status : Connection failed");
                TmjApplication.getInstance().setConneced(false);
                homeFragment.updataStatus(false);

                if(isFirstConnected){
                    showCustomDialog("Erro", "TMJ device not connected, please connect !", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, DeviceList.class);
                            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                            dialog.dismiss();
                        }
                    }, false);
                }
            }

            public void onDeviceConnected(String name, String address) {
                dismissCustomDialog();
                T.showShort("Status : Connected to " + name);
                TmjApplication.getInstance().setConneced(true);
                homeFragment.updataStatus(true);

                isFirstConnected = true;

               /* PreferencesUtil mPreferencesUtil = new PreferencesUtil(MainActivity.this,
                        PreferenceConstants.HOME_CYCLING_TABLE);
                String mDurMinsNum = mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_DURATION, "00");
                String mDisNum = mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_DISTANCE, "0");
                String mSpdNum = mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_SPEED, "0");
                String mCalNum = mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_CALORIES, "0");

                showProgress("wait response...");
                CyclModeRequest request = new CyclModeRequest("cycl","init",mCalNum,mDisNum,mSpdNum,mDurMinsNum);
                bt.send(new Gson().toJson(request),false);
                L.i("bt send " + new Gson().toJson(request));*/


            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
        Intent intent = new Intent();
        intent.setAction("com.wistronits.tmj.gps_service");
        intent.setPackage(getPackageName());
        stopService(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d(TAG,"==onStart");
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                setup();
            }
        }
        Intent intent = new Intent();
        intent.setAction("com.wistronits.tmj.gps_service");
        intent.setPackage(getPackageName());
        startService(intent);

    }

    public void setup() {
        bt.send("connect is success!", true);
    }


    @Override
    public void initData() {
        profileDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getProfileDao();
        List<Profile> list = profileDao.queryBuilder().list();
        if(PreferencesUtil.getPrefBoolean(MainActivity.this,PreferenceConstants.USER_FIRST_INIT,false)){
            String token = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, "");
            HttpImpl.getInstance(MainActivity.this).getProfile(handler, token);
        }else{
            if(list.size()>0){
                Profile profile = list.get(0);
                initUserData(profile);
                String img_url = profile.getImage_url();

                DisplayImageOptions options = new DisplayImageOptions.Builder()//加载头像
                        .showImageForEmptyUri(R.drawable.contact_default) // resource or drawable
                        .showImageOnFail(R.drawable.contact_default).build();

                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(img_url,userPhotoIv,options);
                initUserData(profile);
            }else{
                String token = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, "");
                showProgress("initialize...");
                HttpImpl.getInstance(MainActivity.this).getProfile(handler, token);
            }

        }
        String token = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, "");
        LogUtils.d(TAG,"token == "+token);
        HttpImpl.getInstance(MainActivity.this).getInvitation(handler, token);
        LogUtils.d(TAG,"invitationList size == "+invitationList.size());
        if(invitationList.size()>0){
            mUnReadNum = invitationList.size();
            mUnReadLayout.setVisibility(View.VISIBLE);
            mUnReadTv.setText(mUnReadNum);
        }else {
            mUnReadLayout.setVisibility(View.GONE);
        }
    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS是否打开，如果没有则提示用户是否打开GPS
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("是否打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机GPS设置界面，用户设置GPS的模式
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0);
                            // 设置完成后返回到原来的界面
                        }
                    });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        }
    }

    void initUserData(Profile profile) {
        TmjApplication.getInstance().setUserid(profile.getProfile_id());
        TmjApplication.getInstance().setUserName(profile.getUsername());
        TmjApplication.getInstance().setUserEmail(profile.getEmail());
        TmjApplication.getInstance().setImage_url(profile.getImage_url());
        setViewText(userNameTxt, profile.getUsername());
        setViewText(userIdTxt, profile.getProfile_id());
        setViewText(userEmailTv, profile.getEmail());
    }

    @Override
    public void onBackPressed() {
        LogUtils.d(TAG,"==onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pushIv:
                Intent i = new Intent();
                i.putExtra("invitationList",(Serializable) invitationList);
                LogUtils.d(TAG,"invitationList = "+invitationList);
                i.setClass(MainActivity.this, PushActivity.class);
                startActivityForResult(i, Constants.PUSH_REQUEST_CODE);
                break;
            case R.id.userPhotoIv:
                if (personalFragment == null) {
                    personalFragment = new PersonalFragment();
                }
                switchContent(mCurrent, personalFragment);
                titleTv.setText("Personal Info");
                titleIcon.setVisibility(View.GONE);
                break;
            case R.id.homeLayout:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                switchContent(mCurrent, homeFragment);
                titleTv.setText("Home");
                titleIcon.setBackgroundResource(R.drawable.home);
                titleIcon.setVisibility(View.VISIBLE);
                break;
            case R.id.physicalLayout:
                if (physicalFragment == null) {
                    physicalFragment = new PhysicalFragment();
                }
                switchContent(mCurrent, physicalFragment);
                titleTv.setText("Physical Report");
                titleIcon.setBackgroundResource(R.drawable.report_p);
                titleIcon.setVisibility(View.VISIBLE);
                break;
            case R.id.resultLayout:
                CompetitionResultFragment competitionResultFragment = null;
                if (competitionResultFragment == null) {
                    competitionResultFragment = new CompetitionResultFragment();
                }
                switchContent(mCurrent, competitionResultFragment);
                titleTv.setText("Competition Result");
                titleIcon.setBackgroundResource(R.drawable.result);
                titleIcon.setVisibility(View.VISIBLE);
                break;
            case R.id.settingLayout:
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                }
                switchContent(mCurrent, settingFragment);
                titleTv.setText("TMJ Device Setting");
                titleIcon.setBackgroundResource(R.drawable.devicesetting);
                titleIcon.setVisibility(View.VISIBLE);
                break;
            case R.id.aboutLayout:
                if (aboutFragment == null) {
                    aboutFragment = new AboutFragment();
                }
                switchContent(mCurrent, aboutFragment);
                titleTv.setText("About");
                titleIcon.setBackgroundResource(R.drawable.about);
                titleIcon.setVisibility(View.VISIBLE);
                break;

            case R.id.logoutLayout:
                showCustomDialog("Logout", "Are you sure to logout?", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String accessToken = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, "");
                        String refreshToken = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.REFRESH_TOKEN, "");
                        LogoutRequest request = new LogoutRequest(refreshToken);
                        showProgress("Logout...");
                        HttpImpl.getInstance(getApplicationContext()).loginOut(handler, accessToken, request);

                    }
                }, false);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void switchContent(Fragment from, Fragment to) {
        if (mCurrent != to) {
            ManagerFragment.getInstance().addfragment(to);
            mCurrent = to;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();

            if (!to.isAdded()) {
                transaction.hide(from).add(R.id.flContent, to).commitAllowingStateLoss();
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onStartFragment(Class newClass) {
                        /*CompetitionDetailActivity back page*/
        LogUtils.i(TAG,"zlb== onResume ");

        Fragment fragment = null;
        if (newClass == CompetitionFragment.class) {
            if (competitionFragment == null) {
                competitionFragment = new CompetitionFragment();
            }
            fragment = competitionFragment;
            titleTv.setText("Competition");
            titleIcon.setBackgroundResource(R.drawable.competition_white);
            titleIcon.setVisibility(View.VISIBLE);
        }
        if (newClass == NavigationFragment.class) {
            if (navigationFragment == null) {
                navigationFragment = new NavigationFragment();
            }
            fragment = navigationFragment;
            titleTv.setText("Navigation");
            titleIcon.setBackgroundResource(R.drawable.navigation3);
            titleIcon.setVisibility(View.VISIBLE);
        }
        if (newClass == HomeFragment.class) {
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
            }
            fragment = homeFragment;
            titleTv.setText("Home");
            titleIcon.setBackgroundResource(R.drawable.home);
            titleIcon.setVisibility(View.VISIBLE);
        }
        if (newClass == TrainFragment.class) {
            if (trainFragment == null) {
                trainFragment = new TrainFragment();
            }
            fragment = trainFragment;
            titleTv.setText("Virtual trainer");
            titleIcon.setBackgroundResource(R.drawable.trainer);
            titleIcon.setVisibility(View.VISIBLE);
        }
        if (newClass == TrainEditFragment.class) {
            if (trainEditFragment == null) {
                trainEditFragment = new TrainEditFragment();
            }
            fragment = trainEditFragment;
            titleTv.setText("Virtual trainer");
            titleIcon.setBackgroundResource(R.drawable.trainer);
            titleIcon.setVisibility(View.VISIBLE);
        }
        switchContent(mCurrent, fragment);
    }

    @Override
    public void onStartFragmentWithData(Class newClass, Object data) {
        Fragment fragment = null;
        if (newClass == TrainEditFragment.class) {
            if (trainEditFragment == null) {
                trainEditFragment = new TrainEditFragment();
            }
            fragment = trainEditFragment;
            trainEditFragment.setmRoute((Route) data);//传值到TrainEditFragment中
            titleTv.setText("Virtual trainer");
            titleIcon.setBackgroundResource(R.drawable.trainer);
            titleIcon.setVisibility(View.VISIBLE);
        }

        if (newClass == NavigationFragment.class) {
            if (navigationFragment == null) {
                navigationFragment = new NavigationFragment();
            }
            navigationFragment.setShowTrainProgress((Route) data,MainActivity.this);
            fragment = navigationFragment;
            titleTv.setText("Navigation");
            titleIcon.setBackgroundResource(R.drawable.navigation3);
            titleIcon.setVisibility(View.VISIBLE);
        }
        switchContent(mCurrent, fragment);
    }


    //TODO:如果没有登录，返回则退出应用，不然应用无法退出
    private boolean isBack = false;
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCurrent instanceof HomeFragment) {
                if (!TextUtils.isEmpty(PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, ""))) {
                    //点两次最小化
                    if (isBack) {
                        moveTaskToBack(true);
                    } else {
                        T.show(this, "再次点击切换到后台", 3);
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                isBack = false;
                            }
                        }, 3000);
                        isBack = true;
                    }
                } else {
                    //点两次退出
                    if ((System.currentTimeMillis() - exitTime) > 3000) {
                        T.show(this, "再次点击退出程序", 0);
                        exitTime = System.currentTimeMillis();
                    } else {
                        T.hideToast();
                        finish();
                    }
                }
            } else {
                switchContent(mCurrent, homeFragment);
                titleTv.setText("Home");
                titleIcon.setBackgroundResource(R.drawable.home);
                titleIcon.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BluetoothState.REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK)
                    if (data != null) {
                        showCustomDialog("Connecting", "", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, true);
                        bt.connect(data);
                    }
                break;
            case BluetoothState.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    bt.setupService();
                    bt.startService(BluetoothState.DEVICE_ANDROID);
                } else {
                    T.showShort("Bluetooth was not enabled.");
                }
                break;
            case Constants.PUSH_REQUEST_CODE:
                if (resultCode == Constants.PUSH_RESULT_CODE) {
                    mUnReadNum = data.getExtras().getInt("unReadNum");
                    if (mUnReadNum > 0) {
                        if (mUnReadLayout.getVisibility() == View.GONE) {
                            mUnReadLayout.setVisibility(View.VISIBLE);
                        }
                        String unReadNumStr = Integer.toString(mUnReadNum);
                        mUnReadTv.setText(unReadNumStr);
                    } else {
                        mUnReadLayout.setVisibility(View.GONE);
                    }
                    switchContent(mCurrent,homeFragment);
                }
                break;
            case Constants.CMPTIONDETAIL_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    LogUtils.i(TAG,"zlb== CMPTIONDETAIL_REQUEST_CODE ");
                    switchContent(mCurrent,homeFragment);
                }
                break;
            case Constants.INITVIEW:
                switchContent(mCurrent,homeFragment);
                ManagerFragment.getInstance().finishAllfragment(getSupportFragmentManager());
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startTrainMode(String type,String status,String duration,
                               String calories,String distance,String speed){
        if(checkConnect()){
            TrainModeRequest request = new TrainModeRequest(type,status,duration,calories,"0",distance,"0",speed,"0","0","0","0");
            showProgress("connect...");
            L.i(TAG,new Gson().toJson(request));
            bt.send(new Gson().toJson(request),false);
        }
    }

    public void startNavigationMode(String type,String status,String routes){
        if(checkConnect()){
            NavigationModeRequest request = new NavigationModeRequest(type,status,routes,"","");
            showProgress("connect...");
            L.i(TAG,new Gson().toJson(request));
            bt.send(new Gson().toJson(request),false);
        }
    }

    public void startCompetitionMode(String type,String status){
        if(checkConnect()){
            CompetitionModeRequest request = new CompetitionModeRequest(type,status,"0","1","0","0","0","0");
            showProgress("connect...");
            L.i(TAG,new Gson().toJson(request));
            bt.send(new Gson().toJson(request),false);
        }
    }

    public void startCyclMode(String type,String status){
        if(checkConnect()){
            showProgress("connect...");
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss");
            String time = sdf.format(date);
            CyclModeRequest request = new CyclModeRequest("cycl","init","231","321","123",time);
            L.i(TAG,new Gson().toJson(request));
            bt.send(new Gson().toJson(request),false);
        }
    }

    public boolean checkConnect(){
        if(!TmjApplication.getInstance().isConneced()){
            showCustomDialog("Erro", "TMJ device not connected, please connect !", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                    dialog.dismiss();
                }
            }, false);
            return false;
        }
        return true;
    }

    public void startFragmentAndFinish(Fragment f,Class newClass){
        Fragment fragment = null;
        if (newClass == TrainEditFragment.class) {
            if (trainEditFragment == null) {
                trainEditFragment = new TrainEditFragment();
            }
            fragment = trainEditFragment;
            titleTv.setText("Virtual trainer");
            titleIcon.setBackgroundResource(R.drawable.trainer);
            titleIcon.setVisibility(View.VISIBLE);
        }
        switchContent(mCurrent,homeFragment);
        ManagerFragment.getInstance().finishFragment(getSupportFragmentManager(),f);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgress();
            switch (msg.what) {
                case CodeType.SUCCESS:
                    if (msg.arg1 == CodeType.LOGOUT) {
                        PreferencesUtil.setPrefBoolean(getApplicationContext(), PreferenceConstants.AUTOLOGIN, false);// 是否开启默认登录
                        PreferencesUtil.setPrefString(getApplicationContext(),PreferenceConstants.PASSWORD,"");
                        startActivity(new Intent().setClass(MainActivity.this, LoginActivity.class));
                        profileDao.deleteAll();
                        ApplicationHolder.getApplication().getDbHelper().getDaoSession().getWorkOutDao().deleteAll();
                        ApplicationHolder.getApplication().getDbHelper().getDaoSession().getParticipantsDao().deleteAll();
                        ApplicationHolder.getApplication().getDbHelper().getDaoSession().getHostDao().deleteAll();
                        finish();
                    }else if(msg.arg1 == CodeType.PROFILE){
                        Profile profile = (Profile) msg.obj;
                        profileDao.deleteAll();
                        profileDao.insertOrReplace(profile);

                        initUserData(profile);

                        //重新拉取用户数据时代表用户已更换则删除所有数据
                        ApplicationHolder.getApplication().getDbHelper().getDaoSession().getWorkOutDao().deleteAll();
                        ApplicationHolder.getApplication().getDbHelper().getDaoSession().getParticipantsDao().deleteAll();
                        ApplicationHolder.getApplication().getDbHelper().getDaoSession().getHostDao().deleteAll();

                    }else  if(msg.arg1 == CodeType.GETWORKOUT){
                        PreferencesUtil.setPrefBoolean(getApplicationContext(),PreferenceConstants.USER_FIRST_INIT,false);
                        hideProgress();
                    }else if (msg.arg1 == CodeType.getINVITATION){
                            invitationList = (List<Invitation>) msg.obj;
                        }

                    break;
                case CodeType.TOKENERROR:
                    hideProgress();
                    showCustomDialog("Erro Signing In", "Your account has expired or other places to log in, please re verify user identity.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent().setClass(MainActivity.this, LoginActivity.class));
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
}
