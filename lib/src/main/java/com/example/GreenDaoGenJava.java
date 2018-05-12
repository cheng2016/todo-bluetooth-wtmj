package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenJava {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(14, "com.wistron.swpc.android.WiTMJ.bean");
        schema.setDefaultJavaPackageDao("com.wistron.swpc.android.WiTMJ.dao");

        addPush(schema);

        addDbWorkOut(schema);

        addMyWokeOut(schema);
        // addHost(schema);
        schema.enableKeepSectionsByDefault();
        schema.enableActiveEntitiesByDefault();
      //  new DaoGenerator().generateAll(schema, "E:\\android_git_new\\witmjclientaxi-new\\source\\WiTMJ\\app\\src\\main\\java");
  //     new DaoGenerator().generateAll(schema,"E:\\git\\WiTMJ_new3\\witmjclientaxi-new\\source\\WiTMJ\\app\\src\\main\\java");
        new DaoGenerator().generateAll(schema, "E:\\");
//       new DaoGenerator().generateAll(schema, "E:\\studio_workspace\\WiTMJ_new\\witmjclientaxi-new\\source\\WiTMJ\\app\\src\\main\\java");
//        new DaoGenerator().generateAll(schema,"D:\\android\\projects\\tmj3\\witmjclientaxi-new\\source\\WiTMJ\\app\\src\\main\\java");
    }


    private static void addDbWorkOut(Schema schema){
        Entity entity = schema.addEntity("DbWorkOut");
        entity.addIdProperty();
        entity.addStringProperty("workoutId");
        entity.addStringProperty("recordId");
        entity.addStringProperty("jsonStr");//与服务器交互的string
        entity.addIntProperty("status");//0 未完成比赛    2已上传服务器
    }

    private static void addPush(Schema schema) {
        Entity push = schema.addEntity("Push");
        push.addIdProperty();
        push.addStringProperty("userId").notNull();
        push.addStringProperty("pushMsg");
        push.addBooleanProperty("isRead");
    }

    private static void addMyWokeOut(Schema schema){
        //用户信息表
        Entity myProfile = schema.addEntity("Profile");
        myProfile.addStringProperty("profile_id").primaryKey();
        myProfile.addStringProperty("username");
        myProfile.addStringProperty("email");
        myProfile.addIntProperty("weight");
        myProfile.addIntProperty("height");
        myProfile.addIntProperty("gender");
        myProfile.addStringProperty("birthday");
        myProfile.addStringProperty("image_url");
        myProfile.addStringProperty("image_sas");
        myProfile.addStringProperty("createdat");

//        myProfile.addIntProperty("invitation");
//        myProfile.addStringProperty("record_id");


        //训练表
        Entity myWorkOut = schema.addEntity("WorkOut");
        myWorkOut.addStringProperty("workout_id").primaryKey();
        myWorkOut.addIntProperty("tag");
        myWorkOut.addIntProperty("permission");
        myWorkOut.addStringProperty("name");
        myWorkOut.addStringProperty("start_date");
        myWorkOut.addStringProperty("end_date");
        myWorkOut.addStringProperty("start_location");
        myWorkOut.addStringProperty("end_location");

        myWorkOut.addStringProperty("route");

        myWorkOut.addStringProperty("note");

        myWorkOut.addStringProperty("custom_cal");
        myWorkOut.addStringProperty("custom_distance");
        myWorkOut.addStringProperty("custom_speed");
        myWorkOut.addStringProperty("custom_duration");

//        myWorkOut.addStringProperty("participants");

        //用户记录表
        Entity myRecords = schema.addEntity("Records");
        myRecords.addStringProperty("record_id").primaryKey();
        myRecords.addStringProperty("workout_id");
        myRecords.addIntProperty("tag");// 1 navigation 2 train 3 instant 4 schedule
        myRecords.addIntProperty("invitation");//邀请状态0未同意，1已同意
        myRecords.addStringProperty("start_time");
        myRecords.addStringProperty("end_time");

        myRecords.addStringProperty("duration");
        myRecords.addStringProperty("distance");
        myRecords.addStringProperty("calories");
        myRecords.addStringProperty("avg_speed");

        myRecords.addIntProperty("rank");
        myRecords.addStringProperty("routes");

        //创建 创建着表
        Entity myHost = schema.addEntity("Host");
        myHost.addStringProperty("host_id").primaryKey();
        myHost.addStringProperty("username");
        myHost.addStringProperty("image_url");
        myHost.addStringProperty("email");

        //创建参与者表
        Entity participants = schema.addEntity("Participants");
        participants.addIdProperty();
        participants.addStringProperty("user_id");
        participants.addStringProperty("username");
        participants.addStringProperty("invitation");
        participants.addStringProperty("record_id");

        /**
         *  表关系如下
         *
         *   一个用户可以拥有多个比赛
         *   一场比赛对应一个用户
         *
         *   一个比赛可以有多个参与者
         *   一个参与者对应一个比赛
         *
         *   一个参与者有一个记录
         *
         *   一个比赛对应一个创建者用户
         *   一个创建者用户可以创建多个比赛
         *
         *   一个记录对应一个比赛
         *   一个记录对应一个用户
         *   一个用户有多个记录
         *   一个比赛有多个记录
         *
         * 建立多表关联
         * myProfile表 --> myWorkout       一对多
         *
         * 建立一对多关系
         * myProfile表 ---> myRecords表    一对多
         *
         * 建立一对多关系
         * myWorkout --> myRecords         一对多
         *
         * 建立一对多关系
         * myHost --> myWorkout            一对多
         *
         */

        /**
         * 建立一对多关联（用户对记录为一对多）
         *  (待审核)
         *  暂不需要
         */
//        Property property_pro = myRecords.addStringProperty("profile_id").getProperty();
//        myRecords.addToOne(myProfile,property_pro);
//        myProfile.addToMany(myRecords,property_pro).setName("record");


        /**
         * 建立一对多关联(profile  对  workout  一对多)
         *  (待审核)
         *  暂不需要
         */
//        Property property_profile = myWorkOut.addStringProperty("profile_id").getProperty();
//        myWorkOut.addToOne(myWorkOut,property_profile);
//        myProfile.addToMany(myWorkOut,property_profile).setName("workout");


        /**
         * 建立一对多关联(workout 对  participants  一对多)
         * 已确定
         *
         */
        Property property_part = participants.addStringProperty("workout_id").getProperty();
        participants.addToOne(myWorkOut,property_part);
        myWorkOut.addToMany(participants,property_part).setName("participants");


        /**
         * 建立一对多关联(workout 对  records  一对多)
         *  (待审核)
         *  暂不需要
         */
//        Property property_work = myRecords.addStringProperty("workout_id").getProperty();
//        myRecords.addToOne(myWorkOut,property_work);
//        myWorkOut.addToMany(myRecords,property_work).setName("record");


        //建立一对多关联 (创建者对比赛 一对多)
        Property property_host =  myWorkOut.addStringProperty("host_id").getProperty();
        myWorkOut.addToOne(myHost,property_host);
        myHost.addToMany(myWorkOut,property_host).setName("workout");
    }

}


