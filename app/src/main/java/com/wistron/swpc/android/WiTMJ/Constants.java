package com.wistron.swpc.android.WiTMJ;

/*
 * define global constants
 */
public final class Constants {

    public static final String ROOT_PATH = "/";
    public static final String SDCARD_PATH = ROOT_PATH + "sdcard";

    public static final String GOOGLE_DIRECTION_KEY="AIzaSyBhmt2ZQP0OGFThdnvd0ZcE-P86bFKl7U8";


    public static final int PUSH_REQUEST_CODE = 1;
    public static final int PUSH_RESULT_CODE = 2;
    public static final int CMPTIONDETAIL_REQUEST_CODE = 3;

    public static final int  INIT_OPPONENT= 0x111;
    public static final int  EXIST_OPPONENT= 0x112;
    public static final int  NO_OPPONENT= 0x113;
    public static final int EXIST_OPPONENT_FROM_SCHEDULE = 0x114;

    public static final int TAG_NAVIGATION = 1;
    public static final int TAG_TRAINING = 2;
    public static final int TAG_INSTANT = 3;
    public static final int TAG_SCHEDULE = 4;

    /*从report界面跳到详情界面*/
    public static final String TAG_NAVIGATION_TO_DETAIL ="tag_navigation_to_detail";
    public static final String TAG_TRAINING_TO_DETAIL ="tag_training_to_detail";
    public static final String TAG_INSTANT_TO_DETAIL ="tag_instant_to_detail";

    /*在complete时跳转到详情界面*/
    public static final int TAG_NAVIGATION_CMPLETE_TO_DETAIL = 1;
    public static final int TAG_TRAINING_CMPLETE_TO_DETAIL = 2;
    public static final int TAG_INSTANT_CMPLETE_TO_DETAIL = 3;

    public static final int PERMISSION_PUBLIC = 0;
    public static final int PERMISSION_OFFICIAL = 1;//public = 0, official = 1, private = 2, all = 3
    public static final int PERMISSION_PRIVATE = 2;
    public static final int PERMISSION_ALL = 3;

    public static final int STATUS_INIT = 0;
    public static final int STATUS_UPLOADED = 1;

    public static final int INVITATION_UNSOLVED = 0;
    public static final int INVITATION_OK = 1;
    public static final int INVITATION_DENY = 2;

    /*physical report
    * */
    public static final int INITVIEW = 0X000;

    public static final int HOST = 100;
    public static final int EDIT = 200;

    public static final int DATE_DAY = 1;
    public static final int DATE_WEEK = 2;
    public static final int DATE_MONTH = 3;
    public static final int DATE_YEAR = 4;

    public static final int RADIO_DISTANCE = 1;
    public static final int RADIO_SPEED = 2;
    public static final int RADIO_CALORIES = 3;

    public static final int XASIS_DAY_SEVEN = 7;
    public static final int XASIS_WEEK_FOUR = 4;
    public static final int XASIS_WEEK_FIVE = 5;
    public static final int XASIS_MONTH_TWELVE = 12;
    public static final int XASIS_YEAR_FIVE = 5;

    public static final int DISTANCE_RGB_RED = 195;
    public static final int DISTANCE_RGB_GREEN = 13;
    public static final int DISTANCE_RGB_BLUE = 35;

    public static final int SPEED_RGB_RED = 242;
    public static final int SPEED_RGB_GREEN = 150;
    public static final int SPEED_RGB_BLUE = 0;

    public static final int CALORIES_RGB_RED = 141;
    public static final int CALORIES_RGB_GREEN = 194;
    public static final int CALORIES_RGB_BLUE = 31;


    public static class Config {
        public static final boolean DEVELOPER_MODE = true;
    }


}
