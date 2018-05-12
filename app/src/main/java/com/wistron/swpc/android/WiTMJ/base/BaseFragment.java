package com.wistron.swpc.android.WiTMJ.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.widget.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by WH1604025 on 2016/4/18.
 */
public abstract class BaseFragment extends Fragment {
    private final static String TAG = "BaseFragment";

    private ProgressDialog progressDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        L.i(TAG, "onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i(TAG, "onCreate()");
        progressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        L.i(TAG, "onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        L.i(TAG, "onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        L.i(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.i(TAG, "onResume()");
        if(!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void onEventMainThread(Object event){

    }

    @Override
    public void onPause() {
        super.onPause();
        L.i(TAG, "onPause()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        L.i(TAG, "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        L.i(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.i(TAG, "onDestroy()");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.i(TAG, "onActivityResult()");
    }

    public abstract void initView();

    public abstract void initEvent();

    public abstract void initData();

    public void startActivity(BaseActivity activity) {
        startActivity(new Intent().setClass(getActivity(), activity.getClass()));
    }

    public void showProgress(String title,String message) {
        if(progressDialog==null){
            progressDialog  = new ProgressDialog(getActivity());

        }else if(progressDialog.isShowing()){
            return;
        }

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

    public void showProgress(String message) {
        if(progressDialog==null){
            progressDialog  = new ProgressDialog(getActivity());
        }else if(progressDialog.isShowing()){
            return;
        }
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void hideProgress(){
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog=null;
        }
    }

    public void showCustomDialog(String title, String content, DialogInterface.OnClickListener listener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton("Ok", listener);
        builder.setNegativeButton("Cancel",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public void showCustomDialog(String title, String content, DialogInterface.OnClickListener listener, boolean isSingle) {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton("Ok", listener);
        if (!isSingle) {
            builder.setNegativeButton("Cancel",
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        builder.create().show();
    }

    public void showCustomDialogOK(String title, String content, DialogInterface.OnClickListener listener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton("Ok", listener);
        builder.create().show();
    }

    public void showCustomDialogCancel(String title, String content, DialogInterface.OnClickListener listener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNegativeButton("Cancel",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public void showCustomDialog(String title, String content, String positiveStr, DialogInterface.OnClickListener listener, boolean isSingleChoice) {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(content);
        //如果不是单选则创建Cancel按钮
        if (!isSingleChoice) {
            builder.setNegativeButton("Cancel",
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        builder.setPositiveButton(positiveStr, listener);
        builder.create().show();
    }


}
