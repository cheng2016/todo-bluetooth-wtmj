package com.wistron.swpc.android.WiTMJ.http.request;

/**
 * Created by wh1604025 on 2016/6/22.
 */
public class TrainModeRequest {
    private String type;
    private String status;
    private String duration;
    private String calories;
    private String calories_over;
    private String distance;
    private String distance_over;
    private String speed;
    private String speed_over;
    private String nav_road;
    private String nav_distance;
    private String nav_direction;

    public TrainModeRequest(String type, String status, String duration, String calories,
                            String calories_over, String distance, String distance_over, String speed, String speed_over, String nav_road, String nav_distance, String nav_direction) {
        this.type = type;
        this.status = status;
        this.duration = duration;
        this.calories = calories;
        this.calories_over = calories_over;
        this.distance = distance;
        this.distance_over = distance_over;
        this.speed = speed;
        this.speed_over = speed_over;
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

    public String getCalories_over() {
        return calories_over;
    }

    public void setCalories_over(String calories_over) {
        this.calories_over = calories_over;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance_over() {
        return distance_over;
    }

    public void setDistance_over(String distance_over) {
        this.distance_over = distance_over;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getSpeed_over() {
        return speed_over;
    }

    public void setSpeed_over(String speed_over) {
        this.speed_over = speed_over;
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
