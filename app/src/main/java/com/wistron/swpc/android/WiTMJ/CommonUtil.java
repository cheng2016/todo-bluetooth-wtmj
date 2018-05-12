package com.wistron.swpc.android.WiTMJ;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wistron.swpc.android.WiTMJ.bean.Profile;
import com.wistron.swpc.android.WiTMJ.bean.Records;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Route;
import com.wistron.swpc.android.WiTMJ.util.googlemap.Segment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by zhoubin on 16-1-15.
 */
public class CommonUtil {
    public static final class Operations {
        private Operations() throws InstantiationException {
            throw new InstantiationException("This class is not for instantiation");
        }
        /**
         * Checks to see if the device is online before carrying out any operations.
         *
         * @return
         */
        public static boolean isOnline(Context context) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            return false;
        }
    }

    private CommonUtil() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    /*
    *将Date类型的时间转换为 服务器要求的时间格式（String类型）
    * */
    /*获取到的格式：2016-06-19T00:00:00.000+0800*/
    public static String getServerDateFormat(Date date){
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String s = format1.format(date);
        return s;
    }

    /*
    * 将服务器的时间格式（String类型）转换为Date类型
    * 将带z(及服务器格式的时间)转成Date时，需要加：setTimeZone(TimeZone.getTimeZone("GMT")),其他情况都不需要
    * */
    public static Date getStrToDate(String str){
        Date date =null ;
        try {
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//            System.out.println("readDate=="+readDate);
            readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
//            System.out.println("readDate2=="+readDate);
            date = readDate.parse(str);
//            System.out.println("date=="+date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /*将上传到服务器之前的时间格式转为Date类型
           上传到server之前的格式：A = 2016-06-21T16:43:36.042+0800,转成date类型（putrecord时，record中的date格式类型为A）
    */
    public static Date getBeforeStrToDate(String str){
        Date date =null ;
        try {
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//            readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = readDate.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /*将str=yyyy/MM/dd 格式转为server格式：在report中通过时间（server格式）查询record*/
    public static Date getYmdWthSlshStrToDate(String str){
        Date date =null ;
        try {
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy/MM/dd");
            date = readDate.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /*将str=yyyyMMdd 格式转为server格式：在report中通过时间（server格式）查询record*/
    public static Date getymdStrToDate(String str){
        Date date =null ;
        try {
            SimpleDateFormat readDate = new SimpleDateFormat("yyyyMMdd");
            date = readDate.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /*将不标准化格式(yyyy/M/d)转为server格式：yyyyMMdd,用来比较此格式的时间*/
    public static String getUnfmtStrToStr(String str){
        String s[]=str.split("/");
        String s0 = s[0];
        String s1 = s[1];
        String s2 = s[2];
        if (s1.length()<2){
            s1="0"+s1;
        }if (s2.length()<2){
            s2="0"+s2;
        }
        String newStr = s0+s1+s2;
        return newStr;
    }

    /*将服务器格式转化为yyyyMMdd格式:用于getWeekRecordsByDate中时间比较*/
    public static String getWeekRecordsStr(String str){
        Date date =null ;
        String s ="";
        try {
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = readDate.parse(str);
            java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyyMMdd");
             s = format1.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }

    /*获取最终week的时间：用于比较week类型时间格式的时间
        1.yyyy/M/d转成yyyyMMdd,2.yyyyMMdd转成Date,3.Date转为服务器格式
    */
    public static String getWeekDate(String str){
        String str1 = getUnfmtStrToStr(str);
        Date date = getymdStrToDate(str1);
        String str2 = getServerDateFormat(date);
        return str2;
    }
    public static String getWeekStrToServer(String str){
       String newStr = getUnfmtStrToStr(str);
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyyMMdd");
        java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String string1 ="";
        try {
            date = format1.parse(newStr);
            string1 = format2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return string1;
    }


    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results=new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }
    public static void setRecordPlus(PreferencesUtil mPreferencesUtil,Records records){
        double mDurMinsNum = Double.valueOf(mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_DURATION, "00"));
        double mDisNum = Double.valueOf(mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_DISTANCE, "0"));
        double mSpdNum = Double.valueOf(mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_SPEED, "0"));
        double mCalNum = Double.valueOf(mPreferencesUtil.getString(PreferenceConstants.HOME_CYCLING_CALORIES, "0"));
        mDurMinsNum += Double.valueOf(records.getDuration());
        mDisNum +=  Double.valueOf(records.getDistance());
        if (mSpdNum==0){
            mSpdNum = Double.valueOf( records.getAvg_speed());
        }else {
            mSpdNum += Double.valueOf( records.getAvg_speed());
            mSpdNum /=2;
        }
        mCalNum +=Double.valueOf(records.getCalories());
        mPreferencesUtil.saveString(PreferenceConstants.HOME_CYCLING_DURATION, String.valueOf(mDurMinsNum));
        mPreferencesUtil.saveString(PreferenceConstants.HOME_CYCLING_DISTANCE, String.valueOf(mDisNum));
        mPreferencesUtil.saveString(PreferenceConstants.HOME_CYCLING_SPEED, String.valueOf(mSpdNum));
        mPreferencesUtil.saveString(PreferenceConstants.HOME_CYCLING_CALORIES, String.valueOf(mCalNum));

    }

    public static String[] getDuration(Double duration){
        String str[] = new String[2];
        int m = (int) (duration / 60 % 60);
        int h = (int) (duration / 3600);
        String min = String.valueOf(m);
        String hr = String.valueOf(h);
        str[0] = hr;
        str[1] = min;
        return str ;
    }


    public static Double getDistance(Double distance){
        distance /= 1000;
        Double d =Math.floor(distance*10)/10;
        return d;
    }

    public static Double getSpeed(Double speed){
        speed = 3.6*speed;
        Double  d=Math.floor(speed*10)/10;
        return d;
    }

    public static Double getCalories(Double distance){
        distance =3.0*distance/8800;
        Double d =Math.floor(distance*10)/10;
      return  d;
    }

    public static void drawOneLine(Context context,GoogleMap map,Route route){
         LatLng start;
         LatLng end;
        map.clear();
        start = route.getStartPoint();
        end = route.getEndPoint();

        CameraUpdate center = CameraUpdateFactory.newLatLng(end);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
        map.moveCamera(center);
        map.animateCamera(zoom);
        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        map.addMarker(options);

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.width(10);
        polyOptions.color(context.getResources().getColor(R.color.blue));
        polyOptions.addAll(route.getPoints());
        Polyline polyline = map.addPolyline(polyOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(route.getLatLgnBounds(), 50);
        map.moveCamera(cameraUpdate);

    }
    public static Route parseRoute(JSONObject jsonRoute) throws JSONException {
        int distance = 0;
        Route route = new Route();
        //Create an empty segment
        Segment segment = new Segment();

        route.setRouteString(jsonRoute.toString());
        //Get the bounds - northeast and southwest
        final JSONObject jsonBounds = jsonRoute.getJSONObject("bounds");
        final JSONObject jsonNortheast = jsonBounds.getJSONObject("northeast");
        final JSONObject jsonSouthwest = jsonBounds.getJSONObject("southwest");

        route.setLatLgnBounds(new LatLng(jsonNortheast.getDouble("lat"), jsonNortheast.getDouble("lng")), new LatLng(jsonSouthwest.getDouble("lat"), jsonSouthwest.getDouble("lng")));

        //Get the leg, only one leg as we don't support waypoints
        final JSONObject leg = jsonRoute.getJSONArray("legs").getJSONObject(0);
        //Get the steps for this leg
        final JSONArray steps = leg.getJSONArray("steps");
        //Number of steps for use in for loop
        final int numSteps = steps.length();
        //Set the name of this route using the start & end addresses
        route.setName(leg.getString("start_address") + " to " + leg.getString("end_address"));
        route.setSummary(jsonRoute.getString("summary"));
        //Get google's copyright notice (tos requirement)
        route.setCopyright(jsonRoute.getString("copyrights"));
        //Get distance and time estimation
        route.setDurationText(leg.getJSONObject("duration").getString("text"));
        route.setDurationValue(leg.getJSONObject("duration").getInt("value"));
        route.setDistanceText(leg.getJSONObject("distance").getString("text"));
        route.setDistanceValue(leg.getJSONObject("distance").getInt("value"));
        route.setEndAddressText(leg.getString("end_address"));
        //Get the total length of the route.
        route.setLength(leg.getJSONObject("distance").getInt("value"));

        final JSONObject start_location = leg.getJSONObject("start_location");
        final JSONObject end_location = leg.getJSONObject("end_location");
        final LatLng end_pos1 = new LatLng(end_location.getDouble("lat"),
                end_location.getDouble("lng"));
        route.setEndPoint(end_pos1);
        final LatLng position1 = new LatLng(start_location.getDouble("lat"),
                start_location.getDouble("lng"));
        route.setStartPoint(position1);

        //Get any warnings provided (tos requirement)
        if (!jsonRoute.getJSONArray("warnings").isNull(0)) {
            route.setWarning(jsonRoute.getJSONArray("warnings").getString(0));
        }

                /* Loop through the steps, creating a segment for each one and
                 * decoding any polylines found as we go to add to the route object's
                 * map array. Using an explicit for loop because it is faster!
                 */
        for (int y = 0; y < numSteps; y++) {
            //Get the individual step
            final JSONObject step = steps.getJSONObject(y);
            //Get the start position for this step and set it on the segment
            final JSONObject start = step.getJSONObject("start_location");
            //zhoubin Get the end position for this step and set it on the segment
            final JSONObject end = step.getJSONObject("end_location");
            final LatLng end_pos = new LatLng(end.getDouble("lat"),
                    end.getDouble("lng"));
            segment.setEndPoint(end_pos);

            final LatLng position = new LatLng(start.getDouble("lat"),
                    start.getDouble("lng"));
            segment.setPoint(position);
            //Set the length of this segment in metres
            final int length = step.getJSONObject("distance").getInt("value");
            distance += length;
            segment.setLength(length);
            segment.setDistance((double)distance / (double)1000);
            //Strip html from google directions and set as turn instruction
            segment.setInstruction(step.getString("html_instructions").replaceAll("<(.*?)*>", ""));

            if(step.has("maneuver"))
                segment.setManeuver(step.getString("maneuver"));
            segment.setSend(false);
            //Retrieve & decode this segment's polyline and add it to the route.
            route.addPoints(decodePolyLine(step.getJSONObject("polyline").getString("points")));
            //Push a copy of the segment to the route
            route.addSegment(segment.copy());
        }
        return route;
    }
    public static List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

    /*在report的getDayRecordsByDate中：将yyyyMMdd格式时间转成server格式*/
    public static String getStrToServerStr(String str){

        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy/MM/dd");
        java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = null;
        String s ="";
        try {
            date = format1.parse(str);
            s = format2.format(date);
            System.out.println("===DATE==="+date);
            System.out.println("===s"+s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }


    /*将Date类型的时间转换为 physicalfragment 要求的时间格式 */
    public static String getDateToStr(Date date){
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("MM/dd HH:mm");
        String s = format1.format(date);
        return s;
    }

    /*将Date类型的时间转换为 detailActyfragment 要求的时间格式 */
    public static String getDetailActyFormat(Date date){
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm");
        String s = format1.format(date);
        return s;
    }
    /*将Date类型的时间转换为 detailActyfragment 要求的时间格式 */
    public static String getLongToStr(Date date ,int time){
        int t1 =  time*1000; //s-->ss
        Long min =  new Long(t1);
        long nowTimel =date.getTime();
        Long ss = min + nowTimel;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ss);
        Date time2 = c.getTime();
        DateFormat format1 = new java.text.SimpleDateFormat("HH:mm");
        String time2Str = format1.format(time2);
        return time2Str;
    }

    /*将yyyy/MM/dd转为yyyy-MM-dd*/
    public static String getStr1ToStr2(String str){
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy/MM/dd");
        java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String s ="";
        try {
            date = format1.parse(str);
            s = format2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static Date getFirstDayOfMonth() {
        //        String str = "";
        //        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        //        str = sdf.format(""+lastDate.getTime());
        return lastDate.getTime();
    }

    public static List<Date> dateToWeek(Date mdate) {
        int b = mdate.getDay();
        Date fdate;
        List<Date> list = new ArrayList<Date>();
        /*b+1-->从星期天开始*/
        Long fTime = mdate.getTime() - (b+1) * 24 * 3600000;
        for (int a = 1; a <= 7; a++) {
            fdate = new Date();
            fdate.setTime(fTime + (a * 24 * 3600000));
            list.add(a-1, fdate);
        }
        return list;
    }

    public static String[] getDaysOfWeek(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        List<Date> days = dateToWeek(date);
        String str[] = new String[7];
        for (int i = 0;i<days.size();i++){
           str[i] = sdf.format(days.get(i));
        }
        return str;
    }

    static String image_sas,image_url,userid;

    public static void uploadProfileImage(final String filepath, String accessToken, final Handler handler) {
        TmjConnection connection = TmjClient.create();
        Call<Profile> call = connection.getProfile(accessToken);//"Basic "+enc
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, retrofit2.Response<Profile> response) {
                // System.out.println("getImage_url：" + response.body().getImage_url());
                //  System.out.println("getProfile id：" + response.body().getProfile_id());
                //  System.out.println("getProfile：" + response.code() + "\t" + response.message());
                if (!response.isSuccessful()) {
                    LogUtils.d("", "uploadImage  " + "get profile false break");
                    return;
                }
                image_sas = response.body().getImage_sas();
                userid = response.body().getProfile_id();
                image_url = response.body().getImage_url();

                new AsyncTask<Void,Void,Void>() {
                    @Override
                    protected Void doInBackground(Void[] params) {

                        boolean b = uploadImage(
                                image_url + "?" + image_sas
                                , filepath,handler);
                        LogUtils.d("", "uploadImage  " + b + " url - " + image_url + "?" + image_sas);
                        return null;
                    }
                }.execute();

            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                System.out.println("getProfile：" + "onFailure  " + t.getMessage());
            }
        });
    }

    public static Boolean uploadImage(String serverURL,final String filepath ,final Handler handler) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            OkHttpClient client = builder.build();
            File file = new File(filepath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            Request request = new Request.Builder()
                    .url(serverURL)
                    .addHeader("X-ms-blob-type","BlockBlob")
                    .addHeader("Content-Type","binary/octet-stream")
                    .put(requestFile)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    System.out.println("onFailure  " + e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    System.out.println("uploadPic：" + response.code() + "\t" + response.message());
                    if (response.isSuccessful()) {
                        Message msg = new Message();
                        msg.what = response.code();
                        msg.arg1 = CodeType.UPLOAD_IMG_SUCCESS;
                        msg.obj = response.body();
                        handler.sendMessage(msg);
                        T.showShort("update image success");
                    }
                }
            });

            return true;
        } catch (Exception ex) {
            // Handle the error
            System.out.println(ex.getMessage());
        }
        return false;
    }
    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    public static int getMonthSumDay(int year , int month ){
        int day = 0;
        boolean isRn = year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
        if (month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12) {
            day = 31;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            day = 30;
        } else if (month == 2 && isRn == true) {
            day = 29;
        } else {
            day = 28;
        }
        return day;
    }
    /**
     * 获取下i周的日期
     * 上i周使用负数
     * 例如上周的日期 i传入-1
     * 本周 i=0
     * 下周 i=1
     *
     * */
    public static List<String> getCurDaysInweek(long i){
        List<String> l= new ArrayList<String>();

        Calendar cal =Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); //获取 周日的日期
        long lo=cal.getTimeInMillis();
        lo += 86400000*7*i;//下i周的日期

        cal.setTimeInMillis(lo);
        l.add(df.format(cal.getTime()));
        //  System.out.println(df.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        l.add(df.format(cal.getTime()));
        //   System.out.println(df.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        l.add(df.format(cal.getTime()));
        //   System.out.println(df.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        l.add(df.format(cal.getTime()));
        //  System.out.println(df.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        l.add(df.format(cal.getTime()));
        //  System.out.println(df.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        l.add(df.format(cal.getTime()));
        // System.out.println(df.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        l.add(df.format(cal.getTime()));
        //  System.out.println(df.format(cal.getTime()));

        return l;
    }
    /**
     *
     * 获得输入年月的 所有周 返回这几周的开始日期和结束日期
     * 分别存在 map中 例如：2016.6月的
     * 第一周
     * 1_begin 5/29 1_end 6/4
     * 第二周
     * 2_begin 6/5 2_end 6/11
     *
     * */
    public static Map<String, String> getCurrentWeeks(int year, int month) {
        Map<String, String> map = new HashMap<String, String>();
        //  int year = 0;
        // int month = 0;
        int day = 0;
        int sumdays = 0;
        int yearday = 0;//到1900年1月1日的天数
        int monthday = 0;//到当年1.1日的天数
        int week = 0;//该月第一天是星期几
        int weekday = 0;//当天是星期几
        //计算闰年
        boolean isRn = year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
        int preMonthday;
        int premonthNum;
        int preYear = year - 1;
        ;
        int nextYear = year + 1;
        ;
        int nextmonthNum;
        if (month == 1) {
            premonthNum = 12;
            preMonthday = getMonthSumDay(year - 1, 12);
        } else {
            preMonthday = getMonthSumDay(year, month - 1);
            premonthNum = month - 1;
        }

        if (month == 12) {
            nextmonthNum = 1;
        } else {
            nextmonthNum = month + 1;
        }


        day = getMonthSumDay(year, month);
        for (int i = 1900; i < year; i++) {
            if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                yearday = yearday + 366;
            } else {
                yearday = yearday + 365;
            }
        }
        for (int n = 1; n < month; n++) {
            if (n == 1 || n == 3 || n == 5 || n == 7 || n == 8 || n == 10
                    || n == 12) {
                monthday = monthday + 31;
            } else if (n == 4 || n == 6 || n == 9 || n == 11) {
                monthday = monthday + 30;
            } else if (n == 2 && isRn == true) {
                monthday = monthday + 29;
            } else {
                monthday = monthday + 28;
            }
        }
        int weeknum = 1;
        boolean flag = false;
        // int beginDate = 0;
        sumdays = yearday + monthday;
        week = 1 + sumdays % 7;
        System.out.println("sun\tmon\ttue\twed\tthu\tfri\tsat");//周日是一周的开始

        if (week != 7) {
            for (int j = 0; j < week; j++) {
                int d = (preMonthday - (week - j) + 1);
                System.out.print(d + "\t");
                if (j == 0) {
                    if (premonthNum == 12) {
                        map.put("1_begin", preYear + "/" + premonthNum + "/" + d);//第一周的开始日期
                    } else {
                        map.put("1_begin", year + "/" + premonthNum + "/" + d);//第一周的开始日期
                    }

                    // System.out.println("put " + "1_begin ===" + premonthNum+"/"+d+" ");
                }
            }

            flag = true;
        }

        for (int k = 1; k <= day; k++) {
            weekday = (sumdays - 1 + k) % 7 + 1;
            if (weekday == 6) {
                System.out.print(k + "\n");
                map.put(weeknum + "_end", year + "/" + month + "/" + k);
                // System.out.println("put " + weeknum+"_end ===" + month+"/"+k +" ");

                if ((k + 1) <= day) {//本月最后一周
                    map.put((weeknum + 1) + "_begin", year + "/" + month + "/" + (k + 1));
                    //  System.out.println("monthday " +day+ "  k+1 " + (k+1) );
                    //  System.out.println("put " + (weeknum+1)+"_begin ===" +month+"/"+(k+1));
                    if ((k + 7) > day) {//最后一周的最后一天，是下个月的几号
                        if (month == 12) {
                            map.put((weeknum + 1) + "_end", nextYear + "/" + nextmonthNum + "/" + ((k + 7) - day));
                        } else {
                            map.put((weeknum + 1) + "_end", year + "/" + nextmonthNum + "/" + ((k + 7) - day));
                        }

                        //     System.out.print("put " + (weeknum+1)+"_end ===" + nextmonthNum+"/"+((k+7)-day) +" ");;
                    }
                }

                weeknum++;
            } else {
                if (!flag && k == 1) {
                    map.put("1_begin",year +"/"+ month + "/" + 1);
                    //  System.out.println("put " + "1_begin ===" +month+"/"+1+" ");//第一周的开始日期
                }

                System.out.print(k + "\t");

            }
        }

        return map;
    }


    public static List<String> getWeeksOfMonth (int year,int month) {
        ArrayList<String> weekList = new ArrayList<String>();
/*        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int numOfWeeks = c.getActualMaximum(Calendar.WEEK_OF_MONTH);
        int whichWeek = c.get(Calendar.WEEK_OF_MONTH);
        System.out.println(" actual "+numOfWeeks+" which week "+whichWeek);*/
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT"));
/*        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH)+1;*/
        int numOfWeeks = c.getActualMaximum(Calendar.WEEK_OF_MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        System.out.println("day =="+day);
        Map<String,String> m =   getCurrentWeeks(year,month);
        Set<Map.Entry<String, String>> allSet=m.entrySet();

        Iterator<Map.Entry<String, String>> iter=allSet.iterator();
        String week1bgn = "";
        String week1end = "";
        String week2bgn = "";
        String week2end = "";
        String week3bgn = "";
        String week3end = "";
        String week4bgn = "";
        String week4end = "";
        String week5bgn = "";
        String week5end = "";

        while(iter.hasNext()){
            Map.Entry<String, String> me=iter.next();
            System.out.println(me.getKey()+ " "+me.getValue());
            if (me.getKey().equals("1_begin")){
                week1bgn = me.getValue();
            }else if (me.getKey().equals("1_end")){
                week1end = "~" +me.getValue();
            }else if (me.getKey().equals("2_begin")){
                week2bgn = "" +me.getValue();
            }else if (me.getKey().equals("2_end")){
                week2end = "~" +me.getValue();
            }else if (me.getKey().equals("3_begin")){
                week3bgn = "" +me.getValue();
            }else if (me.getKey().equals("3_end")){
                week3end = "~" +me.getValue();
            }else if (me.getKey().equals("4_begin")){
                week4bgn = "" +me.getValue();
            }else if (me.getKey().equals("4_end")){
                week4end = "~" +me.getValue();
            }
            if (numOfWeeks>4){
                if (me.getKey().equals("5_begin")){
                    week5bgn = "" +me.getValue();
                }else if (me.getKey().equals("5_end")){
                    week5end = "~" +me.getValue();
                }
            }
        }
        weekList.add(0,week1bgn+week1end);
        weekList.add(1,week2bgn+week2end);
        weekList.add(2,week3bgn+week3end);
        weekList.add(3,week4bgn+week4end);
        if (!"".equals(week5bgn)){
            weekList.add(4,week5bgn+week5end);
        }
        return weekList;

    }

    public static List<String> getFiveYear(int i){
        List<String> list = new ArrayList<String>();
        Calendar calndar = Calendar.getInstance();
        calndar.setTimeZone(TimeZone.getTimeZone("GMT"));
        int currentYear = calndar.get(Calendar.YEAR);
        list.add(0,String.valueOf(currentYear+(5*i-2)));
        list.add(1,String.valueOf(currentYear+(5*i-1)));
        list.add(2,String.valueOf(currentYear+(5*i)));
        list.add(3,String.valueOf(currentYear+(5*i+1)));
        list.add(4,String.valueOf(currentYear+(5*i+2)));
        return list;
    }


    public static String  rePlaceManeuver(String maneuver){
        if(TextUtils.isEmpty(maneuver)) return "";
        String newManeuver = "";
        if(maneuver.startsWith("uturn")){
            if(maneuver.equals("uturn-left")){
                newManeuver = maneuver.replace(maneuver,"turn_left");
            }
            if(maneuver.equals("uturn-right")){
                newManeuver = maneuver.replace(maneuver,"turn_right");
            }
        }else{
            if(maneuver.contains("-left")){
                newManeuver = maneuver.replace(maneuver,"left");
            }
            if(maneuver.contains("-right")){
                newManeuver = maneuver.replace(maneuver,"right");
            }
        }
        return newManeuver;
    }

}
