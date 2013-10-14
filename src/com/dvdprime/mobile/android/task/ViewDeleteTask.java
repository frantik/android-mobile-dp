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
package com.dvdprime.mobile.android.task;

import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.model.Refresh;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.ui.DocumentViewActivity;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.dvdprime.mobile.android.volley.StringRequest;

public class ViewDeleteTask {

    /** TAG */
    private final String TAG = makeLogTag(ViewDeleteTask.class);

    private static final String DELETE_URL = Config.HOMEPAGE_URL + "/bbs/deleteOk.asp";

    /**
     * The context.
     */
    private Activity mActivity;

    /**
     * The progress dialog before going to the browser.
     */
    private ProgressDialog mProgressDialog;

    /**
     * Constructor.
     * 
     * @param context
     */
    public ViewDeleteTask(Activity activity) {
        super();
        this.mActivity = activity;
        mProgressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.view_deleting_message));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                DpApp.getRequestQueue().cancelAll(TAG);
            }
        });
    }

    public void execute(final String url) {
        StringRequest req = new StringRequest(Method.POST, DELETE_URL, createReqSuccessListener(), createReqErrorListener()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("major", StringUtil.getParamValue(url, "major"));
                params.put("minor", StringUtil.getParamValue(url, "minor"));
                params.put("bbslist_id", StringUtil.getParamValue(url, "bbslist_id"));
                params.put("bbsfword_id", "");
                params.put("master_sel", "");
                params.put("fword_sel", "");
                params.put("SortMethod", "0");
                params.put("SearchConditionsss", "");
                params.put("SearchConditionTxt", "");
                params.put("returnListPageName", "mobiledp://success");
                return params;
            };
        };
        req.setTag(TAG);
        DpApp.getRequestQueue().add(req);
    }

    private void dismiss() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private Response.Listener<String> createReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                }
                dismiss();
            }
        };
    }

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismiss();
                if (error != null && error.networkResponse.statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                    EventBusProvider.getInstance().post(new Refresh(Refresh.LIST));
                    if (mActivity instanceof DocumentViewActivity) {
                        mActivity.finish();
                    }
                } else {
                    AndroidUtil.showToast(mActivity, mActivity.getResources().getString(R.string.network_connection_fail));
                }
            }
        };
    }
}
