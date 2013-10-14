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

import android.provider.BaseColumns;

public abstract interface DBConst {
    // 데이터베이스 버전
    public static final int DATABASE_VERSION = 20131014;
    // 데이터베이스 파일명
    public static final String DATABASE_NAME = "dpDB.db";
    
    // 즐겨찾기 게시판 테이블명
    public static final String FAVORITE_TABLE_NAME = "favorite";
    // DP 게시판 테이블명
    public static final String BBS_TABLE_NAME = "bbs";
    
    public static final String KEY_ID = "_id";
    
    public static final class Favorite implements BaseColumns, DBConst.BbsColumns {
        public static final String DEFAULT_SORT_ORDER = "creation_time ASC";
        public static final String CREATION_TIME = "creation_time";
    }
    
    public static final class Bbs implements BaseColumns, DBConst.BbsColumns {
        public static final String DEFAULT_SORT_ORDER = "top_id ASC, cat_id ASC, bbs_id ASC";
        public static final String DESC_SORT_ORDER = "bbs_id DESC";
    }

    public static abstract interface BbsColumns {
        public static final String BBS_ID = "bbs_id";
        public static final String CAT_ID = "cat_id";
        public static final String GROUP_TITLE = "group_title";
        public static final String LOGIN_CHECK = "login_check";
        public static final String MAJOR = "major";
        public static final String MASTER_ID = "master_id";
        public static final String MINOR = "minor";
        public static final String TARGET_URL = "target_url";
        public static final String TITLE = "title";
        public static final String TOP_ID = "top_id";
        public static final String UNIQ_ID = "uniq_id";
    }
}