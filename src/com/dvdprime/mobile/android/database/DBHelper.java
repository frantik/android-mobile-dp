/**
 * Copyright 2013 작은광명
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dvdprime.mobile.android.database;

import static com.dvdprime.mobile.android.util.LogUtil.LOGD;
import static com.dvdprime.mobile.android.util.LogUtil.LOGE;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;

/**
 * Database Helper
 * 
 * @author 작은광명
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
    /** TAG */
    private static final String TAG = makeLogTag(DBHelper.class.getSimpleName());

    /** private key */
    private final String DP_DB_PRIMARY_KEY_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";

    /** interger */
    private final String DP_DB_INTEGER_TYPE = "INTEGER";

    /** varchar(32) */
    private final String DP_DB_VARCHAR_32 = "VARCHAR(32)";

    /** varchar(128) */
    private final String DP_DB_VARCHAR_128= "VARCHAR(128)";

    /** varchar(512) */
    private final String DP_DB_VARCHAR_512 = "VARCHAR(512)";

    /** text */
    private final String DP_DB_TEXT_TYPE = "TEXT";

    private static DBHelper mDBHelper = null;

    @SuppressLint("UseSparseArrays")
    private static HashMap<Integer, Long> mLATMap = new HashMap<Integer, Long>();

    private DBHelper() {
        super(DpApp.getContext(), DBConst.DATABASE_NAME, null, DBConst.DATABASE_VERSION);
    }

    /**
     * Return DpDBHelper instance
     * 
     * @param context
     *            context
     * @return DpDBHelper instance
     */
    public static synchronized DBHelper getInstance() {
        if (mDBHelper == null) {
            mDBHelper = new DBHelper();
        }
        return mDBHelper;
    }

    public static synchronized HashMap<Integer, Long> getLATMap() {
        return mLATMap;
    }

    public static synchronized void removeLATMap(int id) {
        mLATMap.remove(id);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    /**
     * Create tables
     * 
     * @param db
     *            dpdb instance
     */
    public void createTables(SQLiteDatabase db) {
        db.beginTransaction();

        // FAVORITE
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBConst.FAVORITE_TABLE_NAME 
                + " (" + DBConst.Bbs._ID + " " + DP_DB_PRIMARY_KEY_TYPE + "," 
                + DBConst.Bbs.UNIQ_ID + " " + DP_DB_VARCHAR_32 + "," 
                + DBConst.Bbs.TOP_ID + " " + DP_DB_INTEGER_TYPE + "," 
                + DBConst.Bbs.CAT_ID + " " + DP_DB_INTEGER_TYPE + "," 
                + DBConst.Bbs.BBS_ID + " " + DP_DB_INTEGER_TYPE + "," 
                + DBConst.Bbs.GROUP_TITLE + " " + DP_DB_VARCHAR_128 + "," 
                + DBConst.Bbs.TITLE + " " + DP_DB_TEXT_TYPE + "," 
                + DBConst.Bbs.MAJOR + " " + DP_DB_VARCHAR_32 + ","
                + DBConst.Bbs.MINOR + " " + DP_DB_VARCHAR_32 + "," 
                + DBConst.Bbs.MASTER_ID + " " + DP_DB_VARCHAR_32 + "," 
                + DBConst.Bbs.TARGET_URL + " " + DP_DB_VARCHAR_512 + "," 
                + DBConst.Bbs.LOGIN_CHECK + " " + DP_DB_INTEGER_TYPE + " DEFAULT 0," 
                + DBConst.Favorite.CREATION_TIME + " DATETIME DEFAULT current_timestamp" + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS creationTimeIndex ON " + DBConst.FAVORITE_TABLE_NAME + " (" + DBConst.Favorite.CREATION_TIME + ");");
        
        LOGD(TAG, "DB TABLE(" + DBConst.FAVORITE_TABLE_NAME + ") Has been created.");
        
        // BBS
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBConst.BBS_TABLE_NAME 
                + " (" + DBConst.Bbs._ID + " " + DP_DB_PRIMARY_KEY_TYPE + "," 
                + DBConst.Bbs.UNIQ_ID + " " + DP_DB_VARCHAR_32 + "," 
                + DBConst.Bbs.TOP_ID + " " + DP_DB_INTEGER_TYPE + "," 
                + DBConst.Bbs.CAT_ID + " " + DP_DB_INTEGER_TYPE + "," 
                + DBConst.Bbs.BBS_ID + " " + DP_DB_INTEGER_TYPE + "," 
                + DBConst.Bbs.GROUP_TITLE + " " + DP_DB_VARCHAR_128 + "," 
                + DBConst.Bbs.TITLE + " " + DP_DB_TEXT_TYPE + "," 
                + DBConst.Bbs.MAJOR + " " + DP_DB_VARCHAR_32 + ","
                + DBConst.Bbs.MINOR + " " + DP_DB_VARCHAR_32 + "," 
                + DBConst.Bbs.MASTER_ID + " " + DP_DB_VARCHAR_32 + "," 
                + DBConst.Bbs.TARGET_URL + " " + DP_DB_VARCHAR_512 + "," 
                + DBConst.Bbs.LOGIN_CHECK + " " + DP_DB_INTEGER_TYPE + " DEFAULT 0" + ");");

        db.execSQL("CREATE INDEX IF NOT EXISTS uniqIdIndex ON " + DBConst.BBS_TABLE_NAME + " (" + DBConst.Bbs.UNIQ_ID + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS catIdIndex ON " + DBConst.BBS_TABLE_NAME + " (" + DBConst.Bbs.CAT_ID + ");");

        LOGD(TAG, "DB TABLE(" + DBConst.BBS_TABLE_NAME + ") Has been created.");

        InputStream is;
        int size;
        byte[] buffer = null;
        try {
            is = DpApp.getContext().getAssets().open("DpDB.csv");
            size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();

            String[] dbData = StringUtil.split(new String(buffer), "\n");

            ContentValues[] values = new ContentValues[dbData.length];
            for (int i = 0; i < dbData.length; i++) {
                String[] columnData = dbData[i].split(",");
                values[i] = new ContentValues();
                values[i].put(DBConst.Bbs.UNIQ_ID, columnData[0]);
                values[i].put(DBConst.Bbs.TOP_ID, columnData[1]);
                values[i].put(DBConst.Bbs.CAT_ID, columnData[2]);
                values[i].put(DBConst.Bbs.BBS_ID, columnData[3]);
                values[i].put(DBConst.Bbs.GROUP_TITLE, columnData[4]);
                values[i].put(DBConst.Bbs.TITLE, columnData[5]);
                values[i].put(DBConst.Bbs.MAJOR, columnData[6]);
                values[i].put(DBConst.Bbs.MINOR, columnData[7]);
                values[i].put(DBConst.Bbs.MASTER_ID, columnData[8]);
                values[i].put(DBConst.Bbs.TARGET_URL, columnData[9]);
                values[i].put(DBConst.Bbs.LOGIN_CHECK, columnData[10]);
                db.insert(DBConst.BBS_TABLE_NAME, null, values[i]);
            }
        } catch (IOException e) {
            LOGE(TAG, "DB TABLE(" + DBConst.BBS_TABLE_NAME + ") Has been insert fail default data.");
        }

        LOGD(TAG, "DB TABLE(" + DBConst.BBS_TABLE_NAME + ") Has been inserted default data.");

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGD(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        upgradeTables(db);
    }

    /**
     * Upgrade Dp db tables
     * 
     * @param db
     *            dpdb instance
     */
    public void upgradeTables(SQLiteDatabase db) {

        // Main Tables
        db.execSQL("DROP TABLE IF EXISTS " + DBConst.BBS_TABLE_NAME);
        db.execSQL("DROP INDEX IF EXISTS uniqIdIndex");
        db.execSQL("DROP INDEX IF EXISTS catIdIndex");
        
        PrefUtil.getInstance().removePref(PrefKeys.ACCOUNT_ID);
        PrefUtil.getInstance().removePref(PrefKeys.ACCOUNT_PW);
        PrefUtil.getInstance().removePref(PrefKeys.FILTERS);
        PrefUtil.getInstance().removePref(PrefKeys.COOKIES);

        createTables(db);
    }

}
