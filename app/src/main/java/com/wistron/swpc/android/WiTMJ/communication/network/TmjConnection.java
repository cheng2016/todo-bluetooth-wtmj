package com.wistron.swpc.android.WiTMJ.communication.network;


import com.google.gson.JsonObject;
import com.wistron.swpc.android.WiTMJ.bean.Invitation;
import com.wistron.swpc.android.WiTMJ.bean.Message;
import com.wistron.swpc.android.WiTMJ.bean.Profile;
import com.wistron.swpc.android.WiTMJ.bean.Records;
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

import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by WH1106013 on 2016/5/16.
 */
public interface TmjConnection {
    public static final String baseurl = "https://witmj.azurewebsites.net/api/";
    @POST("login")
    Call<Token> login(@Header("Authorization") String auth);

    @POST("logout/")
    Call<ResponseBody> logout(@Header("X-ZUMO-AUTH") String accessToken,
                              @Body LogoutRequest refreshToken);

    @GET("users/")
    Call<List<GetUserResponse>> getNearbyUsers(@Header("X-ZUMO-AUTH") String accessToken,
                                       @Query("keyword") String keyword,
                                       @Query("gps_location") String gpsLocation);
    @POST("workout/")
    Call<AddWorkResponse> postWorkOut(@Header("X-ZUMO-AUTH") String accessToken,
                                      @Body JsonObject workout_detail);



    @GET("workout_simple/")
    Call<List<Invitation>> getWorkOutSimple(@Header("X-ZUMO-AUTH") String accessToken);

    @Headers({"X-ms-blob-type:BlockBlob"})
    @PUT("{id}")
    Call<ResponseBody> uploadPic(@Header("X-ZUMO-AUTH") String accessToken,@Path("id") String id,
                                @Query("se") String se,@Body RequestBody file);


    @PUT("users/profile/records/{id}")
    Call<Message> putRecord(@Header("X-ZUMO-AUTH") String accessToken, @Path("id") String recordId, @Body JsonObject record);

    @GET("users/profile/")
    Call<Profile> getProfile(@Header("X-ZUMO-AUTH") String auth);

    @PUT("/users/profile/records/")
    Call<Records>putRecords(@Header("X-ZUMO-AUTH") String accessToken,
                            @Query("id") String id,
                            @Query("user_records") String user_records);

    @GET("json")
    Call<ResponseBody> distancematrix(@Query("origins") String startPlace,
                                      @Query("destinations") String endPlace,
                                      @Query("mode") String mode ,
                                      @Query("language") String language,
                                      @Query("key") String key);

    @PUT("users/profile/")
    Call<ResponseBody> putProfile(@Header("X-ZUMO-AUTH") String accessToken, @Body JsonObject userProfile);

    @PUT("users/profile/gps/")
    Call<ResponseBody> putGps(@Header("X-ZUMO-AUTH") String accessToken, @Body JsonObject gpsLocation);

    @GET("workout/")
    Call<List<GetWorkResponse>> workout(@Header("X-ZUMO-AUTH") String accessToken,
                                        @Query("type") int type,
                                        @Query("tag") int tag,
                                        @Query("isOver") boolean isOver,
                                        @Query("size") int size);

    @GET("workout/")
    Call<List<GetWorkResponse>> workout(@Header("X-ZUMO-AUTH") String accessToken,
                                        @Query("type") int type,
                                        @Query("tag") int tag,
                                        @Query("isOver") boolean isOver);

    @POST("refresh/")
    Call<Token> refresh(@Body RefreshRequest refreshRequest);

    @GET("users/profile/records")
    Call<List<Records>> getRecords(@Header("X-ZUMO-AUTH") String accessToken,
                            @Query("start_date") String startDate,
                            @Query("end_date") String endDate/* ,
                            @Query("size") Integer size*/);

    @POST("workout/")
    Call<AddWorkResponse> addWorkout(@Header("X-ZUMO-AUTH") String accessToken,
                                     @Body JsonObject request);

    @DELETE("workout/{id}")
    Call<ResponseBody> deleteWorkout(@Header("X-ZUMO-AUTH") String accessToken,
                                     @Path("id") String id);

    @PUT("workout/{id}")
    Call<ResponseBody> updateWorkout(@Header("X-ZUMO-AUTH") String accessToken,
                                     @Path("id") String id,
                                     @Body JsonObject request);

    @PUT("users/profile/records/{id}/invitation/")
    Call<ResponseBody> putInvitation(@Header("X-ZUMO-AUTH") String accessToken,
                                     @Path("id") String recordId,
                                     @Body JsonObject invitation);

    @PUT("users/profile/records/{id}/invitation/")
    Call<ResponseBody> updateInvitation(@Header("X-ZUMO-AUTH") String accessToken,
                                     @Path("id") String recordId,
                                     @Body InvitationRequest invitationRequest);

    /**
     * facebook sign
     */
    @POST("sign/facebook/")
    Call<SignResponse> signFacebook(@Header("access_token") String accessToken);

    @GET("users/")
    Call<List<GetUserResponse>> getUsers(@Header("X-ZUMO-AUTH") String accessToken,
                                   @Query("keyword") String keyword,
                                   @Query("gps_location") String gpsLocation);

    @GET("workout/{id}")
    Call<WorkDetailResponse> getWorkDetail(@Header("X-ZUMO-AUTH") String accessToken,
                                           @Path("id") String work_id);


}
