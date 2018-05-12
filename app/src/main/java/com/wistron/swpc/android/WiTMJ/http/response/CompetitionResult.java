package com.wistron.swpc.android.WiTMJ.http.response;

/**
 * Created by WH1604025 on 2016/5/23.
 */
public class CompetitionResult {
    private String date;
    private String time;
    private String content;

    public CompetitionResult(String date, String time, String content) {
        this.date = date;
        this.time = time;
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
