package com.wistron.swpc.android.WiTMJ.competition;

import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.widget.FlowLayout;
import com.wistron.swpc.android.WiTMJ.widget.FlowTextView;

/**
 * Created by WH1604025 on 2016/5/3.
 */
public class HostDetailActivity extends BaseActivity implements View.OnClickListener{
    private ActionBar actionBar;
    private TextView titleTv;
    private ImageView titleIcon;

    private PercentRelativeLayout backBtn;



    private FlowLayout fowLayout;

    private String[] mVals =new String[]{
            "Gwen","Wendy"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_detail);
        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.app_actionbar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);

        initView();
        initEvent();
        initData();

    }

    @Override
    public void initView() {
        titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.titleTv);
        titleTv.setText("Competition");
        titleIcon = (ImageView) actionBar.getCustomView().findViewById(R.id.titleIcon);
        titleIcon.setBackgroundResource(R.drawable.competition_white);
        titleIcon.setVisibility(View.VISIBLE);
        backBtn = (PercentRelativeLayout) actionBar.getCustomView().findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        fowLayout = (FlowLayout) findViewById(R.id.fowLayout);
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {
        for (int i = 0; i < mVals.length; i++) {
            FlowTextView tv = new FlowTextView(this,false);
            tv.setText(mVals[i]);
            fowLayout.getList().add(mVals[i]);
            fowLayout.addView(tv);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtn:
                finish();
                break;
        }
    }
}
