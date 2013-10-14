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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.model.Bbs;
import com.dvdprime.mobile.android.util.PrefUtil;

/**
 * Database Adapter
 * 
 * @author 작은광명
 * 
 */
public class DBAdapter {

    private static DBAdapter instance;

    private DBHelper mOpenHelper;

    private SQLiteDatabase mDb;

    public DBAdapter() {
        mOpenHelper = DBHelper.getInstance();
        mDb = null;
    }

    public static synchronized DBAdapter getInstance() {
        if (instance == null) {
            instance = new DBAdapter();
        }
        return instance;
    }

    public DBAdapter open() throws SQLException {
        return this;
    }

    public void close() {
        mDb = null;
    }

    private void getReadableDatabase() throws SQLiteException {
        if (mDb == null) {
            try {
                mDb = mOpenHelper.getReadableDatabase();
            } catch (SQLiteException e) {
                throw e;
            }
        }
    }

    private void getWritableDatabase() throws SQLiteException {
        if (mDb == null) {
            try {
                mDb = mOpenHelper.getWritableDatabase();
            } catch (SQLiteException e) {
                throw e;
            }
        }
    }

    /**
     * Drop all tables in DpDB then recreate tables. (except bbs table)
     * 
     * @return always true
     */
    public boolean dropAllTables() {
        try {
            getWritableDatabase();
        } catch (SQLiteException e) {
            return false;
        }

        mOpenHelper.upgradeTables(mDb);
        return true;
    }

    /**
     * 게시판 목록을 조회한다.
     * 
     * @param topId
     *            상위 고유번호
     * @return
     */
    public List<Bbs> selectBbsList(int topId) {
        List<Bbs> mResult = null;

        try {
            getReadableDatabase();
        } catch (SQLiteException e) {
            return mResult;
        }

        String[] cols = new String[] { DBConst.Bbs._ID, DBConst.Bbs.UNIQ_ID, DBConst.Bbs.TOP_ID, DBConst.Bbs.CAT_ID, DBConst.Bbs.BBS_ID, DBConst.Bbs.GROUP_TITLE, DBConst.Bbs.TITLE, DBConst.Bbs.MAJOR, DBConst.Bbs.MINOR, DBConst.Bbs.MASTER_ID, DBConst.Bbs.TARGET_URL, DBConst.Bbs.LOGIN_CHECK };
        StringBuilder where = new StringBuilder();
        if (topId > 0) {
            where.append(DBConst.Bbs.TOP_ID + " = " + topId);
        }
        if (topId > -1 && PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, null) == null) {
            if (where.length() > 0) {
                where.append(" AND ");
            }
            where.append(DBConst.Bbs.LOGIN_CHECK + " = 0");
        }

        Cursor c = null;

        if (topId == 0) {
            c = mDb.query(DBConst.FAVORITE_TABLE_NAME, cols, where.toString(), null, null, null, DBConst.Favorite.DEFAULT_SORT_ORDER);
        } else {
            c = mDb.query(DBConst.BBS_TABLE_NAME, cols, where.toString(), null, null, null, DBConst.Bbs.DEFAULT_SORT_ORDER);
        }

        if (c != null && c.moveToFirst()) {
            mResult = new ArrayList<Bbs>();
            do {
                Bbs bbs = new Bbs();
                bbs.setId(c.getString(0));
                bbs.setUniqId(c.getString(1));
                bbs.setTopId(c.getInt(2));
                bbs.setCatId(c.getInt(3));
                bbs.setBbsId(c.getInt(4));
                bbs.setGroupTitle(c.getString(5));
                bbs.setTitle(c.getString(6));
                bbs.setMajor(c.getString(7));
                bbs.setMinor(c.getString(8));
                bbs.setMasterId(c.getString(9));
                bbs.setTargetUrl(c.getString(10));
                bbs.setLoginCheck(c.getInt(11));
                bbs.setIsFavorite(selectFavoriteOne(bbs.getUniqId()));

                mResult.add(bbs);
            } while (c.moveToNext());
        }

        if (c != null)
            c.close();
        
        return mResult;
    }

    /**
     * 게시판 하나를 조회한다.
     * 
     * @param uniqId
     *            게시판 고유번호
     * @return
     */
    public Bbs selectBbsOne(String uniqId) {
        Bbs result = null;

        try {
            getReadableDatabase();
        } catch (SQLiteException e) {
            return result;
        }

        String[] cols = new String[] { DBConst.Bbs._ID, DBConst.Bbs.UNIQ_ID, DBConst.Bbs.TOP_ID, DBConst.Bbs.CAT_ID, DBConst.Bbs.BBS_ID, DBConst.Bbs.GROUP_TITLE, DBConst.Bbs.TITLE, DBConst.Bbs.MAJOR, DBConst.Bbs.MINOR, DBConst.Bbs.MASTER_ID, DBConst.Bbs.TARGET_URL, DBConst.Bbs.LOGIN_CHECK };
        StringBuilder where = new StringBuilder();
        where.append(DBConst.Bbs.UNIQ_ID + " = '" + uniqId + "'");

        Cursor c = mDb.query(DBConst.BBS_TABLE_NAME, cols, where.toString(), null, null, null, null);

        if (c != null && c.moveToFirst()) {
            result = new Bbs();
            result.setId(c.getString(0));
            result.setUniqId(c.getString(1));
            result.setTopId(c.getInt(2));
            result.setCatId(c.getInt(3));
            result.setBbsId(c.getInt(4));
            result.setGroupTitle(c.getString(5));
            result.setTitle(c.getString(6));
            result.setMajor(c.getString(7));
            result.setMinor(c.getString(8));
            result.setMasterId(c.getString(9));
            result.setTargetUrl(c.getString(10));
            result.setLoginCheck(c.getInt(11));
        }

        if (c != null)
            c.close();

        return result;
    }

    /**
     * 즐겨찾기 하나를 조회한다.
     * 
     * @param uniqId
     *            게시판 고유번호
     * @return
     */
    public int selectFavoriteOne(String uniqId) {
        int result = 0;

        try {
            getReadableDatabase();
        } catch (SQLiteException e) {
            return result;
        }

        StringBuilder where = new StringBuilder();
        where.append(DBConst.Bbs.UNIQ_ID + " = '" + uniqId + "'");

        Cursor c = mDb.query(DBConst.FAVORITE_TABLE_NAME, null, where.toString(), null, null, null, null);

        if (c != null && c.moveToFirst()) {
            result = c.getCount();
        }

        if (c != null)
            c.close();

        return result;
    }

    /**
     * 즐겨찾기 테이블에 추가
     * 
     * @param uniqId
     *            게시판 고유번호
     */
    public long insertFavoite(String uniqId) {
        long result = 0L;
        
        try {
            getWritableDatabase();
        } catch (SQLiteException e) {
        }

        StringBuilder where = new StringBuilder();
        where.append(DBConst.Bbs.UNIQ_ID + " = '" + uniqId + "'");

        boolean isExists = false;
        Cursor c = mDb.query(DBConst.FAVORITE_TABLE_NAME, new String[] { "count(*) as count" }, where.toString(), null, null, null, null);

        if (c != null) {
            if (c.moveToNext()) {
                isExists = c.getInt(0) > 0;
            }
            c.close();
        }

        if (!isExists) {
            Bbs bbs = selectBbsOne(uniqId);
            if (bbs != null) {
                ContentValues values = new ContentValues();
                values.put(DBConst.Bbs.UNIQ_ID, bbs.getUniqId());
                values.put(DBConst.Bbs.TOP_ID, bbs.getTopId());
                values.put(DBConst.Bbs.CAT_ID, bbs.getCatId());
                values.put(DBConst.Bbs.BBS_ID, bbs.getBbsId());
                values.put(DBConst.Bbs.GROUP_TITLE, bbs.getGroupTitle());
                values.put(DBConst.Bbs.TITLE, bbs.getTitle());
                values.put(DBConst.Bbs.MAJOR, bbs.getMajor());
                values.put(DBConst.Bbs.MINOR, bbs.getMinor());
                values.put(DBConst.Bbs.MASTER_ID, bbs.getMasterId());
                values.put(DBConst.Bbs.TARGET_URL, bbs.getTargetUrl());
                values.put(DBConst.Bbs.LOGIN_CHECK, bbs.getLoginCheck());
                result = mDb.insert(DBConst.FAVORITE_TABLE_NAME, null, values);
            }
        }
        
        return result;
    }

    /**
     * 즐겨찾기 테이블에서 게시판 삭제
     * 
     * @param uniqId
     *            게시판 고유번호
     */
    public int deleteFavorite(String uniqId) {
        try {
            getWritableDatabase();
        } catch (SQLiteException e) {
        }

        StringBuilder where = new StringBuilder();
        where.append(DBConst.Bbs.UNIQ_ID + " = '" + uniqId + "'");

        return mDb.delete(DBConst.FAVORITE_TABLE_NAME, where.toString(), null);
    }
}