package com.wistron.swpc.android.WiTMJ.http;

import android.os.Handler;

import com.google.gson.JsonObject;
import com.wistron.swpc.android.WiTMJ.http.request.InvitationRequest;
import com.wistron.swpc.android.WiTMJ.http.request.LogoutRequest;
import com.wistron.swpc.android.WiTMJ.http.request.RefreshRequest;
import com.wistron.swpc.android.WiTMJ.http.request.WorkOutRequest;

import org.json.JSONObject;

/**
 * Created by WH1604025 on 2016/6/1.
 */
public interface Http {
    public void login(Handler handler,String auth);

    public void loginOut(Handler handler,String accessToken,LogoutRequest refreshToken);

    public  void getProfile(Handler handler,String auth);

    public  void getWorkOut(Handler handler,String auth,int type, int tag,boolean isOver, int size);

    public  void getWorkOut(Handler handler,String auth,int type, int tag,boolean isOver);

    public  void refresh(Handler handler,RefreshRequest refreshToken);

    public void getRecords(Handler handler,String accessToken,String startDate,String endDate/*,Integer size*/);

    public void signFacebook(Handler handler,String accessToken);

    public void addWorkout(Handler handler,String accessToken,JsonObject request);

    public void deleteWorkout(Handler handler,String accessToken,String id);

    public void updateWorkout(Handler handler,String accessToken,String id,JsonObject request);

    public void getUsers(Handler handler,String accessToken,String keyword,String gpsLocation);

    public void getWorkDetail(Handler handler,String accessToken,String work_id);

    public void updateInvitation(Handler handler, String accessToken, String work_id, InvitationRequest invitationRequest);
}
