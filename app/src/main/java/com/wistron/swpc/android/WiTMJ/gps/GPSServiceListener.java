package com.wistron.swpc.android.WiTMJ.gps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.gson.JsonObject;
import com.wistron.swpc.android.WiTMJ.LogUtils;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by WH1604041 on 2016/5/31.
 */


public class GPSServiceListener implements LocationListener {

    private static final String TAG = "GPSServiceListener";
    private JsonObject jsonObject;//gpsLocation json
    public int GPSCurrentStatus;

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //实时同步Application中的定位坐标
            TmjApplication.getInstance().setLocation(location);
            L.i("Application",location.getLatitude() + "\t" + location.getLongitude());

            jsonObject = new JsonObject();
            LogUtils.i(TAG,"zlb====location.getLongitude() "+location.getLongitude());
            LogUtils.i(TAG,"zlb====location.getLatitude() "+location.getLatitude());
            jsonObject.addProperty("longitude",location.getLongitude());
            jsonObject.addProperty("latitude",location.getLatitude());
            jsonObject.addProperty("workout_id","");
            TmjConnection connection = TmjClient.create();
            String accessToken = PreferencesUtil.getPrefString(TmjApplication.getInstance(), PreferenceConstants.ACCESS_TOKEN, "");
            Call<ResponseBody> call = connection.putGps(accessToken,jsonObject);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        L.i(TAG, "putGPSLocation：" + response.code()+"\t" + response.message());
                        L.i(TAG,jsonObject.toString());
                    } else {
                        L.i(TAG, "Failure  " + response.code() + "\t" + response.message());
                        T.showShort("Failure  " + response.code() + "\t" + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    L.i(TAG, "onFailure  " + t.getMessage());
                    T.showShort("onFailure  " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        GPSCurrentStatus = status;
    }
}