package com.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.ResponseBody;

/**
 * Created by WH1106013 on 2016/5/16.
 */
public class ServerTest {

    public static void main(String[] args) throws Exception {

        /*OkHttpClient httpClient = new OkHttpClient.Builder()
                // 授权证书
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        // System.out.println("Authenticating for response: " + response);
                        // System.out.println("Challenges: " + response.challenges());
                        String credential = Credentials.basic("zhouever", "z741852963");//WiTMJ:WiTMJ0000
                        // HTTP授权的授权证书  Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
                        return response
                                .request()
                                .newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                })
                .build();*/
       /* OkHttpClient httpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, "");
        Request build = new Request.Builder()
                .url("https://witmj.azurewebsites.net/api/login/")//https://witmj.azurewebsites.net/api/login/ https://api.github.com/users/octocat/repos
                .addHeader("Authorization", "Basic V2lUTUo6V2lUTUowMDAw")
                .addHeader("Content-Type", "application/json").post(body)
                .build();
        Response response = httpClient.newCall(build).execute();
        System.out.println(response);
        System.out.println(response.body().string());*/
        TmjService service = TmjFactory.create();
       /* Call<Token> call = service.login("Basic V2lUTUo6V2lUTUowMDAw");
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, retrofit2.Response<Token> response) {
                    System.out.println("Access_token  "+response.body().getAccess_token());
                    System.out.println("Refresh_token  "+response.body().getRefresh_token());
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                System.out.println("Failure  "+t.getMessage());
            }
        });*/

      /* String rf = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI6MWMxYzY2ZmEtNGJlMS00Yjc5LWIyNTYtOGUwNmI3NzczY2I0IiwiaWF0IjoxNDY0NTc3NTg2LCJleHAiOjE0NjQ2NjM5ODYsImF1ZCI6Imh0dHBzOi8vd2l0bWouYXp1cmV3ZWJzaXRlcy5uZXQvIiwiaXNzIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8ifQ.dAjjAvQioQzm5czfm1SeeDAPieooK3aWYst0ryCurlE";
        String rf2 = "{\"refresh_token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI6MWMxYzY2ZmEtNGJlMS00Yjc5LWIyNTYtOGUwNmI3NzczY2I0IiwiaWF0IjoxNDY0NTc3NTg2LCJleHAiOjE0NjQ2NjM5ODYsImF1ZCI6Imh0dHBzOi8vd2l0bWouYXp1cmV3ZWJzaXRlcy5uZXQvIiwiaXNzIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8ifQ.dAjjAvQioQzm5czfm1SeeDAPieooK3aWYst0ryCurlE\"}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),rf2);
       String j= "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI6MWMxYzY2ZmEtNGJlMS00Yjc5LWIyNTYtOGUwNmI3NzczY2I0IiwiaWF0IjoxNDY0NTc3NTg2LCJleHAiOjE0NjQ2NjM5ODYsImF1ZCI6Imh0dHBzOi8vd2l0bWouYXp1cmV3ZWJzaXRlcy5uZXQvIiwiaXNzIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8ifQ.dAjjAvQioQzm5czfm1SeeDAPieooK3aWYst0ryCurlE";
        jsonObject.addProperty("refresh_token",j);
        Call<Token> call2 = service.logout2("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxYzFjNjZmYS00YmUxLTRiNzktYjI1Ni04ZTA2Yjc3NzNjYjQiLCJpYXQiOjE0NjQ1Nzc1ODYsImV4cCI6MTQ2NDU3OTM4NiwiYXVkIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8iLCJpc3MiOiJodHRwczovL3dpdG1qLmF6dXJld2Vic2l0ZXMubmV0LyJ9.x2-gzG2QQ2pj7cRXPMAKgb-vm4fQgTG9MLUr4zbgt-0"
                ,jsonObject);

        call2.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, retrofit2.Response<Token> response) {
               // System.out.println("Access_token  "+response.body().getAccess_token());
              //  System.out.println("Refresh_token  "+response.body().getRefresh_token());
                System.out.println(response.code()+response.message());
                System.out.println(response.code()+""+response.errorBody());
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                System.out.println("Failure  "+t.getMessage());
            }
        });*/

        // call.cancel();



/*

        final Records records = new Records();
        records.setTag(1);
        records.setCalories(100d);
        records.setAvg_speed(100d);
        records.setDuration(100d);
        records.setDistance(100d);
        records.setRoutes("12343");
        records.setStart_time(new Date());
        records.setEnd_time(new Date());
        records.setRank(0);
        records.setRecord_id("d51c6257-f98c-4c82-9e69-3062845ded5e");

       // long recordId = recordsDao.insert(records);
        Gson g2 = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        System.out.println("record json : "+g2.toJson(records)) ;
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(g2.toJson(records)).getAsJsonObject();
        String act ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI3NDA2NWM2Zi00ZjVmLTRhYWYtOGZmMS00Y2Q2YjQwMjYzOWEiLCJpYXQiOjE0NjUyOTIzMTEsImV4cCI6MTQ2NTI5NTkxMSwiYXVkIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8iLCJpc3MiOiJodHRwczovL3dpdG1qLmF6dXJld2Vic2l0ZXMubmV0LyJ9.YKHW9QU6LjmbfuOYkOgk05S7UURJQCXlnZDbdWV5CSw";
        Call<Message> call = service.putRecord(act,records.getRecord_id() ,o);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, retrofit2.Response<Message> response) {
                if(response.isSuccessful()){
                    System.out.println(response.body().getMessage());
                }else{
                    System.out.println(response.body().getMessage());
                }
            }
            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
*/
        //     new DaoGenerator().generateAll(schema,"E:\\git\\WiTMJ_new3\\witmjclientaxi-new\\source\\WiTMJ\\app\\src\\main\\java");

   /*     File file = new File("D:\\android\\projects\\tmj3\\witmjclientaxi-new\\source\\WiTMJ\\app\\src\\main\\res\\mipmap-hdpi\\ic_launcher.png");
        RequestBody requestBody1 =
                RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("binary/octet-stream"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);
        //RequestBody file = RequestBody.create(MediaType.parse("image"), "D:\\android\\projects\\tmj3\\witmjclientaxi-new\\source\\WiTMJ\\app\\src\\main\\res\\mipmap-hdpi\\ic_launcher.png");
        // finally, execute the request
       // https://witmj.blob.core.windows.net/userpicture/1c1c66fa-4be1-4b79-b256-8e06b7773cb4?se=2016-06-08T08%3A53%3A29Z&sp=w&sv=2015-04-05&sr=b&sig=YATg4ZO5rQbzwkdcdXnEHfD492aOKc9twrsymqYYSqQ%3D
//        Call<ResponseBody> call = service.uploadPic("1c1c66fa-4be1-4b79-b256-8e06b7773cb4"
//                ,"2016-06-08T09%3A17%3A07Z&sp=w&sv=2015-04-05&sr=b&sig=wKDkCrJa2HImMy3Bli0Q5RnHruNi7mdj%2Fb2AUZHZ0zg%3D", body);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call,
//                                   retrofit2.Response<ResponseBody> response) {
//                if(response.isSuccessful()){
//                    System.out.println(" "+response.code()+" "+response.message());
//                }else{
//                    System.out.println(" "+response.code()+" "+response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                System.out.println(t.getMessage());
//            }
//        });

        String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxZGMzMWZmNi00MTlkLTRkNjgtOWJmYi00YjIyMTY5NDIyY2MiLCJpYXQiOjE0NjU4MDIwMzMsImV4cCI6MTQ2NTgwNTYzMywiYXVkIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8iLCJpc3MiOiJodHRwczovL3dpdG1qLmF6dXJld2Vic2l0ZXMubmV0LyJ9.D0HcGCphyO3nLQRA-7YgeqE7CDzbgP7zU9pWIayzHEM";
        String id = "9e8aabe3-1f18-4054-958f-3f11d27f6769";
        Call<ResponseBody> deleteCall = service.deleteWorkout(accessToken,id);
        deleteCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    System.out.print("successful");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });



       // upload image
        File file = new File("D:\\android\\projects\\tmj3\\witmjclientaxi-new\\source\\WiTMJ\\app\\src\\main\\res\\mipmap-hdpi\\contact_default.png");

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        Call<ResponseBody> call = service.uploadPic("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI3NDA2NWM2Zi00ZjVmLTRhYWYtOGZmMS00Y2Q2YjQwMjYzOWEiLCJpYXQiOjE0NjU3ODk3NDMsImV4cCI6MTQ2NTc5MzM0MywiYXVkIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8iLCJpc3MiOiJodHRwczovL3dpdG1qLmF6dXJld2Vic2l0ZXMubmV0LyJ9.9-WvyQ1Vqczhxr3Kp7FLThHKeebf9ebEdbmRzikAoYE"
                ,"1c1c66fa-4be1-4b79-b256-8e06b7773cb4" , requestFile);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    System.out.print("successful");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        //download image

      /*  Call<ResponseBody> call = service.getRecords("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxYzFjNjZmYS00YmUxLTRiNzktYjI1Ni04ZTA2Yjc3NzNjYjQiLCJpYXQiOjE0NjU3ODYyOTQsImV4cCI6MTQ2NTc4OTg5NCwiYXVkIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8iLCJpc3MiOiJodHRwczovL3dpdG1qLmF6dXJld2Vic2l0ZXMubmV0LyJ9.66k6nqzeo45TOANmv12jqLI-AgshGVYAvs-nmzcSvS8"
                ,"2016-01-01", "2016-07-01","1");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    System.out.println(" "+response.code()+" "+response.message());
                }else{
                    System.out.println(" "+response.code()+" "+response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        }); */
/*        Gson g2 = new Gson(); //new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JsonParser parser = new JsonParser();
        final Records records = new Records();
        records.setTag(3);
        records.setCalories(11.0);
        records.setAvg_speed(11.0);
        records.setDuration(11.0);
        records.setDistance(11.0);
        records.setRoutes("");
        records.setRank(0);
        records.setStart_time(new Date());
        records.setEnd_time(new Date());
        JsonObject jo = parser.parse( g2.toJson(records)).getAsJsonObject();
        Call<Message> call =service.putRecord("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxYzFjNjZmYS00YmUxLTRiNzktYjI1Ni04ZTA2Yjc3NzNjYjQiLCJpYXQiOjE0NjU3ODc5MTEsImV4cCI6MTQ2NTc5MTUxMSwiYXVkIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8iLCJpc3MiOiJodHRwczovL3dpdG1qLmF6dXJld2Vic2l0ZXMubmV0LyJ9.OfSSExdgnsyepqlXfn3vzz0K5qbKN-J4nQy5r-G5hiw","5c79fca6-ae7b-4cef-9dff-f98c661067c2",jo);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call,
                                   retrofit2.Response<Message> response) {
                if(response.isSuccessful()){
                    System.out.println(" "+response.code()+" "+response.message());
                }else{
                    System.out.println(" "+response.code()+" "+response.message());
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });*/

       /* final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("longitude", "114.420820");
        jsonObject.addProperty("latitude", "30.453560");
        jsonObject.addProperty("workout_id", "");
        String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxYzFjNjZmYS00YmUxLTRiNzktYjI1Ni04ZTA2Yjc3NzNjYjQiLCJpYXQiOjE0NjU5NTc2NDQsImV4cCI6MTQ2NTk2MTI0NCwiYXVkIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8iLCJpc3MiOiJodHRwczovL3dpdG1qLmF6dXJld2Vic2l0ZXMubmV0LyJ9.iuJGJQ0CRArQVx3xJOL92Nx78FyjSo96GEeuwGZfULs";
        Call<ResponseBody> call = service.putGps(accessToken, jsonObject);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
//                    System.out.println("putGPSLocation：" + response.code() + "" + response.message());
//                    System.out.println(jsonObject.toString());
                } else {
//                    System.out.println("Failure  " + response.code() + "" + response.message());
//                    System.out.println("Failure  " + response.code() + "" + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                System.out.println("onFailure  " + t.getMessage());
//                System.out.println("onFailure  " + t.getMessage());
            }
        });*/

       /* java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String s = format1.format(new Date());
        String sd = new Date().toString();
        java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date sd3 =  format2.parse(s);

        System.out.println("sd3-----"+format2.format(sd3));
        System.out.println("s-----"+s);
*/

        //getCurDaysInweek(-10);
        /*String dtc = "2016-06-21T01:48:50.596Z";
        SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = readDate.parse(dtc);

        SimpleDateFormat writeDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String s1 = writeDate.format(date);
<<<<<<< Updated upstream
        System.out.println("s1-----"+s1);

        Date d = getStrToDate("2016-06-21T01:48:50.596Z");
        System.out.println("d-----"+d);

    }

    public static Date getStrToDate(String str){
        Date date =null ;
        try {
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = readDate.parse(str);
            System.out.println("date=="+date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
=======
        System.out.println("s1-----"+s1);*/
        TmjService service2 = TmjFactory.createAutoConnect();
        String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxYzFjNjZmYS00YmUxLTRiNzktYjI1Ni04ZTA2Yjc3NzNjYjQiLCJpYXQiOjE0NjcxODQyMjYsImV4cCI6MTQ2NzE5MTQyNiwiYXVkIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8iLCJpc3MiOiJodHRwczovL3dpdG1qLmF6dXJld2Vic2l0ZXMubmV0LyJ9.1qjrsaEgZS78EDYJux-Epa-aQJ5ezd0G42h_FIf5xl4";
        String refreshToken =   "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI6MWMxYzY2ZmEtNGJlMS00Yjc5LWIyNTYtOGUwNmI3NzczY2I0IiwiaWF0IjoxNDY3MTg0MjI2LCJleHAiOjE0NjcxOTg2MjYsImF1ZCI6Imh0dHBzOi8vd2l0bWouYXp1cmV3ZWJzaXRlcy5uZXQvIiwiaXNzIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8ifQ.5nod2-cDj8opeZ42IqoT5bNrRZ0mxZ5A4F7Jiiaa4_Q";
        Call<Profile> call = service2.getProfile(accessToken);
        call.execute();
>>>>>>> Stashed changes
    }

    private static boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File( "D:\\android\\projects\\tmj3\\Icon.png");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    System.out.println("file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
