package com.wistron.swpc.android.WiTMJ.competition;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.ManagerFragment;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.base.BaseFragment;
import com.wistron.swpc.android.WiTMJ.util.L;

/**
 * Created by WH1604025 on 2016/4/18.
 */
public class CompetitionFragment extends BaseFragment implements View.OnClickListener{
    private final static String TAG = "CompetitionFragment";

    InstantFragment mInstantFragment;
    ScheduleFragment mScheduleFragment;

    private Fragment mCurrent=null;

    TextView instanceTv,scheduleTv;

    private View mMainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mMainView = inflater.inflate(R.layout.fragment_competition, null);
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
        instanceTv = (TextView) mMainView.findViewById(R.id.instanceTv);
        scheduleTv = (TextView) mMainView.findViewById(R.id.scheduleTv);
        mInstantFragment= new InstantFragment();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.content, mInstantFragment).commitAllowingStateLoss();
        mCurrent = mInstantFragment;
        ManagerFragment.getInstance().addfragment(mInstantFragment);
    }

    @Override
    public void initEvent() {
        instanceTv.setOnClickListener(this);
        scheduleTv.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //切换Fragment
    public void switchContent(Fragment from,Fragment to){
        if(mCurrent !=to){
            ManagerFragment.getInstance().addfragment(to);
            mCurrent=to;
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
//			transaction.replace(R.id.id_content, to).commitAllowingStateLoss();
            if(!to.isAdded()){
                transaction.hide(from).add(R.id.content, to).commitAllowingStateLoss();
            }else{
                transaction.hide(from).show(to).commitAllowingStateLoss();
            }
        }
    }

    private void changeButtonDrawable(int position)
    {
        instanceTv.setTextColor(getResources().getColor(R.color.title_text_normal));
        instanceTv.setBackgroundResource(R.color.title_bg_normal);
        scheduleTv.setTextColor(getResources().getColor(R.color.title_text_normal));
        scheduleTv.setBackgroundResource(R.color.title_bg_normal);

        switch(position)
        {
            case 0:
                instanceTv.setBackgroundResource(R.color.title_bg_select);
                instanceTv.setTextColor(getResources().getColor(R.color.title_text_select));
                break;
            case 1:
                scheduleTv.setBackgroundResource(R.color.title_bg_select);
                scheduleTv.setTextColor(getResources().getColor(R.color.title_text_select));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
           case R.id.instanceTv :
               changeButtonDrawable(0);
               if(mInstantFragment==null){
                   mInstantFragment=new InstantFragment();
               }
               switchContent(mCurrent, mInstantFragment);
                break;
            case R.id.scheduleTv :
                changeButtonDrawable(1);
                if(mScheduleFragment==null){
                    mScheduleFragment=new ScheduleFragment();
                }
                switchContent(mCurrent, mScheduleFragment);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.i(TAG,"onDestroy");
    }

}
