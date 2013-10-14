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

import static com.dvdprime.mobile.android.util.LogUtil.LOGI;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.dvdprime.mobile.android.volley.StringRequest;

public class ViewRecommendTask {

    /** TAG */
    private final String TAG = makeLogTag(ViewRecommendTask.class);

    private static final String RECOMMEND_URL = Config.HOMEPAGE_URL + "/bbs/recommend/writeOk.asp?bbslist_id={0}";

    /**
     * The context.
     */
    private Context mContext;

    /**
     * The progress dialog before going to the browser.
     */
    private ProgressDialog mProgressDialog;

    /**
     * Constructor.
     * 
     * @param context
     */
    public ViewRecommendTask(Context context) {
        super();
        this.mContext = context;
        mProgressDialog = ProgressDialog.show(mContext, "", mContext.getString(R.string.view_recommend_message));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                DpApp.getRequestQueue().cancelAll(TAG);
            }
        });
    }

    public void execute(String bbsId) {
        LOGI(TAG, "url : " + StringUtil.format(RECOMMEND_URL, bbsId));
        StringRequest req = new StringRequest(StringUtil.format(RECOMMEND_URL, bbsId), createReqSuccessListener(), createReqErrorListener());
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
                    AndroidUtil.showToast(mContext, StringUtil.substringBetween(response, "'", "'"));
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
                AndroidUtil.showToast(mContext, mContext.getResources().getString(R.string.network_connection_fail));
            }
        };
    }
}
