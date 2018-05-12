package com.wistron.swpc.android.WiTMJ.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "NAVIGATION".
 */
public class Navigation {

    private Long id;
    /** Not-null value. */
    private String userId;
    private Integer tag;
    private String startRoute;
    private String endRoute;

    public Navigation() {
    }

    public Navigation(Long id) {
        this.id = id;
    }

    public Navigation(Long id, String userId, Integer tag, String startRoute, String endRoute) {
        this.id = id;
        this.userId = userId;
        this.tag = tag;
        this.startRoute = startRoute;
        this.endRoute = endRoute;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUserId() {
        return userId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public String getStartRoute() {
        return startRoute;
    }

    public void setStartRoute(String startRoute) {
        this.startRoute = startRoute;
    }

    public String getEndRoute() {
        return endRoute;
    }

    public void setEndRoute(String endRoute) {
        this.endRoute = endRoute;
    }

}
