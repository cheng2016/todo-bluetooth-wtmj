package com.wistron.swpc.android.WiTMJ.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.widget.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

//import me.drakeet.materialdialog.MaterialDialog;


/**
 * Created by WH1604025 on 2016/4/18.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private final static String TAG = "BaseActivity";

    private static ProgressDialog progressDialog;

    private CustomDialog customDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i(TAG,"onCreate()");
    }

    @Override
    public void onStart() {
        super.onStart();
        L.i(TAG,"onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        L.i(TAG,"onRestart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.i(TAG,"onResume()");
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void onEventMainThread(Object event){

    }

    @Override
    public void onPause() {
        super.onPause();
        L.i(TAG,"onPause()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        L.i(TAG,"onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.i(TAG,"onDestroy()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.i(TAG,"onActivityResult()");
    }

    public abstract void initView();

    public abstract void initEvent();

    public abstract void initData();

    public synchronized ProgressDialog getProgress() {
        if(progressDialog==null){
            progressDialog = new ProgressDialog(this);
        }
        return progressDialog;
    }

    public void showProgress(String title,String message) {
        if(progressDialog==null){
            progressDialog  = new ProgressDialog(this);
            if(TextUtils.isEmpty(title)){
                progressDialog.setTitle("Progress Dialog");
            }else{
                progressDialog.setTitle(title);
            }
            if(TextUtils.isEmpty(message)){
                progressDialog.setMessage("Please wait...");
            }else{
                progressDialog.setMessage(message);
            }
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    public void showProgress(String message) {
        if(progressDialog==null){
            progressDialog  = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    public void hideProgress(){
        if(progressDialog!=null){
            progressDialog.dismiss();
            progressDialog=null;
        }
    }

    public void  showCustomDialog(String title,String content,DialogInterface.OnClickListener listener,boolean isSingle){
            CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(content);
            builder.setPositiveButton("Ok", listener);
            if(!isSingle){
                builder.setNegativeButton("Cancel",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
            customDialog = builder.create();
            customDialog.show();
    }

    public void dismissCustomDialog(){
        if(customDialog!=null)
            if(customDialog.isShowing()){
                customDialog.dismiss();
                customDialog=null;
            }
    }

    public void setViewText(TextView view,String text){
       if(TextUtils.isEmpty(text)){
            view.setText("");
        }else {
           view.setText(text);
       }
    }

    public void setViewText(EditText view, String text){
        if(TextUtils.isEmpty(text)){
            view.setText("");
        }else {
            view.setText(text);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void hideKeyBoard(View v) {
        InputMethodManager imm=(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        }
    }
}
