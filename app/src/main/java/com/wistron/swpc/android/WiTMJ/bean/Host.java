package com.wistron.swpc.android.WiTMJ.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.wistron.swpc.android.WiTMJ.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.wistron.swpc.android.WiTMJ.dao.HostDao;
import com.wistron.swpc.android.WiTMJ.dao.WorkOutDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "HOST".
 */
public class Host {

    private String username;
    private String image_url;
    private String email;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient HostDao myDao;

    private List<WorkOut> workout;

    // KEEP FIELDS - put your custom fields here
    @SerializedName("id")
    private String host_id;
    // KEEP FIELDS END

    public Host() {
    }

    public Host(String host_id) {
        this.host_id = host_id;
    }

    public Host(String host_id, String username, String image_url, String email) {
        this.host_id = host_id;
        this.username = username;
        this.image_url = image_url;
        this.email = email;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getHostDao() : null;
    }

    public String getHost_id() {
        return host_id;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<WorkOut> getWorkout() {
        if (workout == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            WorkOutDao targetDao = daoSession.getWorkOutDao();
            List<WorkOut> workoutNew = targetDao._queryHost_Workout(host_id);
            synchronized (this) {
                if(workout == null) {
                    workout = workoutNew;
                }
            }
        }
        return workout;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetWorkout() {
        workout = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
