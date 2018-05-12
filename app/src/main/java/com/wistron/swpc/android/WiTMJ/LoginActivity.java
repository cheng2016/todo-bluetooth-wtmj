package com.wistron.swpc.android.WiTMJ;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.wistron.swpc.android.WiTMJ.base.BaseActivity;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.http.HttpImpl;
import com.wistron.swpc.android.WiTMJ.http.response.SignResponse;
import com.wistron.swpc.android.WiTMJ.http.response.Token;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.NetUtil;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by WH1604025 on 2016/5/3.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private EditText accountEdit, passwordEdit;
    private Button loginBtn, cleanBtn;
    private TextView titleTv;
    private ActionBar actionBar;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private RelativeLayout fbLoginBtn;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private ProfileTracker profileTracker;
    private Profile profile;
    private ImageView logoImg;
    private int count = 0;
    private TmjConnection connection;
    private String account,psw;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = TmjClient.create();
        initFaceBook();
        setContentView(R.layout.activity_login);

        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.app_actionbar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);

        initView();
        initEvent();
        initData();

    }

    public void initFaceBook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                L.i(TAG, "onCurrentAccessTokenChanged");
                accessToken = currentAccessToken.getCurrentAccessToken();
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        profile = Profile.getCurrentProfile();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                // App code
                L.i(TAG, "onCurrentProfileChanged  " + currentProfile.getName() + "\t" + currentProfile.getLastName());
                TmjApplication.getInstance().setUserName(currentProfile.getName());
                TmjApplication.getInstance().setUserid(currentProfile.getId());
                account = currentProfile.getName();
            }
        };

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                T.showShort("onSuccess");
                L.i(TAG, "onSuccess");
//                showProgress("sign Facebook...");
//                HttpImpl.getInstance(LoginActivity.this).signFacebook(handler,accessToken.toString());

            }

            @Override
            public void onCancel() {
                T.showShort("onCancel");
                L.i(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                T.showShort("onError");
                error.printStackTrace();
            }
        });
    }


    @Override
    public void initView() {
        titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.titleTv);
        titleTv.setText("WiTMJ");
        actionBar.getCustomView().findViewById(R.id.backBtn).setVisibility(View.GONE);

        logoImg = (ImageView) findViewById(R.id.logoImg);
        logoImg.setOnClickListener(this);

        fbLoginBtn = (RelativeLayout) findViewById(R.id.fbLoginBtn);
        fbLoginBtn.setOnClickListener(this);

        accountEdit = (EditText) findViewById(R.id.accountEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        cleanBtn = (Button) findViewById(R.id.cleanBtn);
        loginBtn.setOnClickListener(this);
        cleanBtn.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void initEvent() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.wistron.swpc.android.WiTMJ",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG + " KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initData() {
        account = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.ACCOUNT, "");
        psw = PreferencesUtil.getPrefString(getApplicationContext(), PreferenceConstants.PASSWORD, "");
        if(TextUtils.isEmpty(account) && TextUtils.isEmpty(psw)){
            PreferencesUtil.setPrefBoolean(getApplicationContext(),PreferenceConstants.USER_FIRST_INIT,true);
        }
        setViewText(accountEdit,account);
        setViewText(passwordEdit,psw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                if(!TextUtils.isEmpty(account) && accountEdit.getText().toString().equals(account)){
                    PreferencesUtil.setPrefBoolean(getApplicationContext(),PreferenceConstants.USER_FIRST_INIT,false);
                }else{
                    PreferencesUtil.setPrefBoolean(getApplicationContext(),PreferenceConstants.USER_FIRST_INIT,true);
                }
                account = accountEdit.getText().toString();
                psw = passwordEdit.getText().toString();
                if (TextUtils.isEmpty(account)) {
                    T.showShort("Account can no be null！");
                    break;
                }
                if (TextUtils.isEmpty(psw)) {
                    T.showShort("Password can no be null！");
                    break;
                }

                final String accout_psw = account + ":" + psw;
                byte[] encode = Base64.encode(accout_psw.getBytes(), Base64.DEFAULT);//base64 加密
                String enc = new String(encode);
                L.i(TAG, "Authorization " + "Basic " + enc.trim());
                showProgress("login...");
                HttpImpl.getInstance(LoginActivity.this).login(handler,"Basic " + enc.trim());
                break;
            case R.id.cleanBtn:
                accountEdit.setText("");
                passwordEdit.setText("");
                PreferencesUtil.setPrefString(getApplicationContext(), PreferenceConstants.ACCOUNT, "");
                PreferencesUtil.setPrefString(getApplicationContext(), PreferenceConstants.PASSWORD, "");
                break;
            case R.id.fbLoginBtn:
                if(NetUtil.getNetworkState(getApplicationContext()) != NetUtil.NETWORN_NONE){
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                }else{
                    T.showLong(getApplicationContext(),"请检查网络连接！");
                }
                break;
            case R.id.logoImg:
                count++;
                if (count == 6) {
                    showProgress("", "");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            count = 0;
                            hideProgress();
                            startActivity(new Intent().setClass(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }, 1000);
                }
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgress();
            switch (msg.what){
                case CodeType.SUCCESS:
                    if(msg.arg1 == CodeType.LOGIN){
                        Token token = (Token) msg.obj;
                        PreferencesUtil.setPrefString(getApplicationContext(), PreferenceConstants.ACCOUNT, account);
                        PreferencesUtil.setPrefString(getApplicationContext(), PreferenceConstants.PASSWORD, psw);
                        PreferencesUtil.setPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, token.getAccess_token());
                        PreferencesUtil.setPrefString(getApplicationContext(), PreferenceConstants.REFRESH_TOKEN, token.getRefresh_token());
                        PreferencesUtil.setPrefBoolean(getApplicationContext(), PreferenceConstants.AUTOLOGIN, true);// 是否开启默认登录
                        startActivity(new Intent().setClass(LoginActivity.this, MainActivity.class));
                        finish();
                    }else if(msg.arg1 ==CodeType.SIGNFACEBOOK){
                        SignResponse response = (SignResponse) msg.obj;

//                      PreferencesUtil.setPrefString(getApplicationContext(), PreferenceConstants.ACCOUNT, account);

                        PreferencesUtil.setPrefString(getApplicationContext(), PreferenceConstants.ACCESS_TOKEN, response.getAccess_token());
                        PreferencesUtil.setPrefString(getApplicationContext(), PreferenceConstants.REFRESH_TOKEN, "");
                        PreferencesUtil.setPrefBoolean(getApplicationContext(), PreferenceConstants.AUTOLOGIN, true);// 是否开启默认登录
                        startActivity(new Intent().setClass(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    break;
                case CodeType.ERRORRQUEST:
                    T.showShort("Incorrect username or password");
                    break;
            }
        }
    };
}