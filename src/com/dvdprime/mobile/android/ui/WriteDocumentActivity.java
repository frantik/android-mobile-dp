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

import static com.dvdprime.mobile.android.util.LogUtil.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.RequestCode;
import com.dvdprime.mobile.android.database.DBAdapter;
import com.dvdprime.mobile.android.model.Bbs;
import com.dvdprime.mobile.android.model.Refresh;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.task.OAuthTask;
import com.dvdprime.mobile.android.task.UploadPhotoTask;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.LogUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.dvdprime.mobile.android.volley.MultipartRequest;
import com.google.analytics.tracking.android.EasyTracker;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

public class WriteDocumentActivity extends FragmentActivity {
    /** TAG */
    private static final String TAG = LogUtil.makeLogTag(WriteDocumentActivity.class);

    private String bbsId;
    private String date;
    private boolean isModify = false;

    private ImageButton mAttachButtonView;
    private List<Bbs> mBbsList;
    private Spinner mCategory;
    private ArrayAdapter<String> mCategoryAdapter;

    private int mSelectionIndex = -1;

    private String mTitle;
    private String mContent;
    private String mTag;

    private Context mContext;
    private EditText mTitleView;
    private EditText mContentView;
    private EditText mTagView;
    private View mWriteFormView;
    private View mWriteStatusView;

    private OAuth oauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_document);
        getActionBar().setTitle(R.string.action_bar_write_document);

        // Checking Authorization
        if (PrefUtil.getInstance().getString("account_id", null) == null) {
            AndroidUtil.showToast(this, getString(R.string.toast_need_login_message));
            finish();
        }
        mContext = this;

        // Initialize Parameters
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        String title = bundle.getString("title", null);
        String content = bundle.getString("content", null);
        String tag = bundle.getString("tag", null);
        bbsId = bundle.getString("bbsId", "");
        date = bundle.getString("date", "");
        mBbsList = DBAdapter.getInstance().selectBbsList(-1);

        ArrayList<String> categoryList = new ArrayList<String>();
        Iterator<Bbs> iter = mBbsList.iterator();
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                Bbs bbs = (Bbs) iter.next();
                if (StringUtil.equals(id, bbs.getUniqId())) {
                    mSelectionIndex = mBbsList.indexOf(bbs);
                }
                categoryList.add("[" + bbs.getGroupTitle() + "] " + bbs.getTitle());
            }
        }

        mCategory = ((Spinner) findViewById(R.id.bbs_category_spinner));
        mCategoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryList);
        mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(mCategoryAdapter);
        mCategory.setSelection(mSelectionIndex);
        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {}

            public void onNothingSelected(AdapterView<?> paramAdapterView) {}
        });
        mCategory.setVisibility(View.GONE);
        mTitleView = ((EditText) findViewById(R.id.title_editText));
        mContentView = ((EditText) findViewById(R.id.content_editText));
        mTagView = ((EditText) findViewById(R.id.tag_editText));
        mTagView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int which, KeyEvent event) {
                if (which == EditorInfo.IME_ACTION_DONE) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });
        if (title != null) {
            isModify = true;
            mTitleView.setText(title);
            mContentView.setText(content);
            mTagView.setText(tag);
            getActionBar().setTitle(getString(R.string.view_menu_modify));
        }
        mAttachButtonView = ((ImageButton) findViewById(R.id.attach_imageButton));
        mAttachButtonView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AndroidUtil.setKeyboardVisible(mContext, mContentView, false);
                if (PrefUtil.getInstance().getString("flickr_access_token", null) == null) {
                    AndroidUtil.showConfim(mContext, getString(R.string.alert_dialog_alert), getString(R.string.flickr_auth_alert_message), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            new OAuthTask(mContext).execute(new Void[0]);
                        }
                    });
                    return;
                }
                startActivityForResult(new Intent(WriteDocumentActivity.this, WriteGalleryActivity.class), RequestCode.WRITE_GALLERY);
            }
        });
        mWriteFormView = findViewById(R.id.write_form);
        mWriteStatusView = findViewById(R.id.write_status);

        // Initialize Google Analytics
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().sendView("WriteDocument");
        LOGD("Tracker", "WriteDocument");
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

    @Override
    public void onBackPressed() {
        if (mTitleView.getText().toString().length() > 0 || mContentView.getText().toString().length() > 0) {
            AndroidUtil.showConfim(mContext, getString(R.string.alert_dialog_ok), getString(R.string.write_exist_text_message), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration paramConfiguration) {
        super.onConfigurationChanged(paramConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        case R.id.menu_cancel:
            EasyTracker.getTracker().sendEvent("WriteDocument Menu", "Click", "Cancel", Long.valueOf(0L));
            if (mTitleView.getText().toString().length() > 0 || mContentView.getText().toString().length() > 0) {
                AndroidUtil.showConfim(mContext, getString(R.string.alert_dialog_ok), getString(R.string.write_exist_text_message), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                return true;
            }
            finish();
            return true;
        case R.id.menu_register:
            EasyTracker.getTracker().sendEvent("WriteDocument Menu", "Click", "Register", Long.valueOf(0L));
            attemptSend();
            return true;
        default:
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.oauth = getOAuthToken();
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

    private void attemptSend() {
        // Reset Error
        mTitleView.setError(null);
        mContentView.setError(null);
        mTagView.setError(null);

        // Values
        mTitle = mTitleView.getText().toString();
        mContent = mContentView.getText().toString();
        String[] tags = StringUtil.split(mTagView.getText().toString(), ",");
        if (tags != null && tags.length > 0) {
            List<String> tagList = new ArrayList<String>();
            for (int i = 0; i < (tags.length < 5 ? tags.length : 5); i++) {
                tagList.add(StringUtil.curtail(tags[i], 10, ""));
            }
            mTag = StringUtil.join(tagList, ",");
        } else {
            mTag = "DPApp";
        }

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mTitle)) {
            mTitleView.setError(getString(R.string.write_title_hint));
            focusView = mTitleView;
            cancel = true;
        } else if (mTitle.length() > 100) {
            mTitleView.setError(getString(R.string.write_title_limit));
            focusView = mTitleView;
            cancel = true;
        }
        if (TextUtils.isEmpty(mContent)) {
            mContentView.setError(getString(R.string.write_content_hint));
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

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError paramVolleyError) {
                if (paramVolleyError.networkResponse.statusCode == 302) {
                    if (isModify) {
                        setResult(Activity.RESULT_OK);
                    }
                    AndroidUtil.showToast(mContext, getString(R.string.toast_write_document_success_message));
                    EventBusProvider.getInstance().post(new Refresh(Refresh.LIST));
                    finish();
                } else {
                    processFail();
                }
            }
        };
    }

    private Response.Listener<String> createReqSuccessListener() {
        return new Response.Listener<String>() {
            public void onResponse(String response) {
                if (response == null)
                    return;
                processFail();
            }
        };
    }

    private void processFail() {
        // progress indicator 정지
        showProgress(false);
        // show fail dialog
        AndroidUtil.showAlert(mContext, getString(R.string.alert_dialog_warn), getString(R.string.write_document_fail));
    }

    private void sendContent() {
        Bbs bbs = (Bbs) mBbsList.get(mSelectionIndex);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("returnUrl", "mobiledp://success");
        params.put("major", bbs.getMajor());
        params.put("minor", bbs.getMinor());
        params.put("bbsReleaseWriteMode", "0");
        params.put("editormode", "1");
        params.put("userid", PrefUtil.getInstance().getString("account_id", ""));
        params.put("master_id", bbs.getMasterId());
        params.put("notice_yn", "");
        params.put("hot_yn", "");
        params.put("cool_yn", "");
        params.put("custom_tag", mTag);
        params.put("useBR", "");
        params.put("short_content", "");
        params.put("fword", getResources().getString(R.string.write_enter_tag_text));
        params.put("subject", mTitle);
        params.put("content", StringUtil.newLineToBr(mContent));

        if (isModify) {
            params.put("bbslist_id", bbsId);
            params.put("reg_date", date);
            params.put("writemode", "1");
        } else {
            params.put("bbslist_id", "0");
            params.put("reg_date", "");
            params.put("writemode", "0");
        }
        
        String url = isModify ? Config.MODIFY_URL : Config.WRITE_URL;

        MultipartRequest req = new MultipartRequest(url, createReqSuccessListener(), createReqErrorListener(), params);
        req.setTag(TAG);
        DpApp.getRequestQueue().add(req);
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

    public OAuth getOAuthToken() {
        String str1 = PrefUtil.getInstance().getString("flickr_access_token", null);
        String str2 = PrefUtil.getInstance().getString("flickr_token_secret", null);
        if ((str1 == null) && (str2 == null)) {
            LogUtil.LOGW(TAG, "No oauth token retrieved");
            return null;
        }
        OAuth localOAuth = new OAuth();
        String str3 = PrefUtil.getInstance().getString("flickr_user_name", null);
        String str4 = PrefUtil.getInstance().getString("flickr_user_id", null);
        if (str4 != null) {
            User localUser = new User();
            localUser.setUsername(str3);
            localUser.setId(str4);
            localOAuth.setUser(localUser);
        }
        OAuthToken localOAuthToken = new OAuthToken();
        localOAuth.setToken(localOAuthToken);
        localOAuthToken.setOauthToken(str1);
        localOAuthToken.setOauthTokenSecret(str2);
        LogUtil.LOGD(TAG, StringUtil.format("Retrieved token from preference store: oauth token={0}, and token secret={1}", new Object[] { str1, str2 }));
        return localOAuth;
    }

}