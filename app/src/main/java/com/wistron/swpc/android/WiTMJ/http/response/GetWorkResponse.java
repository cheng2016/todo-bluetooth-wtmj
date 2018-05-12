package com.wistron.swpc.android.WiTMJ.http.response;

import com.google.gson.annotations.SerializedName;
import com.wistron.swpc.android.WiTMJ.bean.Host;

/**
 * Created by WH1604025 on 2016/6/6.
 */
public class GetWorkResponse {
    @SerializedName("id")
    private String workout_id;
    private Integer tag;
    private Integer permission;
    private String name;
    private String start_date;
    private String end_date;
    private String start_location;
    private String end_location;

    private String route;

    private String participants;

    private String note;

    private String custom_cal;
    private String custom_distance;
    private String custom_speed;
    private String custom_duration;

    private Host host;

    public String getId() {
        return workout_id;
    }

    public void setId(String id) {
        this.workout_id = id;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStart_location() {
        return start_location;
    }

    public void setStart_location(String start_location) {
        this.start_location = start_location;
    }

    public String getEnd_location() {
        return end_location;
    }

    public void setEnd_location(String end_location) {
        this.end_location = end_location;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCustom_cal() {
        return custom_cal;
    }

    public void setCustom_cal(String custom_cal) {
        this.custom_cal = custom_cal;
    }

    public String getCustom_distance() {
        return custom_distance;
    }

    public void setCustom_distance(String custom_distance) {
        this.custom_distance = custom_distance;
    }

    public String getCustom_speed() {
        return custom_speed;
    }

    public void setCustom_speed(String custom_speed) {
        this.custom_speed = custom_speed;
    }

    public String getCustom_duration() {
        return custom_duration;
    }

    public void setCustom_duration(String custom_duration) {
        this.custom_duration = custom_duration;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }
}
