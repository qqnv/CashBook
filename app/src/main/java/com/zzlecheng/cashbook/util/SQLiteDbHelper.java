package com.zzlecheng.cashbook.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @类名: MyDataBaseHelper
 * @描述:
 * @作者: huangchao
 * @时间: 2018/9/19 上午10:33
 * @版本: 1.0.0
 */
public class SQLiteDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "database.db";

    public static final int DB_VERSION = 1;


    public static final String TABLE_CATEGORY = "category";

    //创建 菜品品类表 category  表的 sql 语句
    private static String CATEGORY_CREATE_TABLE_SQL = "create table " + TABLE_CATEGORY + "("
            + "id text,"
            + "lbmc text,"
            + "sfyx text,"
            + "px integer "
            + ");";

    public static final String TABLE_VARIETY = "variety";

    //创建 菜品表 variety  表的 sql 语句
    private static String VARIETY_CREATE_TABLE_SQL = "create table " + TABLE_VARIETY + "("
            + "id text,"
            + "lbid text,"
            + "cpmc text,"
            + "dw text,"
            + "sfyx text,"
            + "px integer "
            + ");";


    public static final String TABLE_TOTALPRICE = "totalPrice";

    //创建 入库总价  表的 sql 语句
    private static String TOTALPRICE_CREATE_TABLE_SQL = "create table " + TABLE_TOTALPRICE + "("
            + "id text,"
            + "rq text,"
            + "lrsj text,"
            + "zj text "
            + ");";

    public static final String TABLE_DETAILS = "details";

    //创建 菜品详情  表的 sql 语句
    private static String DETAILS_CREATE_TABLE_SQL = "create table " + TABLE_DETAILS + "("
            + "id text,"
            + "rq text,"
            + "cpid text,"
            + "sl text,"
            + "dj text,"
            + "zj text"
            + ");";

    public SQLiteDbHelper(Context context) {
        // 传递数据库名与版本号给父类
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // 在这里通过 db.execSQL 函数执行 SQL 语句创建所需要的表
        // 创建 students 表

        db.execSQL(VARIETY_CREATE_TABLE_SQL);
        db.execSQL(CATEGORY_CREATE_TABLE_SQL);
        db.execSQL(TOTALPRICE_CREATE_TABLE_SQL);
        db.execSQL(DETAILS_CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 数据库版本号变更会调用 onUpgrade 函数，在这根据版本号进行升级数据库
        switch (oldVersion) {
            case 1:
                // do something
                break;

            default:
                break;
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // 启动外键
            db.execSQL("PRAGMA foreign_keys = 1;");

            //或者这样写
            String query = String.format("PRAGMA foreign_keys = %s", "ON");
            db.execSQL(query);
        }
    }
}