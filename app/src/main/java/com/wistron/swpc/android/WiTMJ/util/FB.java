package com.wistron.swpc.android.WiTMJ.util;

import android.graphics.Bitmap;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

/**
 * Created by WH1604041 on 2016/6/15.
 */
public class FB {
    public static void sharePhotoToFacebook(Bitmap image) {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("My Result!")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                T.showShort("你分享的内容已成功分享到Facebook");
            }

            @Override
            public void onCancel() {
                T.showShort("你分享的内容已取消");
            }

            @Override
            public void onError(FacebookException error) {
                T.showShort("你分享的内容未成功");
            }
        });
    }
}
