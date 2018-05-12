package com.wistron.swpc.android.WiTMJ;

import android.text.TextUtils;
import android.util.Log;

/**
 * LogUtils used to formatted log information Format like this:
 * [filename:linenumber]info eg.[LogUtils:10]MDMService Starts...
 */
public class LogUtils {

    private static final boolean DEBUG = true;

    /**
     * @param tag  Used to identify the source of a log message. It usually
     *             identifies the class or activity where the log call occurs.
     * @param info The information you would like logged
     */
    public static void d(String tag, String info) {

        printLog(Log.DEBUG, tag, info);
    }

    /**
     * @param tag  Used to identify the source of a log message. It usually
     *             identifies the class or activity where the log call occurs.
     * @param info The information you would like logged
     */
    public static void e(String tag, String info) {

        printLog(Log.ERROR, tag, info);
    }

    /**
     * @param tag  Used to identify the source of a log message. It usually
     *             identifies the class or activity where the log call occurs.
     * @param info The information you would like logged
     */
    public static void w(String tag, String info) {

        printLog(Log.WARN, tag, info);
    }

    /**
     * @param tag  Used to identify the source of a log message. It usually
     *             identifies the class or activity where the log call occurs.
     * @param info The information you would like logged
     */
    public static void v(String tag, String info) {

        printLog(Log.VERBOSE, tag, info);
    }

    /**
     * @param tag  Used to identify the source of a log message. It usually
     *             identifies the class or activity where the log call occurs.
     * @param info The information you would like logged
     */
    public static void i(String tag, String info) {

        printLog(Log.INFO, tag, info);
    }

    private static void printLog(int type, String tag, String info) {

        if (DEBUG) {
            info = fromatMessage(info);
            switch (type) {
                case Log.DEBUG:
                    Log.d(tag, info);
                    break;
                case Log.ERROR:
                    Log.e(tag, info);
                    break;
                case Log.WARN:
                    Log.w(tag, info);
                    break;
                case Log.INFO:
                    Log.i(tag, info);
                    break;
                case Log.VERBOSE:
                default:
                    Log.v(tag, info);
                    break;
            }
        }
    }

    private static String fromatMessage(String info) {

        Thread current = Thread.currentThread();
        StackTraceElement[] stack = current.getStackTrace();
        if ((null == stack) || (stack.length < 6) || (null == stack[5])) {
            if ((null == info) || (info.trim().length() == 0)) {
                return " unuse information ";
            } else {
                return info;
            }
        }

        if ((null == info) || (info.trim().length() == 0)) {
            info = " unuse information ";
        }

        int line = stack[5].getLineNumber();
        if (line > 0) {
            info = ":" + line + "] " + info;
        } else {
            info = "]" + info;
        }

        String fileName = stack[5].getFileName();
        if (!TextUtils.isEmpty(fileName)) {
            fileName = fileName.replace(".java", "");
            info = "[" + fileName + info;
        } else {
            info = "[" + info;
        }

        return info;
    }
}
