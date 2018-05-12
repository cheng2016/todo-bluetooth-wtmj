package com.wistron.swpc.android.WiTMJ.personalinfo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wistron.swpc.android.WiTMJ.ApplicationHolder;
import com.wistron.swpc.android.WiTMJ.CommonUtil;
import com.wistron.swpc.android.WiTMJ.LogUtils;
import com.wistron.swpc.android.WiTMJ.LoginActivity;
import com.wistron.swpc.android.WiTMJ.R;
import com.wistron.swpc.android.WiTMJ.base.BaseFragment;
import com.wistron.swpc.android.WiTMJ.bean.Profile;
import com.wistron.swpc.android.WiTMJ.bean.WorkOut;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjClient;
import com.wistron.swpc.android.WiTMJ.communication.network.TmjConnection;
import com.wistron.swpc.android.WiTMJ.dao.ProfileDao;
import com.wistron.swpc.android.WiTMJ.http.CodeType;
import com.wistron.swpc.android.WiTMJ.listener.onCalendarSelectListener;
import com.wistron.swpc.android.WiTMJ.util.L;
import com.wistron.swpc.android.WiTMJ.util.PreferenceConstants;
import com.wistron.swpc.android.WiTMJ.util.PreferencesUtil;
import com.wistron.swpc.android.WiTMJ.util.T;
import com.wistron.swpc.android.WiTMJ.util.URL;
import com.wistron.swpc.android.WiTMJ.widget.CalendarDialog;
import com.wistron.swpc.android.WiTMJ.widget.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/5/4.
 */
public class PersonalFragment extends BaseFragment  {
    private final static String TAG = "PersonalFragment";
    private View mPersonalInfoFragment;
    private TextView tv_edit;
    private TextView tv_ok;
    private TextView tv_cancel;
    private LinearLayout ll_gender;
    private EditText et_username;
    private EditText et_email;
    private TextView tv_dateOfBirth;
    private EditText et_height;
    private EditText et_weight;
    private ListView mGenderListView;
    private TextView tv_gender1;
    private CircleImageView iv_pic;
  //  private ImageView iv_pic2;
    private ImageView iv_camera;
    private ImageView iv_gender;
    private PopupWindow mPopupWindow;
    private List<String> mGenderStr = new ArrayList<String>();
    private Bitmap roundBitmap = null;
    private Bitmap bitmap;
    private ProfileDao mPersonalProfileDao;
    private List<Profile> profiles;
    private JsonObject jsonObject;//userProfile json
    private String image_url;
    private Uri uri;
    private Profile profile;
    private String accessToken;
    private String userName;
    private String email;
    private String birthday;
    private int height;
    private int weight;
    private int gender;
    private String imageAbsolutePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPersonalInfoFragment = getActivity().getLayoutInflater().inflate(R.layout.fragment_personalinfo, null);
        initView();
        initEvent();
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup p = (ViewGroup) mPersonalInfoFragment.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mPersonalInfoFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            return;

    }

    @Override
    public void initView() {
        tv_edit = (TextView) mPersonalInfoFragment.findViewById(R.id.tv_edit);
        tv_ok = (TextView) mPersonalInfoFragment.findViewById(R.id.tv_ok);
        tv_cancel = (TextView) mPersonalInfoFragment.findViewById(R.id.tv_cancel);
        et_username = (EditText) mPersonalInfoFragment.findViewById(R.id.et_username);
        et_email = (EditText) mPersonalInfoFragment.findViewById(R.id.et_email);
        tv_dateOfBirth = (TextView) mPersonalInfoFragment.findViewById(R.id.tv_dateOfbirth);
        tv_gender1 = (TextView) mPersonalInfoFragment.findViewById(R.id.tv_gender1);
        ll_gender = (LinearLayout) mPersonalInfoFragment.findViewById(R.id.ll_gender);
        et_height = (EditText) mPersonalInfoFragment.findViewById(R.id.et_height);
        et_weight = (EditText) mPersonalInfoFragment.findViewById(R.id.et_weight);
        iv_gender = (ImageView) mPersonalInfoFragment.findViewById(R.id.iv_gender);
        iv_pic = (CircleImageView) mPersonalInfoFragment.findViewById(R.id.iv_pic);
       // iv_pic2 = (CircleImageView) mPersonalInfoFragment.findViewById(R.id.iv_pic2);
        iv_camera = (ImageView) mPersonalInfoFragment.findViewById(R.id.iv_camera);
        mGenderStr.add("Male");
        mGenderStr.add("Female");
        mPersonalProfileDao = ApplicationHolder.getApplication().getDbHelper().getDaoSession().getProfileDao();
        profiles = mPersonalProfileDao.queryBuilder().list();
        if (profiles != null && profiles.size() > 0) {
            profile = profiles.get(0);
            image_url = profile.getImage_url();
            userName = profile.getUsername();
            email = profile.getEmail();
            birthday = profile.getBirthday();
            height = profile.getHeight();
            weight = profile.getWeight();
            gender = profile.getGender();
            if (gender == 0) {
                tv_gender1.setText("Male");
            } else if (gender == 1) {
                tv_gender1.setText("Female");
            }
            et_username.setText(userName);
            et_email.setText(email);
            tv_dateOfBirth.setText(birthday);
            et_height.setText(height + "");
            et_weight.setText(weight + "");
           
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(image_url, iv_pic);
        }
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgress();
            switch (msg.what){
                case CodeType.UPLOAD_IMG_SUCCESS:
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    ImageView userPhotoIv = (ImageView) getActivity().findViewById(R.id.userPhotoIv);
                    imageLoader.displayImage("file://"+imageAbsolutePath, userPhotoIv);
                    LogUtils.d("", "file.getPath() " +imageAbsolutePath);
                    break;
                case CodeType.TOKENERROR:
                    showCustomDialog("Erro Signing In", "Your account has expired or other places to log in, please re verify user identity.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().startActivity(new Intent().setClass(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        }
                    }, false);
                    break;
                default:
                    T.showShort("" + msg.what);
                    break;
            }
        }
    };
    @Override
    public void initEvent() {
        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);

            }
        });

        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_gender.setBackground(getResources().getDrawable(R.drawable.editing_background1));
                ll_gender.setEnabled(true);
                tv_gender1.setTextColor(getResources().getColor(R.color.font));
                iv_gender.setVisibility(View.VISIBLE);
                tv_edit.setVisibility(View.GONE);
                tv_ok.setVisibility(View.VISIBLE);
                tv_cancel.setVisibility(View.VISIBLE);
                et_username.setBackground(getResources().getDrawable(R.drawable.editing_background));
                et_username.setTextColor(getResources().getColor(R.color.font));
                et_email.setEnabled(true);
                et_email.setBackground(getResources().getDrawable(R.drawable.editing_background));
                et_email.setTextColor(getResources().getColor(R.color.font));
                tv_dateOfBirth.setEnabled(true);
                tv_dateOfBirth.setBackground(getResources().getDrawable(R.drawable.editing_background));
                tv_dateOfBirth.setTextColor(getResources().getColor(R.color.font));
                et_height.setEnabled(true);
                et_height.setBackground(getResources().getDrawable(R.drawable.editing_background));
                et_height.setTextColor(getResources().getColor(R.color.font));
                et_weight.setEnabled(true);
                et_weight.setBackground(getResources().getDrawable(R.drawable.editing_background));
                et_weight.setTextColor(getResources().getColor(R.color.font));
                tv_dateOfBirth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CalendarDialog(PersonalFragment.this).builder().setListener(new onCalendarSelectListener() {
                            @Override
                            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                                int dMonth = month + 1;
                                tv_dateOfBirth.setText(year + "/" + dMonth + "/" + dayOfMonth);
                            }
                        });
                    }
                });
                ll_gender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.ll_gender:
                                showPopWindow(0, 0);
                                break;
                            default:
                                break;
                        }
                    }
                });
                mPopupWindow = new PopupWindow(getActivity());
                mGenderListView = new ListView(getActivity());
                com.wistron.swpc.android.WiTMJ.personalinfo.GenderAdapter popAdapter = new com.wistron.swpc.android.WiTMJ.personalinfo.GenderAdapter(getActivity(), mGenderStr);
                mGenderListView.setAdapter(popAdapter);
                mGenderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                tv_gender1.setText("Male");
                                tv_gender1.setTextColor(getResources().getColor(R.color.font));
                                break;
                            case 1:
                                tv_gender1.setText("Female");
                                tv_gender1.setTextColor(getResources().getColor(R.color.font));
                                break;
                        }
                        if (mPopupWindow != null) {
                            mPopupWindow.dismiss();
                        }
                    }


                });
            }

        });

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_camera.setVisibility(View.GONE);
                String username = et_username.getText().toString();
                et_username.setText(username);
                et_username.setBackground(getResources().getDrawable(R.drawable.edit_background));
                String email = et_email.getText().toString();
                et_email.setText(email);
                et_email.setEnabled(false);
                et_email.setBackground(getResources().getDrawable(R.drawable.edit_background));
                String birthday = tv_dateOfBirth.getText().toString();
                tv_dateOfBirth.setText(birthday);
                tv_dateOfBirth.setEnabled(false);
                tv_dateOfBirth.setBackground(getResources().getDrawable(R.drawable.edit_background));
                String height = et_height.getText().toString();
                et_height.setText(height);
                et_height.setEnabled(false);
                et_height.setBackground(getResources().getDrawable(R.drawable.edit_background));
                String weight = et_weight.getText().toString();
                et_weight.setText(weight);
                et_weight.setEnabled(false);
                et_weight.setBackground(getResources().getDrawable(R.drawable.edit_background));
                int gender = 0;
                if (tv_gender1.getText().toString().equals("Male")) {
                    gender = 0;
                } else if (tv_gender1.getText().toString().equals("Female")) {
                    gender = 1;
                }
                tv_gender1.setText(tv_gender1.getText().toString());
                tv_gender1.setTextColor(getResources().getColor(R.color.font));
                ll_gender.setEnabled(false);
                ll_gender.setBackground(getResources().getDrawable(R.drawable.edit_background));
                iv_gender.setVisibility(View.GONE);

               // userPhotoIv.setImageBitmap(roundBitmap);
                jsonObject = new JsonObject();
                jsonObject.addProperty("username", username);
                jsonObject.addProperty("email", email);
                jsonObject.addProperty("weight", weight);
                jsonObject.addProperty("height", height);
                jsonObject.addProperty("gender", gender);
                jsonObject.addProperty("birthday", birthday);
                TmjConnection connection = TmjClient.create();
                accessToken = PreferencesUtil.getPrefString(getActivity(), PreferenceConstants.ACCESS_TOKEN, "");
                String imageAbsolutePath = URL.getImageAbsolutePath(getActivity(), uri);
                if(imageAbsolutePath!=null){
                    CommonUtil.uploadProfileImage(imageAbsolutePath,accessToken,handler);

                }

                Call<ResponseBody> call = connection.putProfile(accessToken, jsonObject);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            L.i(TAG, "putUserProfile：" + response.code() + "\t" + response.message());
                            L.i(TAG, jsonObject.toString());
                            T.showShort("edit profile success");
                            tv_edit.setVisibility(View.VISIBLE);
                            tv_cancel.setVisibility(View.GONE);
                            tv_ok.setVisibility(View.GONE);
                        } else {
                            L.i(TAG, "Failure  " + response.code() + "\t" + response.message());
                            T.showShort("Failure  " + response.code() + "\t" + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        L.i(TAG, "onFailure  " + t.getMessage());
                        T.showShort("onFailure  " + t.getMessage());
                    }
                });
               /* String image_sas = profile.getImage_sas();
                L.i(TAG, "image_sas " + image_sas);
                String imageUrl_imageSas = image_url + "?" + image_sas + "/";
                L.i(TAG, "imageUrl_imageSas " + imageUrl_imageSas);
                TmjConnection connection1 = TmjClient.createImageUpload();
                String imageAbsolutePath = URL.getImageAbsolutePath(getActivity(), uri);
                File file = new File(imageAbsolutePath);
                L.i(TAG, "url   " + file.getPath());
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                String imagesas = image_sas.substring(image_sas.indexOf("=") + 1, image_sas.length());
                L.i(TAG, "imagesas ******** " + imagesas);
                Call<ResponseBody> responseBodyCall = connection1.uploadPic(accessToken, profile.getProfile_id(), imagesas, requestFile);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            L.i(TAG, "uploadPic：" + response.code() + "\t" + response.message());
                            T.showShort("Success  " + response.code() + "\t" + response.message());
                        } else {
                            L.i(TAG, "Failure  " + response.code() + "\t" + response.message());
                            T.showShort("Failure  " + response.code() + "\t" + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        L.i(TAG, "onFailure  " + t.getMessage());
                        T.showShort("onFailure  " + t.getMessage());
                    }
                });*/
            }
        });


        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()//加载头像
                        .showImageForEmptyUri(R.drawable.camera2) // resource or drawable
                        .showImageOnFail(R.drawable.camera2).build();

                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(image_url, iv_pic, options);
//                iv_pic.setImageDrawable(getResources().getDrawable(R.drawable.contact_default));
                iv_camera.setVisibility(View.GONE);
                et_username.setBackground(getResources().getDrawable(R.drawable.edit_background));
                et_username.setText(userName);
                et_email.setEnabled(false);
                et_email.setText(email);
                et_email.setBackground(getResources().getDrawable(R.drawable.edit_background));
                tv_dateOfBirth.setEnabled(false);
                tv_dateOfBirth.setText(birthday);
                tv_dateOfBirth.setBackground(getResources().getDrawable(R.drawable.edit_background));
                et_height.setEnabled(false);
                et_height.setText(height + "");
                et_height.setBackground(getResources().getDrawable(R.drawable.edit_background));
                et_weight.setEnabled(false);
                et_weight.setText(weight + "");
                et_weight.setBackground(getResources().getDrawable(R.drawable.edit_background));
                iv_gender.setVisibility(View.GONE);
                tv_ok.setVisibility(View.GONE);
                tv_cancel.setVisibility(View.GONE);
                tv_edit.setVisibility(View.VISIBLE);
                ll_gender.setBackground(getResources().getDrawable(R.drawable.edit_background));
                if (gender == 0) {
                    tv_gender1.setText("Male");
                } else if (gender == 1) {
                    tv_gender1.setText("Female");
                }
                tv_gender1.setTextColor(getActivity().getResources().getColor(R.color.black));
                ll_gender.setEnabled(false);
               /* ImageView userPhotoIv = (ImageView) getActivity().findViewById(R.id.userPhotoIv);
                userPhotoIv.setImageDrawable(getResources().getDrawable(R.mipmap.contact_default));*/
            }


        });

    }

    @Override
    public void initData() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            uri = data.getData();

             imageAbsolutePath = CommonUtil.getRealFilePath(this.getActivity(),uri);//URL.getImageAbsolutePath(getActivity(), uri);
            Log.e("imageAbsolutePath ","imageAbsolutePath "+ imageAbsolutePath);
           // iv_pic.setImageURI(null);
           // iv_pic.setImageURI(uri);
            iv_camera.setVisibility(View.VISIBLE);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage("file://"+imageAbsolutePath, iv_pic);
            Log.e("file path"," CommonUtil.getRealFilePath "+ CommonUtil.getRealFilePath(this.getActivity(),uri));
            /*ContentResolver cr = getActivity().getContentResolver();
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
               // roundBitmap = toRoundBitmap(bitmap);
                ImageView imageView = (ImageView) mPersonalInfoFragment.findViewById(R.id.iv_pic);
                imageView.setImageBitmap(bitmap);
                iv_camera.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                Log.i("Exception", e.getMessage(), e);
            }*/
        }
    }

  /*  public Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }*/

    private void showPopWindow(int xoff, int yoff) {
        if (mPopupWindow == null)
            return;
        int[] location = new int[2];
        if (ll_gender != null)
            ll_gender.getLocationOnScreen(location);
        mPopupWindow.setAnimationStyle(R.style.AppTheme);
        mPopupWindow.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
        int width = ll_gender.getMeasuredWidth();
        mPopupWindow.setWidth(width);
        Resources resources = getActivity().getResources();
        Drawable d = resources.getDrawable(R.color.setting_bg);
        mPopupWindow.setBackgroundDrawable(d);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setContentView(mGenderListView);
        mPopupWindow.showAsDropDown(ll_gender, xoff, yoff);
    }
}
