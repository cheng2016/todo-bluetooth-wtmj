package com.wistron.swpc.android.WiTMJ.gps;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


/**
 * Created by WH1604041 on 2016/5/31.
 */
public class GPSService extends Service {
    private static final String TAG = "GPSService";
    private static final long minTime = 1000000;
    private static final float minDistance = 0;
    private LocationManager locationManager;
    private LocationListener locationListener;

    //service bind with activity.
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind GPSService ");
        return null;
    }

    //create service
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate GPSService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, " onStartCommand");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSServiceListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance,
                locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance,
                locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance,
                locationListener);
        return super.onStartCommand(intent, flags, startId);
    }

    //destroy service
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Destroy Service");
    }
}
