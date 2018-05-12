package com.wistron.swpc.android.WiTMJ.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wistron.swpc.android.WiTMJ.ApplicationHolder;
import com.wistron.swpc.android.WiTMJ.bean.Host;
import com.wistron.swpc.android.WiTMJ.bean.Invitation;
import com.wistron.swpc.android.WiTMJ.bean.Participants;
import com.wistron.swpc.android.WiTMJ.bean.Profile;
import com.wistron.swpc.android.WiTMJ.bean.Records;
import com.wistron.swpc.android.WiTMJ.bean.WorkOut;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.dao.HostDao;
import com.wistron.swpc.android.WiTMJ.dao.ParticipantsDao;
import com.wistron.swpc.android.WiTMJ.dao.ProfileDao;
import com.wistron.swpc.android.WiTMJ.dao.RecordsDao;
import com.wistron.swpc.android.WiTMJ.dao.WorkOutDao;
import com.wistron.swpc.android.WiTMJ.http.request.InvitationRequest;
import com.wistron.swpc.android.WiTMJ.http.request.LogoutRequest;
import com.wistron.swpc.android.WiTMJ.http.request.RefreshRequest;
import com.wistron.swpc.android.WiTMJ.http.request.WorkOutRequest;
import com.wistron.swpc.android.WiTMJ.http.response.AddWorkResponse;
import com.wistron.swpc.android.WiTMJ.http.response.GetUserResponse;
import com.wistron.swpc.android.WiTMJ.http.response.GetWorkResponse;
import com.wistron.swpc.android.WiTMJ.http.response.SignResponse;
import com.wistron.swpc.android.WiTMJ.http.response.Token;
import com.wistron.swpc.android.WiTMJ.http.response.WorkDetailResponse;
import com.wistron.swpc.android.WiTMJ.util.L;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by WH1604025 on 2016/6/1.
 */
public class HttpImpl extends BaseHttp implements Http {
    private final static String TAG = "HttpImpl";

    private Context mContext;

    private TmjConnection connection;

    private static HttpImpl httpImpl;

    private ProfileDao myProfileDao;
    private WorkOutDao myWorkOutDao;
    private RecordsDao myRecordsDao;
    private HostDao myHostDao;

    private ParticipantsDao participantsDao;


    public static synchronized HttpImpl getInstance(Context context) {
        if (httpImpl == null) {
            httpImpl = new HttpImpl(context);
        }
        return httpImpl;
    }

    public HttpImpl(Context context) {
        super(context);
        this.mContext = context;
        connection = TmjClient.create();

    }

    @Override
    public void login(final Handler handler, String auth) {
        Call<Token> call = connection.login(auth);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, retrofit2.Response<Token> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.LOGIN;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void loginOut(final Handler handler, String accessToken, LogoutRequest logoutRequest) {
        Call<ResponseBody> call = connection.logout(accessToken, logoutRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.LOGOUT;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void getProfile(final Handler handler, String auth) {
        Call<Profile> call = connection.getProfile(auth);//"Basic "+enc
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, retrofit2.Response<Profile> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.PROFILE;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }


    public void getInvitation(final Handler handler, String auth) {
        Call<List<Invitation>> call = connection.getWorkOutSimple(auth);//"Basic "+enc
        call.enqueue(new Callback<List<Invitation>>() {
            @Override
            public void onResponse(Call<List<Invitation>> call, retrofit2.Response<List<Invitation>> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.getINVITATION;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<List<Invitation>> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void getWorkOut(final Handler handler, String auth, int type, int tag,boolean isOver, int size) {
        Call<List<GetWorkResponse>> call = connection.workout(auth, type, tag,isOver,size);
        call.enqueue(new Callback<List<GetWorkResponse>>() {
            @Override
            public void onResponse(Call<List<GetWorkResponse>> call, Response<List<GetWorkResponse>> response) {

                Message msg = new Message();

                myWorkOutDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getWorkOutDao();

                myHostDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getHostDao();

                participantsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getParticipantsDao();

                List<GetWorkResponse> list = response.body();
                List<Participants> participantsList = new ArrayList<Participants>();

//                myWorkOutDao.deleteAll();
//                participantsDao.deleteAll();
//                myHostDao.deleteAll();
                L.i(TAG, "myWorkOutDao_participantsDao_myHostDao  is deleteAll");


                for (int i = 0; i < list.size(); i++) {
                    participantsList = gson.fromJson(list.get(i).getParticipants(), new TypeToken<List<Participants>>() {}.getType());
                    for (int j = 0; j < participantsList.size(); j++) {
                        participantsList.get(j).setWorkout_id(list.get(i).getId());
                    }
                    WorkOut myWorkOut = new WorkOut(list.get(i).getId(),
                            list.get(i).getTag(),
                            list.get(i).getPermission(),
                            list.get(i).getName(),
                            list.get(i).getStart_date(),
                            list.get(i).getEnd_date(),
                            list.get(i).getStart_location(),
                            list.get(i).getEnd_location(),
                            list.get(i).getRoute(),
                            list.get(i).getNote(),
                            list.get(i).getCustom_cal(),
                            list.get(i).getCustom_distance(),
                            list.get(i).getCustom_speed(),
                            list.get(i).getCustom_duration(),
                            list.get(i).getHost().getHost_id());

                    myWorkOutDao.insertOrReplace(myWorkOut);

                    Host myHost = new Host(list.get(i).getHost().getHost_id(),
                            list.get(i).getHost().getUsername(),
                            list.get(i).getHost().getImage_url(),
                            list.get(i).getHost().getEmail());
                    myHostDao.insertOrReplace(myHost);

                    participantsDao.deleteInTx(participantsDao.queryBuilder().where(ParticipantsDao
                            .Properties.Workout_id.eq(list.get(i).getId())).list());

                    participantsDao.insertOrReplaceInTx(participantsList);
                }


                List<WorkOut> workOutList = myWorkOutDao.queryBuilder().where(WorkOutDao.Properties.Tag.eq(4)).list();
                L.i(TAG, "MyWorkOut list size is " + workOutList.size());

                List<Host> hostList = myHostDao.queryBuilder().list();
                L.i(TAG, "MyHost list size is " + hostList.size());

                List<Participants> FK_list = participantsDao.queryBuilder().list();
                L.i(TAG, "participants list size is " + FK_list.size());

                msg.what = response.code();
                msg.arg1 = CodeType.GETWORKOUT;
                msg.obj = workOutList;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<List<GetWorkResponse>> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void getWorkOut(final Handler handler, String auth, int type, int tag, boolean isOver) {
        Call<List<GetWorkResponse>> call = connection.workout(auth, type, tag,isOver);
        call.enqueue(new Callback<List<GetWorkResponse>>() {
            @Override
            public void onResponse(Call<List<GetWorkResponse>> call, Response<List<GetWorkResponse>> response) {

                Message msg = new Message();

                myWorkOutDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getWorkOutDao();

                myHostDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getHostDao();

                participantsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getParticipantsDao();

                List<GetWorkResponse> list = response.body();

                if (list != null && list.size() >0) {
                    List<Participants> participantsList = new ArrayList<Participants>();

                    for (int i = 0; i < list.size(); i++) {
                        if(TextUtils.isEmpty(list.get(i).getParticipants()))continue;
                        participantsList = gson.fromJson(list.get(i).getParticipants(), new TypeToken<List<Participants>>() {}.getType());
                        for (int j = 0; j < participantsList.size(); j++) {
                            participantsList.get(j).setWorkout_id(list.get(i).getId());
                        }
                        WorkOut myWorkOut = new WorkOut(list.get(i).getId(),
                                list.get(i).getTag(),
                                list.get(i).getPermission(),
                                list.get(i).getName(),
                                list.get(i).getStart_date(),
                                list.get(i).getEnd_date(),
                                list.get(i).getStart_location(),
                                list.get(i).getEnd_location(),
                                list.get(i).getRoute(),
                                list.get(i).getNote(),
                                list.get(i).getCustom_cal(),
                                list.get(i).getCustom_distance(),
                                list.get(i).getCustom_speed(),
                                list.get(i).getCustom_duration(),
                                list.get(i).getHost().getHost_id());

                        myWorkOutDao.insertOrReplace(myWorkOut);

                        Host myHost = new Host(list.get(i).getHost().getHost_id(),
                                list.get(i).getHost().getUsername(),
                                list.get(i).getHost().getImage_url(),
                                list.get(i).getHost().getEmail());
                        myHostDao.insertOrReplace(myHost);

                        participantsDao.deleteInTx(participantsDao.queryBuilder().where(ParticipantsDao
                                .Properties.Workout_id.eq(list.get(i).getId())).list());

                        participantsDao.insertOrReplaceInTx(participantsList);
                    }
                }

                List<WorkOut> workOutList = myWorkOutDao.queryBuilder().where(WorkOutDao.Properties.Tag.eq(4)).list();
                L.i(TAG, "MyWorkOut list size is " + workOutList.size());

                List<Host> hostList = myHostDao.queryBuilder().list();
                L.i(TAG, "MyHost list size is " + hostList.size());

                List<Participants> FK_list = participantsDao.queryBuilder().list();
                L.i(TAG, "participants list size is " + FK_list.size());


                msg.what = response.code();
                msg.arg1 = CodeType.GETWORKOUT;
                msg.obj = workOutList;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<List<GetWorkResponse>> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void refresh(final Handler handler, RefreshRequest refreshRequest) {
        Call<Token> call = connection.refresh(refreshRequest);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.REFRESH;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void getRecords(final Handler handler, String accessToken, String startDate, String endDate/*, Integer size*/) {
        Call<List<Records>> call = connection.getRecords(accessToken, startDate, endDate/*, size*/);
        call.enqueue(new Callback<List<Records>>() {
            @Override
            public void onResponse(Call<List<Records>> call, Response<List<Records>> response) {
                myRecordsDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getRecordsDao();

                List<Records> list = response.body();
                for (int i = 0; i < list.size(); i++) {
                    Records records = list.get(i);
                }

                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.RECORDS;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<List<Records>> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void signFacebook(final Handler handler, String accessToken) {
        Call<SignResponse> call = connection.signFacebook(accessToken);
        call.enqueue(new Callback<SignResponse>() {
            @Override
            public void onResponse(Call<SignResponse> call, Response<SignResponse> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.SIGNFACEBOOK;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<SignResponse> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void addWorkout(final Handler handler, String accessToken, JsonObject request) {
        Call<AddWorkResponse> call = connection.addWorkout(accessToken,request);
        call.enqueue(new Callback<AddWorkResponse>() {
            @Override
            public void onResponse(Call<AddWorkResponse> call, Response<AddWorkResponse> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.ADDWORKOUT;
                msg.obj = response.body();
                handler.sendMessage(msg);
                postEvent(response);
            }

            @Override
            public void onFailure(Call<AddWorkResponse> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void deleteWorkout(final Handler handler, String accessToken, String id) {
        Call<ResponseBody> call = connection.deleteWorkout(accessToken,id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.DELETEWORKOUT;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void updateWorkout(final Handler handler, String accessToken, String id, JsonObject request) {
        Call<ResponseBody> call = connection.updateWorkout(accessToken,id,request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.UPDATEWORKOUT;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void getUsers(final Handler handler, String accessToken, String keyword, String gpsLocation) {
        Call<List<GetUserResponse>> call = connection.getUsers(accessToken,keyword,gpsLocation);
        call.enqueue(new Callback<List<GetUserResponse>>() {
            @Override
            public void onResponse(Call<List<GetUserResponse>> call, Response<List<GetUserResponse>> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.GETUSER;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<List<GetUserResponse>> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    public void getWorkDetail(final Handler handler,String accessToken,String work_id){
        Call<WorkDetailResponse> call = connection.getWorkDetail(accessToken,work_id);
        call.enqueue(new Callback<WorkDetailResponse>() {
            @Override
            public void onResponse(Call<WorkDetailResponse> call, Response<WorkDetailResponse> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.WORKDETAIL;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<WorkDetailResponse> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }

    @Override
    public void updateInvitation(final Handler handler, String accessToken, String work_id, InvitationRequest invitationRequest) {
        Call<ResponseBody> call = connection.updateInvitation(accessToken,work_id,invitationRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Message msg = new Message();
                msg.what = response.code();
                msg.arg1 = CodeType.UPDATEINVITATION;
                msg.obj = response.body();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendFailMessage(handler, t);
            }
        });
    }
}