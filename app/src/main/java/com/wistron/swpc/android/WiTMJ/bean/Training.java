package com.wistron.swpc.android.WiTMJ.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "TRAINING".
 */
public class Training {

    private Long id;
    /** Not-null value. */
    private String userId;
    private Integer tag;
    private String startRoute;
    private String endRoute;
    private java.util.Date duration;
    private Long speed;
    private Float distance;
    private Long calories;

    public Training() {
    }

    public Training(Long id) {
        this.id = id;
    }

    public Training(Long id, String userId, Integer tag, String startRoute, String endRoute, java.util.Date duration, Long speed, Float distance, Long calories) {
        this.id = id;
        this.userId = userId;
        this.tag = tag;
        this.startRoute = startRoute;
        this.endRoute = endRoute;
        this.duration = duration;
        this.speed = speed;
        this.distance = distance;
        this.calories = calories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUserId() {
        return userId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public String getStartRoute() {
        return startRoute;
    }

    public void setStartRoute(String startRoute) {
        this.startRoute = startRoute;
    }

    public String getEndRoute() {
        return endRoute;
    }

    public void setEndRoute(String endRoute) {
        this.endRoute = endRoute;
    }

    public java.util.Date getDuration() {
        return duration;
    }

    public void setDuration(java.util.Date duration) {
        this.duration = duration;
    }

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Long getCalories() {
        return calories;
    }

    public void setCalories(Long calories) {
        this.calories = calories;
    }

}
