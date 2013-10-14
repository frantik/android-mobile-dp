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
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.constants.ResultCode;
import com.dvdprime.mobile.android.gcm.ServerUtilities;
import com.dvdprime.mobile.android.model.Account;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.response.ListResponse;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.GsonUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.dvdprime.mobile.android.volley.ApiRequest;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gcm.GCMRegistrar;

/**
 * Login Acitivity
 * 
 * @author 작은광명
 * 
 */
public class LoginActivity extends Activity {
    /** TAG */
    private static final String TAG = makeLogTag(LoginActivity.class.getSimpleName());

    private Activity mActivity;

    private PrefUtil mPref;

    // Values for username and password at the time of the login attempt.
    private String mUsername;

    private String mPassword;

    // UI references.
    private EditText mUsernameView;

    private EditText mPasswordView;

    private View mLoginFormView;

    private View mLoginStatusView;

    private AsyncTask<Void, Void, Void> mGCMRegisterTask;

    private boolean isLoginSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);

        mActivity = this;
        mPref = PrefUtil.getInstance();
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // 이벤트 버스 등록
        EventBusProvider.getInstance().register(this, Account.class);
        // Google Analytics
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().sendView("Login");
        LOGD("Tracker", "Login");
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
    public void onDestroy() {
        super.onDestroy();
        DpApp.getRequestQueue().cancelAll(TAG);
        EventBusProvider.getInstance().unregister(this);

        if (mGCMRegisterTask != null) {
            mGCMRegisterTask.cancel(true);
        }

        try {
            if (!isLoginSuccess) {
                GCMRegistrar.onDestroy(this);
            }
        } catch (Exception e) {
            LOGW(TAG, "C2DM unregistration error", e);
        }
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form. If there are form errors (invalid email, missing fields, etc.), the errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.login_password_hint));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.login_username_hint));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            DpApp.requestLogin(TAG, mUsername, mPassword);
            AndroidUtil.setKeyboardVisible(mActivity, mPasswordView, false);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which
        // allow
        // for very easy animations. If available, use these APIs to
        // fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so
            // simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // Notify EventBus
    public void onEvent(final Account account) {
        if (Config.DEBUG) {
            LOGD(TAG, "login account: " + account);
        }
        // 로그인 실패
        if (account.getResultCode().equals(ResultCode.LOGIN_FAIL)) {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showProgress(false);
                    mPasswordView.setError(getString(R.string.login_request_fail_message));
                    mPasswordView.requestFocus();
                    AndroidUtil.setKeyboardVisible(mActivity, mPasswordView, true);
                }

            });
        }
        // 로그인 성공
        else if (account.getResultCode().equals(ResultCode.LOGIN_SUCCESS)) {
            // 필터 목록을 조회한다.
            ApiRequest filterReq = new ApiRequest(StringUtil.format(Config.MOBILE_DP_API_FILTER_LIST, account.getId()), createReqSuccessListener(), createReqErrorListener());
            filterReq.setTag(TAG);
            DpApp.getRequestQueue().add(filterReq);

            // GCM Regist
            GCMRegistrar.checkDevice(this);
            GCMRegistrar.checkManifest(this);
            final String regId = GCMRegistrar.getRegistrationId(this);
            if (StringUtil.isBlank(regId)) {
                // Automatically registers application on startup.
                GCMRegistrar.register(this, Config.GCM_SENDER_ID);
                isLoginSuccess = true;
            } else {
                // Device is already registered on GCM, needs to check if it is
                // registered on our server as well.
                if (ServerUtilities.isRegisteredOnServer(this)) {
                    // Skips registration.
                    LOGI(TAG, "Already registered on the C2DM server");
                    ServerUtilities.register(mActivity, account.getId(), regId);
                    isLoginSuccess = true;
                } else {
                    // Try to register again, but not in the UI thread.
                    // It's also necessary to cancel the thread onDestroy(),
                    // hence the use of AsyncTask instead of a raw thread.
                    mGCMRegisterTask = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            boolean registered = ServerUtilities.register(LoginActivity.this, regId);
                            // At this point all attempts to register with the
                            // app
                            // server failed, so we need to unregister the
                            // device
                            // from GCM - the app will try to register again
                            // when
                            // it is restarted. Note that GCM will send an
                            // unregistered callback upon completion, but
                            // GCMIntentService.onUnregistered() will ignore it.
                            if (!registered) {
                                GCMRegistrar.unregister(LoginActivity.this);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            mGCMRegisterTask = null;
                        }
                    };
                    mGCMRegisterTask.execute(null, null, null);
                }
            }
        }
    }

    private Response.Listener<String> createReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    ListResponse listRes = GsonUtil.fromJson(response, ListResponse.class);
                    if (listRes.getStatus() == 200) {
                        if (listRes.getList() == null) {
                            mPref.removePref(PrefKeys.FILTERS);
                        } else {
                            LOGI(TAG, GsonUtil.toJson(listRes.getList()));
                            mPref.setString(PrefKeys.FILTERS, GsonUtil.toJson(listRes.getList()));
                        }
                    }
                }
                finish();
            }
        };
    }

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
            }
        };
    }

}
