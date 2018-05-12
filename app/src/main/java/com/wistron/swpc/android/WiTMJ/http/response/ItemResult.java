package com.wistron.swpc.android.WiTMJ.http.response;

/**
 * Created by WH1604025 on 2016/5/23.
 */
public class ItemResult {
    private String speed;
    private String distance;
    private String duration;
    private String calories;

    public ItemResult(String speed, String distance, String duration, String calories) {
        this.speed = speed;
        this.distance = distance;
        this.duration = duration;
        this.calories = calories;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}
