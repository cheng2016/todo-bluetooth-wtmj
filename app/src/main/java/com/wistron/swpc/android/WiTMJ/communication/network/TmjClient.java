package com.wistron.swpc.android.WiTMJ.communication.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by WH1106013 on 2016/5/17.
 */
public class TmjClient {
    public static TmjConnection create() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(15, TimeUnit.SECONDS);
        builder.connectTimeout(15, TimeUnit.SECONDS);
       /* builder.addInterceptor(new Interceptor() {
            @Override public okhttp3.Response intercept(Chain chain) throws IOException {
                RequestBody body = chain.request().body();
                System.out.println(bodyToString(body));
                Request request = chain.request().newBuilder()
                        // .addHeader("Authorization", "Basic V2lUTUo6V2lUTUowMDAw")
                       // .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();
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
        // add your other interceptors …
        // add logging as last interceptor
        builder.addInterceptor(logging);// <-- this is the important line!

        Retrofit retrofit =
                new Retrofit.Builder().baseUrl(TmjConnection.baseurl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(builder.build())
                       // .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();

        return retrofit.create(TmjConnection.class);
    }

    public static TmjConnection create(String baseUrl) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(15, TimeUnit.SECONDS);
        builder.connectTimeout(15, TimeUnit.SECONDS);

        Retrofit retrofit =
                new Retrofit.Builder().baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(builder.build())
                        // .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();
        return retrofit.create(TmjConnection.class);
    }

    public static TmjConnection createImageUpload() {

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        OkHttpClient client = builder.build();

        Retrofit retrofit =
                new Retrofit.Builder().baseUrl("https://witmj.blob.core.windows.net/userpicture/")
                        .client(client)
                        .build();

        return retrofit.create(TmjConnection.class);
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
