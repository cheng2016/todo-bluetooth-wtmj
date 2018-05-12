package com.wistron.swpc.android.WiTMJ;

/**
 * Created by WH1106013 on 2016/4/1.

/**
 * 应用对象获取方法
 * 所有地方有需要拿到上下文的地方
 * 都可以通过此类的静态接口去
 * 拿到上下文句柄
 *
 * @author bin zhou
 * @see
 */
public class ApplicationHolder {
    private static TmjApplication application;

    public static TmjApplication getApplication() {
        return application;
    }

    public static void setApplication(TmjApplication app) {
        application = app;
    }
}
