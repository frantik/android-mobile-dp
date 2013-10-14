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
import static com.dvdprime.mobile.android.util.LogUtil.LOGW;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.util.HashMap;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.constants.RequestCode;
import com.dvdprime.mobile.android.task.OAuthTask;
import com.dvdprime.mobile.android.task.UploadPhotoTask;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.dvdprime.mobile.android.volley.ApiRequest;
import com.dvdprime.mobile.android.volley.StringRequest;
import com.google.analytics.tracking.android.EasyTracker;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

/**
 * Write Comment Activity
 * 
 * @author 작은광명
 * 
 */
public class WriteCommentActivity extends FragmentActivity {

    /** TAG */
    private static final String TAG = makeLogTag(WriteCommentActivity.class);

    /** Context */
    private Context mContext;

    /** Document ID */
    private String bbsId;

    /** Comment No */
    private String cmtNo;

    /** Member Id */
    private String memberId;

    /** Title */
    private String docTitle;

    /** Url */
    private String docUrl;

    /** Flickr Oauth */
    private OAuth oauth;

    /** Content EditText */
    private EditText mContentView;

    private String mContent;

    /** Image Button */
    private ImageButton mAttachButtonView;

    private View mWriteFormView;

    private View mWriteStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);
        getActionBar().setTitle(R.string.action_bar_write_comment);

        if (PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, null) == null) {
            AndroidUtil.showToast(this, getString(R.string.toast_need_login_message));
            finish();
        }

        mContext = this;
        Bundle bundle = getIntent().getExtras();
        bbsId = bundle.getString("bbsId");
        cmtNo = bundle.getString("cmtNo", "0");
        memberId = bundle.getString("memberId");
        docTitle = bundle.getString("title");
        docUrl = bundle.getString("url");

        if (bundle.getInt("depth", 0) > 0) {
            getActionBar().setTitle(R.string.action_bar_write_childcmt);
        }
        // 내용
        mContentView = (EditText) findViewById(R.id.content_editText);
        mAttachButtonView = (ImageButton) findViewById(R.id.attach_imageButton);
        mAttachButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 키보드 숨기기
                AndroidUtil.setKeyboardVisible(mContext, mContentView, false);
                // 플리커 연동이 안된경우 플리커 연동 알림
                if (PrefUtil.getInstance().getString(PrefKeys.FLICKR_API_ACCESS_TOKEN, null) == null) {
                    AndroidUtil.showConfim(mContext, getString(R.string.alert_dialog_alert), getString(R.string.flickr_auth_alert_message), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            new OAuthTask(mContext).execute();
                        }
                    });
                }
                // 플리커 연동이 되어있는 경우 갤러리로 이동
                else {
                    startActivityForResult(new Intent(WriteCommentActivity.this, WriteGalleryActivity.class), RequestCode.WRITE_GALLERY);
                }
            }
        });
        mWriteFormView = findViewById(R.id.write_form);
        mWriteStatusView = findViewById(R.id.write_status);

        // Google Analytics
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().sendView("WriteComment");
        LOGD("Tracker", "WriteComment");
    }

    @Override
    public void onResume() {
        super.onResume();

        // Flickr Oauth
        oauth = getOAuthToken();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RequestCode.WRITE_GALLERY && resultCode == RESULT_OK) {
            UploadPhotoTask taskUpload = new UploadPhotoTask(this, intent.getExtras().getString("list"));
            taskUpload.setOnUploadDone(new UploadPhotoTask.onUploadDone() {

                @Override
                public void onComplete(final String url) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mContentView.setText(mContentView.getText().toString() + "\n\n" + StringUtil.format(Config.IMAGE_TAG, url));
                        }
                    });
                }
            });

            taskUpload.execute(oauth);
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
    public void onBackPressed() {
        if (mContentView.getText().toString().length() > 0) {
            AndroidUtil.showConfim(mContext, getString(R.string.alert_dialog_ok), getString(R.string.write_exist_text_message), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
        case R.id.menu_cancel:
            EasyTracker.getTracker().sendEvent("WriteComment Menu", "Click", "Cancel", 0L);
            if (mContentView.getText().toString().length() > 0) {
                AndroidUtil.showConfim(mContext, getString(R.string.alert_dialog_ok), getString(R.string.write_exist_text_message), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            } else {
                finish();
            }
            return true;
        case R.id.menu_register:
            EasyTracker.getTracker().sendEvent("WriteComment Menu", "Click", "Register", 0L);
            attemptSend();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mWriteStatusView.setVisibility(View.VISIBLE);
            mWriteStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mWriteStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            mWriteFormView.setVisibility(View.VISIBLE);
            mWriteFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mWriteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mWriteStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mWriteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void attemptSend() {
        // Reset Error
        mContentView.setError(null);

        // Values
        mContent = mContentView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid content.
        if (TextUtils.isEmpty(mContent)) {
            mContentView.setError(getString(R.string.write_comment_content_hint));
            focusView = mContentView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);
            sendContent();
            AndroidUtil.setKeyboardVisible(mContext, mContentView, false);
        }
    }

    private void sendContent() {
        StringRequest req = new StringRequest(Method.POST, Config.WRITE_COMMENT_URL, createReqSuccessListener(), createReqErrorListener()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bbslist_id", bbsId);
                params.put("bbsmemo_id", cmtNo);
                params.put("answerContent", StringUtil.newLineToBr(mContent));
                params.put("user_status", "2");
                params.put("YN_Check", "Y");
                return params;
            };
        };
        req.setTag(TAG);
        DpApp.getRequestQueue().add(req);
    }

    private Response.Listener<String> createReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.contains("reply-area")) {
                    setResult(RESULT_OK);
                    // 성공 메시지 토스트
                    AndroidUtil.showToast(mContext, getString(R.string.toast_write_comment_success_message));
                    // API서버로 알림 정보를 전송
                    if (memberId != null) {
                        DpApp.getRequestQueue().add(new ApiRequest(Method.POST, Config.MOBILE_DP_API_NOTIFICATION_POST, null, null) {
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("ids", memberId);
                                params.put("type", "01");
                                params.put("title", docTitle);
                                params.put("message", StringUtil.curtail(mContent, 100, ".."));
                                params.put("targetUrl", docUrl);
                                params.put("targetKey", "");
                                return params;
                            };
                        });
                    }
                    finish();
                } else {
                    processFail();
                }
            }
        };
    }

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                processFail();
            }
        };
    }

    private void processFail() {
        // progress indicator 정지
        showProgress(false);
        // show fail dialog
        AndroidUtil.showAlert(mContext, getString(R.string.alert_dialog_warn), getString(R.string.write_comment_fail));
    }

}
