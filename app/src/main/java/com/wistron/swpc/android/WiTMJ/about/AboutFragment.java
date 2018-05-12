package com.wistron.swpc.android.WiTMJ.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.base.BaseFragment;

/**
 * Created by WH1604041 on 2016/4/26.
 */
public class AboutFragment extends BaseFragment {
    private View mAboutFragment;
    private LinearLayout ll_about;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAboutFragment = getActivity().getLayoutInflater().inflate(R.layout.fragment_about, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup p = (ViewGroup) mAboutFragment.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mAboutFragment;
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
        ll_about = (LinearLayout) mAboutFragment.findViewById(R.id.ll_about);
    }

    @Override
    public void initEvent() {
        ll_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void initData() {

    }


}
