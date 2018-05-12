package com.wistron.swpc.android.WiTMJ.bean;

/**
 * Created by WH1603043 on 22/6/2016.
 */
public class ReportData {
    private Float distance = 0F;
    private Float speed = 0F;
    private Float cal = 0F;
    private String year = "";
    private String month = "";
    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getCal() {
        return cal;
    }

    public void setCal(Float cal) {
        this.cal = cal;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
