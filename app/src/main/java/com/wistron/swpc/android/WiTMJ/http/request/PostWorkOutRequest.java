package com.wistron.swpc.android.WiTMJ.http.request;

import java.util.List;

/**
 * Created by WH1604025 on 2016/6/3.
 */
public class PostWorkOutRequest {
    private int tag;

    private int permission;

    private String name;

    private String start_date;

    private String end_date;

    private String start_location;

    private String end_location;

    private Route route;

    private List<Participants> participants ;

    private String note;

    public void setTag(int tag){
        this.tag = tag;
    }
    public int getTag(){
        return this.tag;
    }
    public void setPermission(int permission){
        this.permission = permission;
    }
    public int getPermission(){
        return this.permission;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setStart_date(String start_date){
        this.start_date = start_date;
    }
    public String getStart_date(){
        return this.start_date;
    }
    public void setEnd_date(String end_date){
        this.end_date = end_date;
    }
    public String getEnd_date(){
        return this.end_date;
    }
    public void setStart_location(String start_location){
        this.start_location = start_location;
    }
    public String getStart_location(){
        return this.start_location;
    }
    public void setEnd_location(String end_location){
        this.end_location = end_location;
    }
    public String getEnd_location(){
        return this.end_location;
    }
    public void setRoute(Route route){
        this.route = route;
    }
    public Route getRoute(){
        return this.route;
    }
    public void setParticipants(List<Participants> participants){
        this.participants = participants;
    }
    public List<Participants> getParticipants(){
        return this.participants;
    }
    public void setNote(String note){
        this.note = note;
    }
    public String getNote(){
        return this.note;
    }
}
