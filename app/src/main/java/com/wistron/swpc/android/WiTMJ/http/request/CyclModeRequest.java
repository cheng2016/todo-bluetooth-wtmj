package com.wistron.swpc.android.WiTMJ.http.request;

/**
 * Created by wh1604025 on 2016/6/22.
 */
public class CyclModeRequest {
    private String type;
    private String status;
    private String speed;
    private String distance;
    private String calories;
    private String time;

    public CyclModeRequest() {
    }

    public CyclModeRequest(String type, String status, String speed, String distance, String calories, String time) {
        this.type = type;
        this.status = status;
        this.speed = speed;
        this.distance = distance;
        this.calories = calories;
        this.time = time;
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

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
