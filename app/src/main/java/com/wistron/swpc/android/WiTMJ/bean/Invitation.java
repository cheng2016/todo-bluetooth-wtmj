package com.wistron.swpc.android.WiTMJ.bean;

import java.io.Serializable;

/**
 * Created by WH1603043 on 16/6/2016.
 */
public class Invitation implements Serializable{
    public String name;
    public String hostname;
    public String record_id;

    public Invitation(String name, String hostname, String record_id) {
        this.name = name;
        this.hostname = hostname;
        this.record_id = record_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }
}
