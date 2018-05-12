package com.wistron.swpc.android.WiTMJ.http.request;

/**
 * Created by wh1604025 on 2016/6/22.
 */
public class NavigationModeRequest {
    private String type;
    private String status;
    private String nav_road;
    private String nav_distance;
    private String nav_direction;

    public NavigationModeRequest(String type, String status, String nav_road, String nav_distance, String nav_direction) {
        this.type = type;
        this.status = status;
        this.nav_road = nav_road;
        this.nav_distance = nav_distance;
        this.nav_direction = nav_direction;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNav_road() {
        return nav_road;
    }

    public void setNav_road(String nav_road) {
        this.nav_road = nav_road;
    }

    public String getNav_distance() {
        return nav_distance;
    }

    public void setNav_distance(String nav_distance) {
        this.nav_distance = nav_distance;
    }

    public String getNav_direction() {
        return nav_direction;
    }

    public void setNav_direction(String nav_direction) {
        this.nav_direction = nav_direction;
    }
}
