package com.wistron.swpc.android.WiTMJ.http.request;

import java.util.List;

/**
 * Created by WH1604025 on 2016/6/7.
 */
public class WorkOutRequest {
    private Integer tag;
    private Integer permission;
    private String name;
    private String start_date;
    private String end_date;
    private String start_location;
    private String end_location;

    private String custom_cal;
    private String custom_distance;
    private String custom_speed;
    private String custom_duration;

    private String route;

    private List<ParticipantsRequest> participants;

    private String note;

    public WorkOutRequest() {
    }

    public WorkOutRequest(Integer tag, Integer permission, String name, String start_date, String end_date, String start_location, String end_location, String custom_cal, String custom_distance, String custom_speed, String custom_duration, String route, List<ParticipantsRequest> participants, String note) {
        this.tag = tag;
        this.permission = permission;
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.start_location = start_location;
        this.end_location = end_location;
        this.custom_cal = custom_cal;
        this.custom_distance = custom_distance;
        this.custom_speed = custom_speed;
        this.custom_duration = custom_duration;
        this.route = route;
        this.participants = participants;
        this.note = note;
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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public List<ParticipantsRequest> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantsRequest> participants) {
        this.participants = participants;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static class ParticipantsRequest {
        private String user_id;
        private String username;

        public ParticipantsRequest(String user_id, String username) {
            this.user_id = user_id;
            this.username = username;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
