package com.wistron.swpc.android.WiTMJ.http.response;

import java.util.List;

/**
 * Created by WH1604025 on 2016/5/23.
 */
public class GroupResult {
    private String number;
    private String name;
    private String content;

    public GroupResult() {
    }

    public GroupResult(String number, String name, String content) {
        this.number = number;
        this.name = name;
        this.content = content;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
