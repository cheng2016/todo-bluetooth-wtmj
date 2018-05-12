package com.wistron.swpc.android.WiTMJ.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.wistron.swpc.android.WiTMJ.TmjApplication;

import java.util.Map;
import java.util.Set;

public class PreferencesUtil {
    public static final int DEFAULT_SHAREDPERENCES_MODE = Context.MODE_PRIVATE;
    private static final String DEF_STRING_VALUE = "";
    public static final boolean DEF_BOOLEAN_VALUE = true;
    protected SharedPreferences preferences;

    /**
     * Retrieve a boolean value from the preferences,default false
     * 
     * @param key
     * @return
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * Retrieve a boolean value from the preferences
     * 
     * @param key
     * @param defValue
     * @return
     */
    public boolean getBoolean(String key, boolean defValue) {
        return null != preferences ? preferences.getBoolean(key, defValue)
                : false;
    }

    /**
     * Retrieve a String value from the preferences,default ""
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        return null != preferences ? preferences.getString(key,
                DEF_STRING_VALUE) : DEF_STRING_VALUE;
    }

    /**
     * Retrieve a String value from the preferences
     * 
     * @param key
     * @return
     */
    public String getString(String key, String defValue) {
        return null != preferences ? preferences.getString(key, defValue)
                : DEF_STRING_VALUE;
    }

    /**
     * Retrieve an int value from the preferences,default 0
     * 
     * @param key
     * @return
     */
    public int getInt(String key) {
        return null != preferences ? preferences.getInt(key, 0) : 0;
    }

    /**
     * Retrieve an int value from the preferences
     * 
     * @param key
     * @return
     */
    public int getInt(String key, int defValue) {
        return null != preferences ? preferences.getInt(key, defValue) : 0;
    }

    /**
     * Retrieve a long value from the preferences,default 0
     * 
     * @param key
     * @return
     */
    public long getLong(String key) {
        return null != preferences ? preferences.getLong(key, 0) : 0;
    }

    /**
     * Retrieve a long value from the preferences
     * 
     * @param key
     * @return
     */
    public long getLong(String key, long defValue) {
        return null != preferences ? preferences.getLong(key, defValue) : 0;
    }

    /**
     * create new preferencesUtil instance,the Operating mode
     * Context.MODE_PRIVATE
     * 
     * @param context
     * @param table
     *            SharedPreferences name
     */
    public PreferencesUtil(Context context, String table) {
        if (null == context) {
            context = TmjApplication.getInstance().getApplicationContext();
        }
        preferences = context.getSharedPreferences(table,
                DEFAULT_SHAREDPERENCES_MODE);
    }

    /**
     * create new preferencesUtil instance
     * 
     * @param context
     * @param table
     *            SharedPreferences name
     * @param mode
     *            open the SharedPreferences
     */
    public PreferencesUtil(Context context, String table, int mode) {
        if (null == context) {
            throw new NullPointerException("context is null!");
        }
        preferences = context.getSharedPreferences(table, mode);
    }

    /**
     * write ContentValues data in SharedPreferences
     * 
     * @param values
     * @return
     */
    public boolean write(ContentValues values) {
        if (null == preferences) {
            return false;
        }
        Editor editor = preferences.edit();
        if (null == editor) {
            return false;
        }
        Set<Map.Entry<String, Object>> entries = values.valueSet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                editor.putString(key, String.valueOf(value));
            } else if (value instanceof Integer || value instanceof Short
                    || value instanceof Byte) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof Float || value instanceof Double) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            }
        }
        return editor.commit();
    }

    /**
     * Retrieve all values from the preferences
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public ContentValues read() {
        if (null == preferences) {
            return null;
        }
        Map map = preferences.getAll();
        if (null == map) {
            return null;
        }
        ContentValues values = new ContentValues();
        Set<Map.Entry<String, ?>> entries = map.entrySet();
        for (Map.Entry<String, ?> entry : entries) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                values.put(key, String.valueOf(value));
            } else if (value instanceof Integer || value instanceof Short
                    || value instanceof Byte) {
                values.put(key, (Integer) value);
            } else if (value instanceof Long) {
                values.put(key, (Long) value);
            } else if (value instanceof Float || value instanceof Double) {
                values.put(key, (Float) value);
            } else if (value instanceof Boolean) {
                values.put(key, (Boolean) value);
            }
        }
        return values;
    }

    /**
     * save String data(key-value)
     * 
     * @param key
     * @param value
     */
    public void saveString(String key, String value) {
        Editor editor = preferences.edit();
        if (null != editor) {
            editor.putString(key, value).commit();
        }
    }

    /**
     * save Boolean data(key-value)
     * 
     * @param key
     * @param value
     */
    public void saveBoolean(String key, boolean value) {
        Editor editor = preferences.edit();
        if (null != editor) {
            editor.putBoolean(key, value).commit();
        }
    }

    /**
     * save Int data(key-value)
     * 
     * @param key
     * @param value
     */
    public void saveInt(String key, int value) {
        Editor editor = preferences.edit();
        if (null != editor) {
            editor.putInt(key, value).commit();
        }
    }

    /**
     * save Long data(key-value)
     * 
     * @param key
     * @param value
     */
    public void saveLong(String key, long value) {
        Editor editor = preferences.edit();
        if (null != editor) {
            editor.putLong(key, value).commit();
        }
    }

    /**
     * determin whether contain the key valueF
     * 
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        if ((null != preferences) && preferences.contains(key)) {
            return true;
        }
        return false;
    }

    /**
     * delete SharedPreferences data by key
     * 
     * @param key
     */
    public void deleteKey(String key) {
        Editor editor = preferences.edit();
        if (null != editor) {
            editor.remove(key).commit();
        }
    }

    public static String getPrefString(Context context, String key,
            final String defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }

    public static void setPrefString(Context context, final String key,
            final String value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putString(key, value).commit();
    }

    public static boolean getPrefBoolean(Context context, final String key,
            final boolean defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getBoolean(key, defaultValue);
    }

    public static boolean hasKey(Context context, final String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(
                key);
    }

    public static void setPrefBoolean(Context context, final String key,
            final boolean value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putBoolean(key, value).commit();
    }

    public static void setPrefInt(Context context, final String key,
            final int value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putInt(key, value).commit();
    }

    public static int getPrefInt(Context context, final String key,
            final int defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getInt(key, defaultValue);
    }

    public static void setPrefFloat(Context context, final String key,
            final float value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putFloat(key, value).commit();
    }

    public static float getPrefFloat(Context context, final String key,
            final float defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getFloat(key, defaultValue);
    }

    public static void setSettingLong(Context context, final String key,
            final long value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putLong(key, value).commit();
    }

    public static long getPrefLong(Context context, final String key,
            final long defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getLong(key, defaultValue);
    }

    public static void clearPreference(Context context,
            final SharedPreferences p) {
        final Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }
}
