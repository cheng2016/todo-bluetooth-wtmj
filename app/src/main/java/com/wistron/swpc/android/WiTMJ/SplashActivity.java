package com.wistron.swpc.android.WiTMJ;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;

/**
 * Created by WH1604025 on 2016/5/26.
 */
public class SplashActivity extends BaseActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉状态栏
        setContentView(R.layout.activity_splash);
        initView();
        initData();
        initEvent();

    }

    @Override
    public void onEventMainThread(Object event){
        super.onEventMainThread(event);
    }


    @Override
    public void initView() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(PreferencesUtil.getPrefBoolean(getApplicationContext(), PreferenceConstants.AUTOLOGIN,false)){
                    startActivity(new Intent().setClass(SplashActivity.this,MainActivity.class));
                    overridePendingTransition(R.anim.little_out, R.anim.little_in);
                    finish();
                }else{
                    startActivity(new Intent().setClass(SplashActivity.this,LoginActivity.class));
                    overridePendingTransition(R.anim.little_out, R.anim.little_in);
                    finish();
                }
            }
        },2000);
    }
}
