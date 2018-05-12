package com.wistron.swpc.android.WiTMJ.http.response;

import java.util.List;

/**
 * Created by WH1604025 on 2016/5/25.
 */
public class Records {
    private String id;
    private Integer tag;
    private Integer permission;
    private String host;
    private String name;
    private String start_date;
    private String end_date;
    private String start_location;
    private String end_location;
    private List<WorkDetailResponse.Participants> list;

    private Integer status;

    public Records(String name, Integer tag, String start_date, String end_date) {
        this.name = name;
        this.tag = tag;
        this.start_date = start_date;
        this.end_date = end_date;
    }

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<WorkDetailResponse.Participants> getList() {
        return list;
    }

    public void setList(List<WorkDetailResponse.Participants> list) {
        this.list = list;
    }
}
