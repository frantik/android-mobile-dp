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
package com.dvdprime.mobile.android.ui;

import static com.dvdprime.mobile.android.util.LogUtil.LOGD;
import static com.dvdprime.mobile.android.util.LogUtil.LOGI;
import static com.dvdprime.mobile.android.util.LogUtil.LOGW;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.flickr.Constants;
import com.dvdprime.mobile.android.task.GetOAuthTokenTask;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.google.analytics.tracking.android.EasyTracker;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

/**
 * Flickr Oauth Activity
 * 
 * @author 작은광명
 * 
 */
public class FlickrOauthActivity extends FragmentActivity {
    
    /** TAG */
    private static final String TAG = makeLogTag(FlickrOauthActivity.class);

    /** Flickr Oauth */
    private OAuth oauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        // Google Analytics
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().sendView("Oauth");
        LOGD("Tracker", "Oauth");
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        // this is very important, otherwise you would get a null Scheme in the onResume later on.
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        String scheme = getIntent().getScheme();
        // Flickr Oauth
        oauth = getOAuthToken();
        if (Constants.ID_SCHEME.equals(scheme) && (oauth == null || oauth.getUser() == null)) {
            Uri uri = getIntent().getData();
            String query = uri.getQuery();
            LOGD(TAG, "Returned Query: " + query);
            String[] data = query.split("&");
            if (data != null && data.length == 2) {
                String oauthToken = data[0].substring(data[0].indexOf("=") + 1);
                String oauthVerifier = data[1].substring(data[1].indexOf("=") + 1);
                LOGD(TAG, StringUtil.format("OAuth Token: {0}; OAuth Verifier: {1}", oauthToken, oauthVerifier));

                String secret = PrefUtil.getInstance().getString(PrefKeys.FLICKR_API_TOKEN_SECRET, null);
                if (secret != null) {
                    GetOAuthTokenTask task = new GetOAuthTokenTask(this);
                    task.execute(oauthToken, secret, oauthVerifier);
                }
            }
        }
    }

    public void onOAuthDone(OAuth result) {
        if (result == null) {
            AndroidUtil.showToast(DpApp.getContext(), getString(R.string.toast_flickr_authorization_failed_message));
        } else {
            User user = result.getUser();
            OAuthToken token = result.getToken();
            if (user == null || user.getId() == null || token == null || token.getOauthToken() == null || token.getOauthTokenSecret() == null) {
                AndroidUtil.showToast(DpApp.getContext(), getString(R.string.toast_flickr_authorization_failed_message));
                return;
            }
            AndroidUtil.showToast(DpApp.getContext(), getString(R.string.toast_flickr_authorization_success_message));
            saveOAuthToken(user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
            finish();
        }
    }

    public OAuth getOAuthToken() {
        // Restore preferences
        String oauthTokenString = PrefUtil.getInstance().getString(PrefKeys.FLICKR_API_ACCESS_TOKEN, null);
        String tokenSecret = PrefUtil.getInstance().getString(PrefKeys.FLICKR_API_TOKEN_SECRET, null);
        if (oauthTokenString == null && tokenSecret == null) {
            LOGW(TAG, "No oauth token retrieved");
            return null;
        }
        OAuth oauth = new OAuth();
        String userName = PrefUtil.getInstance().getString(PrefKeys.FLICKR_API_USER_NAME, null);
        String userId = PrefUtil.getInstance().getString(PrefKeys.FLICKR_API_USER_ID, null);
        if (userId != null) {
            User user = new User();
            user.setUsername(userName);
            user.setId(userId);
            oauth.setUser(user);
        }
        OAuthToken oauthToken = new OAuthToken();
        oauth.setToken(oauthToken);
        oauthToken.setOauthToken(oauthTokenString);
        oauthToken.setOauthTokenSecret(tokenSecret);
        LOGD(TAG, StringUtil.format("Retrieved token from preference store: oauth token={0}, and token secret={1}", oauthTokenString, tokenSecret));
        return oauth;
    }

    public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
        LOGI(TAG, StringUtil.format("Saving userName={0}, userId={1}, oauth token={2}, and token secret={3}", userName, userId, token, tokenSecret));
        PrefUtil.getInstance().setString(PrefKeys.FLICKR_API_USER_NAME, userName);
        PrefUtil.getInstance().setString(PrefKeys.FLICKR_API_USER_ID, userId);
        PrefUtil.getInstance().setString(PrefKeys.FLICKR_API_ACCESS_TOKEN, token);
        PrefUtil.getInstance().setString(PrefKeys.FLICKR_API_TOKEN_SECRET, tokenSecret);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
