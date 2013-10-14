/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dvdprime.mobile.android.gcm;

import static com.dvdprime.mobile.android.util.LogUtil.LOGD;
import static com.dvdprime.mobile.android.util.LogUtil.LOGI;
import static com.dvdprime.mobile.android.util.LogUtil.LOGV;
import static com.dvdprime.mobile.android.util.LogUtil.LOGW;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.SystemUtil;
import com.dvdprime.mobile.android.volley.ApiRequest;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {
    private static final String TAG = makeLogTag("GCMs");

    /**
     * Register this account/device pair within the server.
     * 
     * @param context
     *            Current context
     * @param gcmId
     *            The GCM registration ID for this device
     * @return whether the registration succeeded or not.
     */
    public static boolean register(final Context context, final String gcmId) {
        register(context, PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, null), gcmId);
        return true;
    }

    public static boolean register(final Context context, final String accountId, final String gcmId) {
        LOGI(TAG, "registering device (gcm_id = " + gcmId + ")");
        if (accountId == null) {
            LOGW(TAG, "Account ID: null");
        } else {
            LOGI(TAG, "Account ID: " + accountId);

            Map<String, String> params = new HashMap<String, String>();
            params.put("id", accountId);
            params.put("token", gcmId);
            params.put("version", SystemUtil.getVersionName(context));
            post(params);
            setRegisteredOnServer(context, true, gcmId);
        }

        return true;
    }

    public static void unregister(final Context context, final String gcmId) {
        unregister(context, PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, null), gcmId);
    }

    /**
     * Unregister this account/device pair within the server.
     * 
     * @param context
     *            Current context
     * @param gcmId
     *            The GCM registration ID for this device
     */
    public static void unregister(final Context context, final String accountId, final String gcmId) {
        LOGI(TAG, "unregistering device (gcmId = " + gcmId + ")");

        if (accountId == null) {
            LOGW(TAG, "Account ID: null");
        } else {
            LOGI(TAG, "Account ID: " + accountId);

            Map<String, String> params = new HashMap<String, String>();
            params.put("id", accountId);
            params.put("token", gcmId);
            delete(params);
            // Regardless of server success, clear local preferences
            setRegisteredOnServer(context, false, null);
        }

    }

    /**
     * Sets whether the device was successfully registered in the server side.
     * 
     * @param context
     *            Current context
     * @param flag
     *            True if registration was successful, false otherwise
     * @param gcmId
     *            True if registration was successful, false otherwise
     */
    private static void setRegisteredOnServer(Context context, boolean flag, String gcmId) {
        PrefUtil prefs = PrefUtil.getInstance();
        LOGD(TAG, "Setting registered on server status as: " + flag);
        if (flag) {
            prefs.setLong(PrefKeys.GCM_REGISTERED_TS, new Date().getTime());
            prefs.setString(PrefKeys.GCM_REG_ID, gcmId);
        } else {
            prefs.removePref(PrefKeys.GCM_REG_ID);
        }
    }

    /**
     * Checks whether the device was successfully registered in the server side.
     * 
     * @param context
     *            Current context
     * @return True if registration was successful, false otherwise
     */
    public static boolean isRegisteredOnServer(Context context) {
        PrefUtil prefs = PrefUtil.getInstance();
        // Find registration threshold
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        long yesterdayTS = cal.getTimeInMillis();
        long regTS = prefs.getLong(PrefKeys.GCM_REGISTERED_TS, 0);
        if (regTS > yesterdayTS) {
            LOGV(TAG, "GCM registration current. regTS=" + regTS + " yesterdayTS=" + yesterdayTS);
            return true;
        } else {
            LOGV(TAG, "GCM registration expired. regTS=" + regTS + " yesterdayTS=" + yesterdayTS);
            return false;
        }
    }

    public static String getGcmId(Context context) {
        return PrefUtil.getInstance().getString(PrefKeys.GCM_REG_ID, null);
    }

    /**
     * Unregister the current GCM ID when we sign-out
     * 
     * @param context
     *            Current context
     */
    public static void onSignOut(Context context) {
        String gcmId = getGcmId(context);
        if (gcmId != null) {
            unregister(context, gcmId);
        }
    }

    private static void post(final Map<String, String> params) {
        DpApp.getRequestQueue().add(new ApiRequest(Method.POST, Config.GCM_SERVER_URL, null, null) {
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            };
        });
    }

    private static void delete(final Map<String, String> params) {
        StringBuffer url = new StringBuffer().append(Config.GCM_SERVER_URL).append("?");
        Iterator<String> iter = params.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            url.append("&").append(key).append("=").append(params.get(key));
        }
        DpApp.getRequestQueue().add(new ApiRequest(Method.DELETE, url.toString(), null, null));
    }
}
