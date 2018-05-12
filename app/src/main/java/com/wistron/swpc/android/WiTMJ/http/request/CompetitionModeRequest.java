package com.wistron.swpc.android.WiTMJ.http.request;

/**
 * Created by wh1604025 on 2016/6/22.
 */
public class CompetitionModeRequest {
    private String type;
    private String status;
    private String rank;
    private String number;
    private String lead;
    private String behind;
    private String nav_road;
    private String nav_distance;

    public CompetitionModeRequest(String type, String status, String rank, String number,
                                  String lead, String behind, String nav_road, String nav_distance) {
        this.type = type;
        this.status = status;
        this.rank = rank;
        this.number = number;
        this.lead = lead;
        this.behind = behind;
        this.nav_road = nav_road;
        this.nav_distance = nav_distance;
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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public String getBehind() {
        return behind;
    }

    public void setBehind(String behind) {
        this.behind = behind;
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
}
