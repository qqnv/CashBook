package com.zzlecheng.cashbook;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zzlecheng.cashbook.util.LogUtils;
import com.zzlecheng.cashbook.util.SQLiteDbHelper;
import com.zzlecheng.cashbook.util.SharedPreferenceUtils;
import com.zzlecheng.cashbook.util.ToastUtils;

/**
 * @类名: MyApplication
 * @描述:
 * @作者: huangchao
 * @时间: 2018/9/18 上午10:27
 * @版本: 1.0.0
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.isDebug = true;
        ToastUtils.init(true);
        SharedPreferenceUtils.getInstance().init(this);
    }
}
