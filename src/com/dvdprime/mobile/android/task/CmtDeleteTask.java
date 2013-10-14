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

import static com.dvdprime.mobile.android.util.LogUtil.*;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.ui.DocumentViewActivity;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.volley.StringRequest;

public class CmtDeleteTask {
    /** TAG */
    private final String TAG = makeLogTag(CmtDeleteTask.class);

    /** DvdPrime Comment Delete URL */
    public static final String DELETE_URL = Config.HOMEPAGE_URL + "/bbs/memo/deleteOk.asp";

    /**
     * The context.
     */
    private DocumentViewActivity mActivity;

    /**
     * The progress dialog before going to the browser.
     */
    private ProgressDialog mProgressDialog;

    /**
     * Constructor.
     * 
     * @param context
     */
    public CmtDeleteTask(DocumentViewActivity activity) {
        super();
        this.mActivity = activity;
        mProgressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.cmt_deleting_message));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                DpApp.getRequestQueue().cancelAll(TAG);
            }
        });
    }

    public void execute(final String bbsId, final String cmtNo) {
        StringRequest req = new StringRequest(Method.POST, DELETE_URL, createReqSuccessListener(), createReqErrorListener()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bbslist_id", bbsId);
                params.put("bbsmemo_id", cmtNo);
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
                if (response != null && response.contains("parent.location.href")) {
                    mActivity.onRefreshStarted(null);
                    AndroidUtil.showToast(mActivity, mActivity.getResources().getString(R.string.toast_delete_comment_success_message));
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
                AndroidUtil.showToast(mActivity, mActivity.getResources().getString(R.string.network_connection_fail));
            }
        };
    }
}
