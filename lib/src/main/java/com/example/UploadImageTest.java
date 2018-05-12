package com.example;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by WH1106013 on 2016/6/20.
 */
public class UploadImageTest {
   static String image_sas,image_url,userid;
   // static String userid ="1c1c66fa-4be1-4b79-b256-8e06b7773cb4";
    static  String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxYzFjNjZmYS00YmUxLTRiNzktYjI1Ni04ZTA2Yjc3NzNjYjQiLCJpYXQiOjE0NjY0MTMzNTIsImV4cCI6MTQ2NjQxNTE1MiwiYXVkIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8iLCJpc3MiOiJodHRwczovL3dpdG1qLmF6dXJld2Vic2l0ZXMubmV0LyJ9.ZQjB-pcqFfzRvj8hDsLmfb8Y7q5T-rDmOi-k4Kep1-M";
   // static  String

    public static void main(String[] args) throws Exception {
        /*TmjService   connection = TmjFactory.create();
        Call<Profile> call = connection.getProfile(accessToken);//"Basic "+enc
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, retrofit2.Response<Profile> response) {
               // System.out.println("getImage_url：" + response.body().getImage_url());
              //  System.out.println("getProfile id：" + response.body().getProfile_id());
              //  System.out.println("getProfile：" + response.code() + "\t" + response.message());
                if(!response.isSuccessful()){
                    System.out.println("Failure  " + response.code() + "\t" + response.message());
                    return;
                }
                image_sas= response.body().getImage_sas();
                userid = response.body().getProfile_id();
                image_url = response.body().getImage_url();

                TmjService connection1 = TmjFactory.createImageUpload();
                File file = new File("D:\\android\\projects\\tmj3\\witmjclientaxi-new\\source\\WiTMJ\\app\\src\\main\\res\\drawable-mdpi\\fb.png");
                System.out.println("url   " + file.getPath());
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                String imagesas = image_sas.substring(image_sas.indexOf("=") + 1, image_sas.length());
                System.out.println("imageurl ******** " +  image_url +"?"+image_sas);
                uploadProfileImage(

                        image_url +"?"+image_sas
                        ,file);
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                System.out.println("getProfile：" + "onFailure  " + t.getMessage());
            }
        });*/


    }

    public static Boolean uploadProfileImage(String serverURL, File file) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            OkHttpClient client = builder.build();
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
                }
            });

            return true;
        } catch (Exception ex) {
            // Handle the error
            System.out.println(ex.getMessage());
        }
        return false;
    }
}
