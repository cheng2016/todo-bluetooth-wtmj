package com.wistron.swpc.android.WiTMJ.communication.notification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.TmjApplication;
import com.wistron.swpc.android.WiTMJ.bean.Profile;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.http.HttpImpl;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
/*
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;*/
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;


public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;

    private GoogleCloudMessaging gcm;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static Boolean isVisible = false;
    private static final String TAG = "MainActivity";

    private String HubEndpoint = null;
    private String HubSasKeyName = null;
    private String HubSasKeyValue = null;

    public static final String INTENT_BUNDLE_PROFILE_USER_ID = "intent_bundle_profile_user_id";
    String url = "https://witmjdev.azurewebsites.net/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        mainActivity = this;
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, MyHandler.class);

        if (checkPlayServices()) {
            new RegisterUserByUserId().execute();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.");
                ToastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void ToastNotify(final String notificationMessage)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
                TextView helloText = (TextView) findViewById(R.id.text_hello);
                helloText.setText(notificationMessage);
            }
        });
    }

    class RegisterUserByUserId extends AsyncTask<String, Void, String> {

        String user_id = null;

        @Override
        protected String doInBackground(String... params) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String regID = sharedPreferences.getString("registrationID", null);

            //if notificaiton already register, ignore
            if(regID!=null)
                return "Executed";
/*
            try {
                //get access_token
               HttpClient clientPost = new DefaultHttpClient();
                HttpPost requestPost = new HttpPost(url + "/login");
                requestPost.setHeader(new BasicHeader("Content-Type", "application/json"));
                requestPost.setHeader(new BasicHeader("Authorization", "Basic dG1qMDAzOjEyMzQ1Ng=="));

                HttpResponse response = clientPost.execute(requestPost);
                String json_string = EntityUtils.toString(response.getEntity());

                JSONObject JSONObjectFromLogin = new JSONObject(json_string);
                String access_token = JSONObjectFromLogin.getString("access_token");

                //get profile & receive push notificaiton
                HttpClient clientGet = new DefaultHttpClient();
                HttpGet requestGet = new HttpGet(url + "/users/profile");
                requestGet.setHeader(new BasicHeader("Content-Type", "application/json"));
                requestGet.setHeader(new BasicHeader("X-ZUMO-AUTH", access_token));

                HttpResponse responseGet = clientGet.execute(requestGet);
                String json_string_get = EntityUtils.toString(responseGet.getEntity());

                JSONObject JSONObjectFromUserProfile = new JSONObject(json_string_get);

                user_id = TmjApplication.getInstance().getUserid();
                Log.v("Leo", user_id);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            return "Executed";
        }


        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, " Registering with Notification Hubs");
            String token = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, "");
            TmjConnection connection = TmjClient.create();
            Call<Profile> call = connection.getProfile(token);//"Basic "+enc
            call.enqueue(new Callback<Profile>() {
                @Override
                public void onResponse(Call<Profile> call, retrofit2.Response<Profile> response) {

                    user_id = response.body().getProfile_id();

                    Log.i(TAG, " user_id esponse.body().getProfile_id()" + user_id);
                    // Start IntentService to register this application with GCM.
                    Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
                    intent.putExtra(INTENT_BUNDLE_PROFILE_USER_ID, user_id);
                    startService(intent);
                }

                @Override
                public void onFailure(Call<Profile> call, Throwable t) {
                   // sendFailMessage(handler, t);
                }
            });

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
