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

import static com.dvdprime.mobile.android.util.LogUtil.LOGD;
import static com.dvdprime.mobile.android.util.LogUtil.LOGE;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.flickr.Constants;
import com.dvdprime.mobile.android.flickr.FlickrHelper;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

public class OAuthTask extends AsyncTask<Void, Integer, String> {

    private String TAG = makeLogTag(OAuthToken.class);

    private static final Uri OAUTH_CALLBACK_URI = Uri.parse(Constants.ID_SCHEME + "://oauth");

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
    public OAuthTask(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mContext, "", mContext.getString(R.string.write_flickr_auth_request));
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                OAuthTask.this.cancel(true);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            Flickr f = FlickrHelper.getInstance().getFlickr();
            OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(OAUTH_CALLBACK_URI.toString());
            saveTokenSecrent(oauthToken.getOauthTokenSecret());
            URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(Permission.WRITE, oauthToken);
            return oauthUrl.toString();
        } catch (Exception e) {
            LOGE(TAG, "Error to oauth", e);
            return "error:" + e.getMessage();
        }
    }

    /**
     * Saves the oauth token secrent.
     * 
     * @param tokenSecret
     */
    private void saveTokenSecrent(String tokenSecret) {
        LOGD(TAG, "request token: " + tokenSecret);
        PrefUtil.getInstance().setString(PrefKeys.FLICKR_API_TOKEN_SECRET, tokenSecret);
        LOGD(TAG, "oauth token secrent saved: " + tokenSecret);
    }

    @Override
    protected void onPostExecute(String result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (result != null && !result.startsWith("error")) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result)));
        } else {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }
    }

}
