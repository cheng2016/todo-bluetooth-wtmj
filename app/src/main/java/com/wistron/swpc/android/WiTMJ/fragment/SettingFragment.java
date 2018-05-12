package com.wistron.swpc.android.WiTMJ.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wistron.swpc.android.WiTMJ.MainActivity;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.base.BaseFragment;

/**
 * Created by WH1604025 on 2016/5/4.
 */
public class SettingFragment extends BaseFragment{

    private final static String TAG = "SettingFragment";
    private View mMainView;

    private ImageView statusImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mMainView = inflater.inflate(R.layout.fragment_setting, null);
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
    }

    @Override
    public void initView() {
        statusImg = (ImageView) mMainView.findViewById(R.id.statusImg);
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(TmjApplication.getInstance().isConneced()){
            statusImg.setBackgroundResource(R.drawable.link_break);
        }else{
            statusImg.setBackgroundResource(R.drawable.icon_bluetooth);
        }
    }
}
