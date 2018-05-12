package com.example;

import com.google.gson.JsonObject;

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
import retrofit2.http.Url;

/**
 * Created by WH1106013 on 2016/5/16.
 */
public interface TmjService {
    public static final String baseurl = "https://witmj.azurewebsites.net/api/";//https://witmj.azurewebsites.net/api/

   /* @Headers({"X-ms-blob-type:BlockBlob",
            "Content-Type:binary/octet-stream"})
    @PUT("{id}")
    Call<ResponseBody> uploadPic(@Header("X-ZUMO-AUTH") String accessToken,@Path("id") String id,
                                 @Query("se") String se,@Body RequestBody file);*/

    @Headers({"X-ms-blob-type:BlockBlob"})
    @PUT("{id}?se=2016-06-20T08%3A37%3A41Z&sp=w&sv=2015-04-05&sr=b&sig=HvgePwamFgRd%2Fhe3W338Mcw0f4snlV1qn2%2BfA7NkVo4%3D")
    Call<ResponseBody> uploadPic(@Header("X-ZUMO-AUTH") String accessToken,@Path("id") String id
           // ,@Query(value = "se", encoded = true) String se
            ,@Body RequestBody file);//

    @GET("users/profile/")
    Call<Profile> getProfile(@Header("X-ZUMO-AUTH") String auth);
    @POST("refresh")
    Call<Token> refreshToken(@Body  JsonObject refreshtoken);
    @GET
    Call<ResponseBody> downloadPic(@Url String fileUrl);

    @POST("login")
    Call<Token> login(@Header("Authorization") String auth);


    @POST("logout")
    Call<Token> logout(@Header("X-ZUMO-AUTH") String accessToken, @Body RequestBody refreshToken);

    @POST("logout")
    Call<Token> logout2(@Header("X-ZUMO-AUTH") String accessToken, @Body JsonObject refreshToken);

    @Headers({"Content-Type:application/json"})
    @GET("users/profile/records/")
    Call<ResponseBody> getRecords(@Header("X-ZUMO-AUTH") String accessToken,
                                   @Query("start_date") String startDate,
                                   @Query("end_date") String endDate/* ,
                                   @Query("size") String size*/);

    @Headers({"Content-Type: application/json"})
    @PUT("users/profile/records/{id}")
    Call<Message> putRecord(@Header("X-ZUMO-AUTH") String accessToken, @Path("id") String recordId, @Body JsonObject record);

    @DELETE("workout/{id}")
    Call<ResponseBody> deleteWorkout(@Header("X-ZUMO-AUTH") String accessToken,
                                     @Path("id") String id);
    @PUT("users/profile/gps/")
    Call<ResponseBody> putGps(@Header("X-ZUMO-AUTH") String accessToken, @Body JsonObject gpsLocation);
}
