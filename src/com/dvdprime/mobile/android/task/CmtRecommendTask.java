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

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.LogUtil;
import com.dvdprime.mobile.android.volley.StringRequest;

public class CmtRecommendTask {

    private static final String RECOMMEND_URL = "http://dvdprime.donga.com/bbs/recommend/RwriteOk.asp";

    private final String TAG = LogUtil.makeLogTag(CmtRecommendTask.class);

    private Context mContext;

    private ProgressDialog mProgressDialog;

    public CmtRecommendTask(Context paramContext) {
        this.mContext = paramContext;
        this.mProgressDialog = ProgressDialog.show(mContext, "", mContext.getString(R.string.cmt_recommend_message));
        this.mProgressDialog.setCanceledOnTouchOutside(false);
        this.mProgressDialog.setCancelable(true);
        this.mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface paramDialogInterface) {
                DpApp.getRequestQueue().cancelAll(TAG);
            }
        });
    }

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError paramVolleyError) {
                dismiss();
                AndroidUtil.showToast(mContext, mContext.getResources().getString(R.string.network_connection_fail));
            }
        };
    }

    private Response.Listener<String> createReqSuccessListener() {
        return new Response.Listener<String>() {
            public void onResponse(String result) {
                if (result != null) {
                    AndroidUtil.showToast(mContext, mContext.getString(R.string.toast_view_recommend_success_message));
                }
                dismiss();
            }
        };
    }

    private void dismiss() {
        if (this.mProgressDialog == null)
            return;
        this.mProgressDialog.dismiss();
    }

    public void execute(final String cmtNo) {
        StringRequest req = new StringRequest(Method.POST, RECOMMEND_URL, createReqSuccessListener(), createReqErrorListener()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bbsmemo_id", cmtNo);
                params.put("regI", "0");
                return params;
            };
        };
        req.setTag(TAG);
        DpApp.getRequestQueue().add(req);
    }
}