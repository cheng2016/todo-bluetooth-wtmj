package com.wistron.swpc.android.WiTMJ.http.response;

/**
 * Created by WH1604025 on 2016/6/12.
 */
public class GetUserResponse {
    private String id;
    private String username;
    private String image_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
