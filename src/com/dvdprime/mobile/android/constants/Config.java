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
package com.dvdprime.mobile.android.constants;

import android.os.Environment;

import com.dvdprime.mobile.android.util.StringUtil;

import java.io.File;

public final class Config {

    /** Debug Status */
    public static final boolean DEBUG = false;

    /** utf-8 */
    public static final String UTF8 = "utf-8";

    /** euc-kr */
    public static final String EUC_KR = "euc-kr";

    /** Mobile DP Server URL (Service) */
    public static final String MOBILE_DP_URL = "REAL_MOBILE_DP_SERVER";
    // public static final String MOBILE_DP_URL = "http://192.168.0.10/dp";

    /** Count API */
    public static final String MOBILE_DP_COUNT = MOBILE_DP_URL + "/count?id={0}";

    /** Filter List API */
    public static final String MOBILE_DP_API_FILTER_LIST = MOBILE_DP_URL + "/filters?id={0}";

    /** Filter Delete API */
    public static final String MOBILE_DP_API_FILTER_DELETE = MOBILE_DP_URL + "/filter?id={0}&targetId={1}";

    /** Filter Post API */
    public static final String MOBILE_DP_API_FILTER_POST = MOBILE_DP_URL + "/filter";

    /** Notification Post API */
    public static final String MOBILE_DP_API_NOTIFICATION_POST = MOBILE_DP_URL + "/notification";

    /** Notification List API */
    public static final String MOBILE_DP_API_NOTIFICATION_LIST = MOBILE_DP_URL + "/notifications?id={0}";

    /** DvdPrime Home URL */
    public static final String HOMEPAGE_URL = "http://dvdprime.donga.com";

    /** DvdPrime Login URL */
    public static final String LOGIN_URL = HOMEPAGE_URL + "/membership/login_proc.asp";

    /** DvdPrime Document Write URL */
    public static final String WRITE_URL = HOMEPAGE_URL + "/bbs/writeok.asp";

    /** DvdPrime Document Modify URL */
    public static final String MODIFY_URL = HOMEPAGE_URL + "/bbs/editOk.asp";

    /** DvdPrime Comment Write URL */
    public static final String WRITE_COMMENT_URL = HOMEPAGE_URL + "/bbs/memo/writeOk.asp";
    
    /** DvdPrime 쪽지 목록 호출 URL */
    public static final String MEMO_LIST_URL = HOMEPAGE_URL + "/note/src/Recv_Memo_List.asp?flag=sv";

    public static final String IMAGE_TAG = "<img src=\"{0}\">";

    /** Google Shortener URL */
    public static final String GOOGLE_SHORTENER_URL = "https://www.googleapis.com/urlshortener/v1/url?key=" + Config.GOOGLE_API_KEY;

    public static String getAbsoluteUrl(String url) {
        if (StringUtil.startsWith(url, "http:"))
            return url;
        else
            return HOMEPAGE_URL + url;
    }

    /** sdcard 경로 */
    public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();

    /** external package path */
    public static final String EXTERNAL_STORAGE_PATH = SDCARD_DIRECTORY + "/Android/data/com.dvdprime.mobile.android";

    /** external cache path */
    public static final String CACHE_PATH = EXTERNAL_STORAGE_PATH + File.separator + "cache";

    /** cache dir */
    public static final File CACHE_DIR = new File(CACHE_PATH);

    /** Google API Key */
    public static final String GOOGLE_API_KEY = "YOUR_GOOGLE_API_KEY";

    /** 글 내용 공유하기 메시지 (제목 - 단축URL) */
    public static final String SHARE_MESSAGE = "{0} - {1} from dvdprime.com";

    /** Google Analytics config */
    public static final String GA_TRAKING_ID = "UA-42872579-1";

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // GCM config
    // ///////////////////////////////////////////////////////////////////////////////////////////
    public static final String GCM_SERVER_URL = MOBILE_DP_URL + "/device";

    public static final String GCM_SENDER_ID = "YOUR_GCM_SENDER_ID_HERE";

    public static final String GCM_API_KEY = "YOUR_GCM_API_KEY_HERE";

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // Flickr config
    // ///////////////////////////////////////////////////////////////////////////////////////////
    public static final String FLICKR_API_KEY = "YOUR_FLICKR_API_KEY_HERE";

    public static final String FLICKR_API_SEC = "YOUR_FLICKR_API_SEC_HERE";

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // Ad Keys
    // ///////////////////////////////////////////////////////////////////////////////////////////
    /** 애드몹 키 */
    public static final String ADMOB_KEY = "YOUR_ADMOB_KEY_HERE";

    /** 애드포스트 키 */
    public static final String ADPOST_KEY = "YOUR_ADPOST_KEY_HERE";

    /** 아담 클라이언트 아이디 */
    public static final String ADAM_CLIENT_ID = "YOUR_ADAM_CLIENT_ID_HERE";

}