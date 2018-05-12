package com.wistron.swpc.android.WiTMJ.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wistron.swpc.android.WiTMJ.ApplicationHolder;
import com.wistron.swpc.android.WiTMJ.Constants;
import com.wistron.swpc.android.WiTMJ.LoginActivity;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.activity.CompetitionResultDetailActivity;
import com.wistron.swpc.android.WiTMJ.adapter.CompetitionResultAdapter;
import com.wistron.swpc.android.WiTMJ.base.BaseFragment;
import com.wistron.swpc.android.WiTMJ.bean.WorkOut;
import com.wistron.swpc.android.WiTMJ.dao.WorkOutDao;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.http.HttpImpl;
import com.wistron.swpc.android.WiTMJ.http.response.CompetitionResult;
import com.wistron.swpc.android.WiTMJ.util.DateUtil;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;

import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by WH1604025 on 2016/5/11.
 */
public class CompetitionResultFragment extends BaseFragment {

    private final static String TAG = "CompetitionResultFragment";
    private View mMainView;


    private ListView schedultListView,instantListView;

    private CompetitionResultAdapter schedultAdapter,instantAdapter;

    private List<WorkOut> instantList,scheduleList;

    private WorkOutDao myWorkOutDao;

    int tag = Constants.TAG_SCHEDULE;

    String accessToken;
    int type = 3;
    int size = 10;
    boolean isOver = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mMainView = inflater.inflate(R.layout.fragment_competition_result, null);
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
        schedultListView = (ListView) mMainView.findViewById(R.id.schedultListView);
        instantListView = (ListView) mMainView.findViewById(R.id.instantListView);
    }

    @Override
    public void initEvent() {
        schedultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),CompetitionResultDetailActivity.class);
                intent.putExtra("work_id",scheduleList.get(position).getWorkout_id());
                startActivity(intent);
            }
        });

        instantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),CompetitionResultDetailActivity.class);
                intent.putExtra("work_id",instantList.get(position).getWorkout_id());
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {


        schedultAdapter = new CompetitionResultAdapter(getActivity(),true);
        schedultListView.setAdapter(schedultAdapter);

        instantAdapter = new CompetitionResultAdapter(getActivity(),false);
        instantListView.setAdapter(instantAdapter);

        myWorkOutDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getWorkOutDao();

        accessToken = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.ACCESS_TOKEN, "");


        showProgress("wait...");
        HttpImpl.getInstance(getActivity()).getWorkOut(handler,accessToken,type,tag,isOver);

//        scheduleList = myWorkOutDao.queryBuilder().where(WorkOutDao.Properties.Tag.eq(3)).list();;
//        instantList = myWorkOutDao.queryBuilder().where(WorkOutDao.Properties.Tag.eq(4)).list();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgress();
            switch (msg.what){
                case CodeType.SUCCESS:
                    if(msg.arg1 == CodeType.GETWORKOUT){
                        if(tag == Constants.TAG_SCHEDULE){
                            scheduleList = (List<WorkOut>) msg.obj;
                            schedultAdapter.update(scheduleList);

                            tag = Constants.TAG_INSTANT;
                            HttpImpl.getInstance(getActivity()).getWorkOut(handler,accessToken,type,tag,isOver);
                        }else{
                            instantList = (List<WorkOut>) msg.obj;
                            instantAdapter.update(instantList);
                        }
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
            }
        }
    };
}
