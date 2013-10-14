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
package com.dvdprime.mobile.android;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.dvdprime.mobile.android.model.Document;
import com.dvdprime.mobile.android.provider.LoginRequestProvider;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.volley.OkHttpStack;
import com.dvdprime.mobile.android.volley.Volley;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DpApp extends Application {
    private static Context context;
    private static DpApp instance;
    private static Set<String> mAvoidDuplicate;
    private static ArrayList<Document> mDocumentList = new ArrayList<Document>();
    private static boolean mHasData;
    private static boolean mNeedLogin;
    private static int mPage;
    private static RequestQueue mRequestQueue;

    static {
        mAvoidDuplicate = new HashSet<String>();
        mHasData = false;
        mNeedLogin = false;
        mPage = 1;
        com.android.volley.VolleyLog.DEBUG = false;
    }

    public static void addCookie(Map<String, String> paramMap) {
        if (PrefUtil.getInstance().getString("cookies", null) == null)
            return;
        paramMap.put("cookie", PrefUtil.getInstance().getString("cookies", ""));
    }

    public static Context getContext() {
        return context;
    }

    public static DpApp getDP() {
        return instance;
    }

    public static Set<String> getDocDuplicateSet() {
        if (mAvoidDuplicate != null)
            return mAvoidDuplicate;
        mAvoidDuplicate = new HashSet<String>();
        return mAvoidDuplicate;
    }

    public static ArrayList<Document> getDocumentList() {
        if (mDocumentList != null)
            return mDocumentList;
        mDocumentList = new ArrayList<Document>();
        return mDocumentList;
    }

    public static int getPage() {
        return mPage;
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null)
            return mRequestQueue;
        throw new IllegalStateException("RequestQueue not initialized");
    }

    public static boolean hasData() {
        return mHasData;
    }

    public static int incPage() {
        int i = mPage;
        mPage = i + 1;
        return i;
    }

    public static boolean isNeedLogin() {
        return mNeedLogin;
    }

    public static void requestLogin(String className, String id, String pw) {
        new LoginRequestProvider().execute(new String[] { className, id, pw });
    }

    public static void setCookies(String paramString) {
        if (paramString == null)
            return;
        PrefUtil.getInstance().setString("cookies", paramString);
    }

    public static void setDocumentClear() {
        mDocumentList.clear();
        mAvoidDuplicate.clear();
        mHasData = false;
        mPage = 1;
    }

    public static void setHasData(boolean paramBoolean) {
        mHasData = paramBoolean;
    }

    public static void setNeedLogin(boolean paramBoolean) {
        mNeedLogin = paramBoolean;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        context = this;
        mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack());
    }

    public void onTerminate() {
        super.onTerminate();
    }
}