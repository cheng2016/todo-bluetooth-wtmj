package com.wistron.swpc.android.WiTMJ;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.base.BaseFragment;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.BluetoothSPP;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.BluetoothState;
import com.wistron.swpc.android.WiTMJ.communication.bluetooth.DeviceList;
import com.wistron.swpc.android.WiTMJ.competition.CompetitionFragment;
import com.wistron.swpc.android.WiTMJ.dao.RecordsDao;
import com.wistron.swpc.android.WiTMJ.listener.onFragmentListener;
import com.wistron.swpc.android.WiTMJ.navigation.NavigationFragment;
import com.wistron.swpc.android.WiTMJ.train.TrainFragment;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;


public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = "HomeFragment";
    private RecordsDao mRecordsDao;
    private LinearLayout competenceLayout;
    private View mMainView;
    private LinearLayout mTmjLayout;
    private LinearLayout mTmjDeviceIconLayout;
    private TextView mTtmjDeviceTv;
    private LinearLayout navigationLayout;
    private LinearLayout trainingLayout;
    private Button resetBtn;
    private TextView mDurHrsTv;
    private TextView mDurMinsTv;
    private TextView mCalTv;
    private TextView mDisTv;
    private TextView mSpdTv;

    private int mDurHrsNum;
    private int mDurMinsNum;
    private Double mCalNum;
    private Double mDisNum;
    private Double mSpdNum;

    private onFragmentListener listener;
    private ProgressBar mTrainProgrsBar;
    private PreferencesUtil mPreferencesUtil;

    private BluetoothSPP bt;

    public void setOnFragmentListener(onFragmentListener listener) {
        this.listener = listener;
    }

    private ImageView statusImg;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (onFragmentListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mMainView = inflater.inflate(R.layout.fragment_home, null);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup p = (ViewGroup) mMainView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        initView();
        initEvent();
        initData();
        return mMainView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            return;
        initRecordsData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(TmjApplication.getInstance().isConneced()){
            statusImg.setBackgroundResource(R.drawable.link_break);
            mTmjDeviceIconLayout.setEnabled(true);

            mTtmjDeviceTv.setEnabled(true);
        }else{
            statusImg.setBackgroundResource(R.drawable.icon_bluetooth);
            mTmjDeviceIconLayout.setEnabled(false);

            mTtmjDeviceTv.setEnabled(false);
        }
        initRecordsData();
    }

    public void updataStatus(boolean isConnect){
        if(isConnect){
            statusImg.setBackgroundResource(R.drawable.link_break);
            mTmjDeviceIconLayout.setEnabled(true);

            mTtmjDeviceTv.setEnabled(true);
        }else{
            statusImg.setBackgroundResource(R.drawable.icon_bluetooth);
            mTmjDeviceIconLayout.setEnabled(false);

            mTtmjDeviceTv.setEnabled(false);
        }
    }

    @Override
    public void initView() {
        mRecordsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getRecordsDao();
        competenceLayout = (LinearLayout) mMainView.findViewById(R.id.competenceLayout);
        navigationLayout = (LinearLayout) mMainView.findViewById(R.id.navigationLayout);
        mTmjLayout = (LinearLayout) mMainView.findViewById(R.id.tmjLayout);
        mTmjLayout.setOnClickListener(this);
        mTmjLayout = (LinearLayout) mMainView.findViewById(R.id.tmjLayout);
        mTmjDeviceIconLayout = (LinearLayout) mMainView.findViewById(R.id.tmjDeviceIconLayout);
        mTmjDeviceIconLayout.setOnClickListener(this);

        mTtmjDeviceTv = (TextView) mMainView.findViewById(R.id.tmjDevice);
        mTtmjDeviceTv.setOnClickListener(this);

        mDurHrsTv = (TextView) mMainView.findViewById(R.id.durationHrsTv);
        mDurMinsTv = (TextView) mMainView.findViewById(R.id.durationMinsTv);
        mCalTv = (TextView) mMainView.findViewById(R.id.caloriesAllTv);
        mDisTv = (TextView) mMainView.findViewById(R.id.distanceAllTv);
        mSpdTv = (TextView) mMainView.findViewById(R.id.speedAllTv);

        mTmjLayout.setOnClickListener(this);
        navigationLayout = (LinearLayout) mMainView.findViewById(R.id.navigationLayout);
        trainingLayout = (LinearLayout) mMainView.findViewById(R.id.trainingLayout);

        resetBtn = (Button) mMainView.findViewById(R.id.reset);
        resetBtn.setOnClickListener(this);
        mTrainProgrsBar = (ProgressBar) mMainView.findViewById(R.id.trainProgrsBar);

        statusImg = (ImageView) mMainView.findViewById(R.id.statusImg);

        mPreferencesUtil = new PreferencesUtil(getActivity(),
                PreferenceConstants.HOME_CYCLING_TABLE);

        mMainView.findViewById(R.id.noneLayout).setOnClickListener(this);
    }

    @Override
    public void initEvent() {
        competenceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStartFragment(CompetitionFragment.class);
            }
        });

        navigationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStartFragment(NavigationFragment.class);
            }
        });
        trainingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStartFragment(TrainFragment.class);
            }
        });
    }

    @Override
    public void initData() {
        bt = ((MainActivity)getActivity()).getBluetoothSPP();
        initRecordsData();
    }

    private void initRecordsData() {
        double mDurNum = Double.valueOf(mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_DURATION,"00"));
        double mDisNum = Double.valueOf(mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_DISTANCE,"0"));
        double mSpdNum = Double.valueOf(mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_SPEED,"0"));
        double mCalNum = Double.valueOf(mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_CALORIES,"0"));

        LogUtils.d(TAG,"==mDurNum="+mDurNum);
        LogUtils.d(TAG,"==mDisNum="+mDisNum);
        LogUtils.d(TAG,"==mSpdNum="+mSpdNum);
        LogUtils.d(TAG,"==mCalNum="+mCalNum);

        String dur[] = CommonUtil.getDuration(mDurNum);
        LogUtils.d(TAG,"==dur[0]="+dur[0]);
        LogUtils.d(TAG,"==dur[1]="+dur[1]);

        double disDL =  CommonUtil.getDistance(mDisNum);
        LogUtils.d(TAG,"==disDL="+disDL);
        String dis = String.valueOf(disDL);

        double calDL =  CommonUtil.getCalories(mCalNum);
        LogUtils.d(TAG,"==calDL="+calDL);
        String cal = String.valueOf(calDL);

        double spdDL =  CommonUtil.getSpeed(mSpdNum);
        LogUtils.d(TAG,"==spdDL="+spdDL);
        String spd = String.valueOf(spdDL);

/*         double spdNum = 0.0d;
        String spd;
       if (mDurNum!=0 && mDisNum!=0){
            spdNum = mDisNum/mDurNum ;
        }else {
            spdNum = 0.0d;
        }*/

 /*       double spdDL =  CommonUtil.getSpeed(spdNum);
        spd = String.valueOf(spdDL);*/

        mDurHrsTv.setText(dur[0]);
        mDurMinsTv.setText(dur[1]);
        mDisTv.setText(dis);
        mCalTv.setText(cal);
        mSpdTv.setText(spd);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tmjLayout:
                Intent intent = new Intent(getActivity(), DeviceList.class);
                getActivity().startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                break;
            case R.id.tmjDevice:
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                    bt.send("Device1", false);
                }
                break;
            case R.id.reset:
                showCustomDialog("Sure to reset","",new android.content.DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPreferencesUtil.saveString(PreferenceConstants.HOME_CYCLING_DURATION,"00");
                        mPreferencesUtil.saveString(PreferenceConstants.HOME_CYCLING_DISTANCE,"0");
                        mPreferencesUtil.saveString(PreferenceConstants.HOME_CYCLING_SPEED, "0");
                        mPreferencesUtil.saveString(PreferenceConstants.HOME_CYCLING_CALORIES, "0");
                        mDurHrsTv.setText("0");
                        mDurMinsTv.setText("00");
                        mCalTv.setText("0");
                        mDisTv.setText("0");
                        mSpdTv.setText("0");
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.tmjDeviceIconLayout:
//                showProgress("Fetching route information");
                break;
            case R.id.noneLayout:
                ManagerFragment.getInstance().finishAllfragment(((MainActivity)getActivity()).getSupportFragmentManager());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
