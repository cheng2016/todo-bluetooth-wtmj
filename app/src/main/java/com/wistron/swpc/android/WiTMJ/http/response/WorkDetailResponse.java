package com.wistron.swpc.android.WiTMJ.http.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wh1604025 on 2016/6/15.
 */
public class WorkDetailResponse {
    private String id;
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
    private List<Participants> participants;
    private String record_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<Participants> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participants> participants) {
        this.participants = participants;
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public class Participants {
        private String user_id;
        private String username;
        private Integer invention;
        private String start_time;
        private String end_time;
        private String duration;
        private String distance;
        private String calories;
        private String avg_speed;
        private String rank;

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

        public Integer getInvention() {
            return invention;
        }

        public void setInvention(Integer invention) {
            this.invention = invention;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
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

        public String getAvg_speed() {
            return avg_speed;
        }

        public void setAvg_speed(String avg_speed) {
            this.avg_speed = avg_speed;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }
    }
}