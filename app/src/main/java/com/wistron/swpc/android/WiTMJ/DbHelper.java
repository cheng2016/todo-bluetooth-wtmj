package com.wistron.swpc.android.WiTMJ;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wistron.swpc.android.WiTMJ.dao.DaoMaster;
import com.wistron.swpc.android.WiTMJ.dao.DaoSession;

/**
 * Created by WH1106013 on 2016/4/1.
 */
public class DbHelper {
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private DbHelper(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "tmj-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
    public static DbHelper getInstance(Context context){
        return new DbHelper(context);
    }

    public DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
