package com.zzlecheng.cashbook.util;

import android.util.Log;

/**
 * Author:RandBII
 * Date:2018/6/2 0002
 * Description:
 */
public class LogUtils {
    public static final String TAG = "Message--";
    public static boolean isDebug;

    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

}
