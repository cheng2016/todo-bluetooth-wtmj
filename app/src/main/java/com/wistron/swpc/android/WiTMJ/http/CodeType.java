package com.wistron.swpc.android.WiTMJ.http;

/**
 * Created by WH1604025 on 2016/6/2.
 */
public class CodeType {
    public final static int SUCCESS = 200;
    public final static int TOKENERROR =  401;
    public final static int ERRORRQUEST = 400;

    public final static int FAILED =  0x000;

    public final static  int NETWORN_NONE = 0x100;

    public final static int LOGIN = 0x101;
    public final static int LOGOUT = 0x102;
    public final static int PROFILE = 0x103;
    public final static int RECORDS = 0x104;
    public final static int GETWORKOUT = 0x105;
    public final static int REFRESH = 0x106;

    public final static int SIGNFACEBOOK = 0x107;

    public final static int ADDWORKOUT = 0x108;
    public final static int DELETEWORKOUT = 0x109;

    public final static int UPDATEWORKOUT = 0x110;

    public final static int GETUSER = 0x111;
    public final static int WORKDETAIL = 0x112;

    public final static int getINVITATION = 0x113;

    public final static int UPDATEINVITATION = 0x114;

    public final static int UPLOAD_IMG_SUCCESS = 200;
}
