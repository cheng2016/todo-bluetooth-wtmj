package com.wistron.swpc.android.WiTMJ.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.wistron.swpc.android.WiTMJ.bean.Push;
import com.wistron.swpc.android.WiTMJ.bean.DbWorkOut;
import com.wistron.swpc.android.WiTMJ.bean.Profile;
import com.wistron.swpc.android.WiTMJ.bean.WorkOut;
import com.wistron.swpc.android.WiTMJ.bean.Records;
import com.wistron.swpc.android.WiTMJ.bean.Host;
import com.wistron.swpc.android.WiTMJ.bean.Participants;

import com.wistron.swpc.android.WiTMJ.dao.PushDao;
import com.wistron.swpc.android.WiTMJ.dao.DbWorkOutDao;
import com.wistron.swpc.android.WiTMJ.dao.ProfileDao;
import com.wistron.swpc.android.WiTMJ.dao.WorkOutDao;
import com.wistron.swpc.android.WiTMJ.dao.RecordsDao;
import com.wistron.swpc.android.WiTMJ.dao.HostDao;
import com.wistron.swpc.android.WiTMJ.dao.ParticipantsDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig pushDaoConfig;
    private final DaoConfig dbWorkOutDaoConfig;
    private final DaoConfig profileDaoConfig;
    private final DaoConfig workOutDaoConfig;
    private final DaoConfig recordsDaoConfig;
    private final DaoConfig hostDaoConfig;
    private final DaoConfig participantsDaoConfig;

    private final PushDao pushDao;
    private final DbWorkOutDao dbWorkOutDao;
    private final ProfileDao profileDao;
    private final WorkOutDao workOutDao;
    private final RecordsDao recordsDao;
    private final HostDao hostDao;
    private final ParticipantsDao participantsDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        pushDaoConfig = daoConfigMap.get(PushDao.class).clone();
        pushDaoConfig.initIdentityScope(type);

        dbWorkOutDaoConfig = daoConfigMap.get(DbWorkOutDao.class).clone();
        dbWorkOutDaoConfig.initIdentityScope(type);

        profileDaoConfig = daoConfigMap.get(ProfileDao.class).clone();
        profileDaoConfig.initIdentityScope(type);

        workOutDaoConfig = daoConfigMap.get(WorkOutDao.class).clone();
        workOutDaoConfig.initIdentityScope(type);

        recordsDaoConfig = daoConfigMap.get(RecordsDao.class).clone();
        recordsDaoConfig.initIdentityScope(type);

        hostDaoConfig = daoConfigMap.get(HostDao.class).clone();
        hostDaoConfig.initIdentityScope(type);

        participantsDaoConfig = daoConfigMap.get(ParticipantsDao.class).clone();
        participantsDaoConfig.initIdentityScope(type);

        pushDao = new PushDao(pushDaoConfig, this);
        dbWorkOutDao = new DbWorkOutDao(dbWorkOutDaoConfig, this);
        profileDao = new ProfileDao(profileDaoConfig, this);
        workOutDao = new WorkOutDao(workOutDaoConfig, this);
        recordsDao = new RecordsDao(recordsDaoConfig, this);
        hostDao = new HostDao(hostDaoConfig, this);
        participantsDao = new ParticipantsDao(participantsDaoConfig, this);

        registerDao(Push.class, pushDao);
        registerDao(DbWorkOut.class, dbWorkOutDao);
        registerDao(Profile.class, profileDao);
        registerDao(WorkOut.class, workOutDao);
        registerDao(Records.class, recordsDao);
        registerDao(Host.class, hostDao);
        registerDao(Participants.class, participantsDao);
    }
    
    public void clear() {
        pushDaoConfig.getIdentityScope().clear();
        dbWorkOutDaoConfig.getIdentityScope().clear();
        profileDaoConfig.getIdentityScope().clear();
        workOutDaoConfig.getIdentityScope().clear();
        recordsDaoConfig.getIdentityScope().clear();
        hostDaoConfig.getIdentityScope().clear();
        participantsDaoConfig.getIdentityScope().clear();
    }

    public PushDao getPushDao() {
        return pushDao;
    }

    public DbWorkOutDao getDbWorkOutDao() {
        return dbWorkOutDao;
    }

    public ProfileDao getProfileDao() {
        return profileDao;
    }

    public WorkOutDao getWorkOutDao() {
        return workOutDao;
    }

    public RecordsDao getRecordsDao() {
        return recordsDao;
    }

    public HostDao getHostDao() {
        return hostDao;
    }

    public ParticipantsDao getParticipantsDao() {
        return participantsDao;
    }

}
