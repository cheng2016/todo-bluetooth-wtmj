package com.wistron.swpc.android.WiTMJ.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import com.google.gson.Gson;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.NetUtil;
import com.wistron.swpc.android.WiTMJ.util.T;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by WH1604025 on 2016/6/1.
 */
public class BaseHttp {
    private final static String TAG = "BaseHttp";

    private Context mContext;

    private ProgressDialog progressDialog;

    protected void postEvent(Object object){
        EventBus.getDefault().post(object);
    }

//    private CustomDialog customDialog;

    public Gson gson = new Gson();

    public BaseHttp(Context mContext) {
        this.mContext = mContext;
    }

   /*public void showCustomDialog(String title, String content, DialogInterface.OnClickListener listener, boolean isSingle){
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
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
        }else{
            customDialog.setCancelable(false);
            customDialog.setCanceledOnTouchOutside(false);
        }
        customDialog = builder.create();
        customDialog.show();
    }*/

   /* public void showProgress(String title,String message) {
        if(progressDialog==null){
            progressDialog  = new ProgressDialog(mContext);
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
            progressDialog  = new ProgressDialog(mContext);
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
    }*/

    // 创建JSONObject对象
    public JSONObject createJSONObject(String key , String values) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(key, values);
            L.i(TAG, "jsonObject：" + jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void sendFailMessage(Handler handler,Throwable t){
        L.i(TAG, "onFailure：" + t.getMessage());
        if (NetUtil.getNetworkState(mContext) == NetUtil.NETWORN_NONE){
            handler.sendEmptyMessage(CodeType.NETWORN_NONE);
            T.showLong(mContext,"连接服务器失败，请检查网络连接状态！");
        }else{
            handler.sendEmptyMessage(CodeType.FAILED);
            T.showLong(mContext,t.getMessage());
        }
    }
}
