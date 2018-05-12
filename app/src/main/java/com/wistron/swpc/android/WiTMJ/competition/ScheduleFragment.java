package com.wistron.swpc.android.WiTMJ.competition;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wistron.swpc.android.WiTMJ.ApplicationHolder;
import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.LoginActivity;
import com.wistron.swpc.android.WiTMJ.ManagerFragment;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.adapter.SchedultAdapter.OperaterListener;
import com.wistron.swpc.android.WiTMJ.bean.Participants;
import com.wistron.swpc.android.WiTMJ.bean.WorkOut;
import com.wistron.swpc.android.WiTMJ.dao.WorkOutDao;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.adapter.SchedultAdapter;
import com.wistron.swpc.android.WiTMJ.base.BaseFragment;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.http.HttpImpl;
import com.wistron.swpc.android.WiTMJ.http.request.InvitationRequest;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.NetUtil;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WH1604025 on 2016/4/18.
 */
public class ScheduleFragment extends BaseFragment implements View.OnClickListener ,OperaterListener{
    private final static String TAG = "ScheduleFragment";
    private View mMainView;

    private Button hostBtn;

    private RelativeLayout contentLayout;

    private ListView listView;
    private SchedultAdapter adapter;

    private List<WorkOut> workOutList = new ArrayList<WorkOut>();

    private WorkOutDao myWorkOutDao;

    String accessToken;
    int type = 3;
    int size = 10;
    int tag = Constants.TAG_SCHEDULE;
    boolean isOver = true;

    InstantFragment instantFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mMainView = inflater.inflate(R.layout.fragment_schedule, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup p = (ViewGroup) mMainView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mMainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            return;
        initView();
        initEvent();
        initData();
    }

    @Override
    public void initView() {
        hostBtn = (Button) mMainView.findViewById(R.id.hostBtn);
        contentLayout = (RelativeLayout) mMainView.findViewById(R.id.contentLayout);
        hostBtn.setOnClickListener(this);
        contentLayout.setOnClickListener(this);


        listView = (ListView) mMainView.findViewById(R.id.listView);
        adapter = new SchedultAdapter(this);

//        for (int i = 0; i <3 ; i++) {
//            WorkOut workout = new WorkOut("dasdasd",2,2,"HH","2016-06-16 16:21:01 +0800","2016-06-16 18:21:01 +0800",
//                    "武汉|311321.3212,132123,1321","南昌|311321.3212,132123,1321","","","","","","","");
//            workout.resetParticipants();
//            workOutList.add(workout);
//        }
        adapter.updata(workOutList);
        adapter.setOperaterListener(this);
        listView.setAdapter(adapter);
    }

    @Override
    public void initEvent() {
/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent().setClass(getActivity(), HostActivity.class);
//                intent.putExtra("type", false);
//                intent.putExtra("workout_id",workOutList.get(position).getWorkout_id());
//                intent.putExtra("index",position);
//                startActivityForResult(intent,Activity.RESULT_FIRST_USER);

                instantFragment= new InstantFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                mMainView.findViewById(R.id.schedule_list).setVisibility(View.GONE);
                transaction.add(R.id.schedult_content, instantFragment).commitAllowingStateLoss();
                instantFragment.setWorkOut(workOutList.get(position));
                ManagerFragment.getInstance().addfragment(instantFragment);
            }
        });*/
    }

    @Override
    public void initData() {
        accessToken = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.ACCESS_TOKEN, "");
        myWorkOutDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getWorkOutDao();

        if(NetUtil.getNetworkState(getActivity())==NetUtil.NETWORN_NONE){
            workOutList = myWorkOutDao.queryBuilder().where(WorkOutDao.Properties.Tag.eq(4)).list();
            adapter.updata(workOutList);
        }else{
            //获取workout数据
            showProgress("wait...");
            HttpImpl.getInstance(getActivity()).getWorkOut(handler, accessToken, type,tag,isOver);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgress();
            switch (msg.what){
                case CodeType.SUCCESS:
                    if(msg.arg1 == CodeType.GETWORKOUT){
                        workOutList = (List<WorkOut>) msg.obj;
                        adapter.updata(workOutList);
                    }
                    if(msg.arg1 == CodeType.UPDATEINVITATION){
                        T.showShort("success");
                    }
                    break;
                case CodeType.TOKENERROR:
                    showCustomDialog("Erro Signing In", "Your account has expired or other places to log in, please re verify user identity.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().startActivity(new Intent().setClass(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        }
                    }, false);
                    break;
                default:
                    T.showShort("" + msg.what);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.hostBtn:
                intent = new Intent().setClass(getActivity(), HostActivity.class);
                startActivityForResult(intent,Activity.RESULT_FIRST_USER);
                break;
            case R.id.contentLayout:
                intent = new Intent().setClass(getActivity(), HostDetailActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode == Activity.RESULT_FIRST_USER){
                ApplicationHolder.getApplication().getDbHelper().getDaoSession().clear();
                workOutList = myWorkOutDao.queryBuilder().where(WorkOutDao.Properties.Tag.eq(4)).list();
//                        showProgress("update...");
//                        HttpImpl.getInstance(getActivity()).getWorkOut(handler, accessToken, type,tag,isOver);
//                        int index = data.getIntExtra("index",0);
                adapter.updata(workOutList);
            }
        }
        L.i(TAG, "onActivityResult");
    }

    @Override
    public void showClickListener(int position) {
        instantFragment= new InstantFragment();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mMainView.findViewById(R.id.schedule_list).setVisibility(View.GONE);
        transaction.add(R.id.schedult_content, instantFragment).commitAllowingStateLoss();
        instantFragment.setWorkOut(workOutList.get(position));
        ManagerFragment.getInstance().addfragment(instantFragment);
    }

    @Override
    public void editClickListener(int position) {
        Intent intent = new Intent().setClass(getActivity(), HostActivity.class);
        intent.putExtra("workout_id",workOutList.get(position).getWorkout_id());
        startActivityForResult(intent, Activity.RESULT_FIRST_USER);
    }

    @Override
    public void okClickListener(int position) {
        showProgress("wait...");
        String recordsId = "";
        for ( Participants p:workOutList.get(position).getParticipants()) {
            if(p.getUser_id().equals(TmjApplication.getInstance().getUserid())){
                recordsId = p.getRecord_id();
                break;
            }
        }

        HttpImpl.getInstance(getActivity()).updateInvitation(handler,accessToken,recordsId, new InvitationRequest(Constants.INVITATION_OK));
    }

    @Override
    public void cancelClickListener(int position) {
        showProgress("wait...");
        String recordsId = "";
        for ( Participants p:workOutList.get(position).getParticipants()) {
            if(p.getUser_id().equals(TmjApplication.getInstance().getUserid())){
                recordsId = p.getRecord_id();
                break;
            }
        }
        HttpImpl.getInstance(getActivity()).updateInvitation(handler,accessToken,recordsId, new InvitationRequest(Constants.INVITATION_DENY));

    }
}
