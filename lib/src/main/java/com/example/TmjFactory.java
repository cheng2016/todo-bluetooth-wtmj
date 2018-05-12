package com.example;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
/*
import retrofit2.RxJavaCallAdapterFactory;
*/
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by WH1106013 on 2016/5/17.
 */
public class TmjFactory {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static TmjService create(/*Context context*/) {

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(5, TimeUnit.SECONDS);
/*
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(interceptor);
        }*/
      /*  builder.addInterceptor(new Interceptor() {
            @Override public okhttp3.Response intercept(Chain chain) throws IOException {
                RequestBody body = chain.request().body();
                System.out.println(bodyToString(body));

                Request request = chain.request().newBuilder()
                       // .addHeader("Authorization", "Basic V2lUTUo6V2lUTUowMDAw")
                       //.addHeader("Content-Type", "binary/octet-stream") //Content-Type:binary/octet-stream
                       // .post(body)
                        .build();
                System.out.println("headers = "+request.headers());;
                System.out.println("url = "+request.url());
                return chain.proceed(request);
            }
        });
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                System.out.println("Response " +response.body().string());
                return response;
            }
        });*/
      //  builder.addInterceptor(new UnauthorisedInterceptor(context));
        OkHttpClient client = builder.build();

        Retrofit retrofit =
                new Retrofit.Builder().baseUrl(TmjService.baseurl)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                       // .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();

        return retrofit.create(TmjService.class);
    }
    public static TmjService createAutoConnect() {

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
/*

        builder.addInterceptor(new Interceptor() {
            @Override public okhttp3.Response intercept(Chain chain) throws IOException {
                RequestBody body = chain.request().body();
                System.out.println(bodyToString(body));

                Request request = chain.request().newBuilder()
                        // .addHeader("Authorization", "Basic V2lUTUo6V2lUTUowMDAw")
                        //.addHeader("Content-Type", "binary/octet-stream") //Content-Type:binary/octet-stream
                        // .post(body)
                        .build();
                System.out.println("headers = "+request.headers());;
                System.out.println("url = "+request.url());
                return chain.proceed(request);
            }
        });*/
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                System.out.println("Response " +response.body().string());
                String refreshToken =   "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI6MWMxYzY2ZmEtNGJlMS00Yjc5LWIyNTYtOGUwNmI3NzczY2I0IiwiaWF0IjoxNDY3MTg0MjI2LCJleHAiOjE0NjcxOTg2MjYsImF1ZCI6Imh0dHBzOi8vd2l0bWouYXp1cmV3ZWJzaXRlcy5uZXQvIiwiaXNzIjoiaHR0cHM6Ly93aXRtai5henVyZXdlYnNpdGVzLm5ldC8ifQ.5nod2-cDj8opeZ42IqoT5bNrRZ0mxZ5A4F7Jiiaa4_Q";
                if(response.code()==401){
                    TmjService service = create();
                    final JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("refresh_token", refreshToken);
                    retrofit2.Response<Token> tokenCall =service.refreshToken(jsonObject)
                            .execute();
                    String Access_token = tokenCall.body().getAccess_token();
                    System.out.println("new Access_token"+Access_token);

                    Request request = chain.request().newBuilder()
                            .removeHeader("X-ZUMO-AUTH")
                            .addHeader("X-ZUMO-AUTH",Access_token)
                            .build();

                    chain.proceed(request);
                }

                return response;
            }
        });
        OkHttpClient client = builder.build();
        Retrofit retrofit =
                new Retrofit.Builder().baseUrl(TmjService.baseurl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

        return retrofit.create(TmjService.class);
    }
    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

}
