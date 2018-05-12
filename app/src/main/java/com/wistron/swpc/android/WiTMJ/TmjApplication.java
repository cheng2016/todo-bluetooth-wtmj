package com.wistron.swpc.android.WiTMJ;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wistron.swpc.android.WiTMJ.util.L;

/**
 * Created by WH1106013 on 2016/4/1.
 */
public class TmjApplication extends MultiDexApplication {
    private static DbHelper dbHelper;

    private static String TAG = "TmjApplication";

    private static TmjApplication sInstance;
    private SharedPreferences settings;
    private String userid;
    private String userName;
    private String image_url;
    private String userEmail;

    private Location location;

    public synchronized static TmjApplication getInstance() {
        return sInstance;
    }

    private boolean isConneced = false;//TMJ设备是否已连接

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
//        FacebookSdk.sdkInitialize(this);
    }

    @Override
    public void onCreate() {
      /*  if (Constants.Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
        }*/
        super.onCreate();
        L.i(TAG,"onCreate()");
        sInstance = this;
        ApplicationHolder.setApplication(this);
        dbHelper = DbHelper.getInstance(this);
        settings = this.getSharedPreferences("TMJ_SETTINGS", Context.MODE_PRIVATE);
        initImageLoader(getApplicationContext());
    }

    public DbHelper getDbHelper() {
        return dbHelper;
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
       config.threadPriority(Thread.NORM_PRIORITY - 2);
        //config.denyCacheImageMultipleSizesInMemory() ;
      // config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public boolean isConneced() {
        return isConneced;
    }

    public void setConneced(boolean conneced) {
        isConneced = conneced;
    }

    public SharedPreferences getSettings() {
        return settings;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}