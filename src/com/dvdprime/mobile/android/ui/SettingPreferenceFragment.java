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
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.constants.ResultCode;
import com.dvdprime.mobile.android.dialog.CustomConfirmDialog;
import com.dvdprime.mobile.android.dialog.CustomFilterListDialog;
import com.dvdprime.mobile.android.gcm.ServerUtilities;
import com.dvdprime.mobile.android.model.Account;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.PrefUtil;

/**
 * Setting Preference Fragment
 * 
 * @author 작은광명
 * 
 */
public class SettingPreferenceFragment extends PreferenceFragment {

    /** TAG */
    private static final String TAG = makeLogTag(SettingPreferenceFragment.class.getSimpleName());

    /** Preference ActionBar */
    private ActionBar mActionbar;

    /** Preference Utility */
    private PrefUtil mPref;

    /** Account Preference */
    private Preference accountPref;

    /** Flickr Preference */
    private Preference flickrPref;

    /** Notification Preference */
    private CheckBoxPreference commentPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionbar = getActivity().getActionBar();

        if (mActionbar != null) {
            mActionbar.setTitle(getResources().getString(R.string.action_bar_setting));
        }

        addPreferencesFromResource(R.xml.settings_preference);

        // 홈버튼 생성
        mActionbar.setDisplayHomeAsUpEnabled(true);

        // 설정 유틸 초기화
        mPref = PrefUtil.getInstance();

        // 계정 설정
        accountPref = findPreference("accountInfo");
        accountPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                showLoginDialog();
                return false;
            }
        });
        if (mPref.getString(PrefKeys.ACCOUNT_ID, null) != null) {
            accountPref.setSummary(getString(R.string.pref_account_information_summary_saved, mPref.getString(PrefKeys.ACCOUNT_ID, "")));
        }

        // 플리커 설정
        flickrPref = findPreference("flickrInfo");
        flickrPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                showFlickrDialog();
                return false;
            }
        });
        if (mPref.getString(PrefKeys.FLICKR_API_USER_NAME, null) != null) {
            flickrPref.setSummary(getString(R.string.pref_flickr_information_summary_saved, mPref.getString(PrefKeys.FLICKR_API_USER_NAME, "")));
        }

        // 필터 설정
        Preference filterPref = (Preference) findPreference("filterList");
        filterPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mPref.getString(PrefKeys.ACCOUNT_ID, null) != null) {
                    if (mPref.getString(PrefKeys.FILTERS, null) != null) {
                        showFilterListDialog();
                    } else {
                        AndroidUtil.showAlert(getActivity(), getString(R.string.alert_dialog_alert), getString(R.string.filter_not_exist_message));
                    }
                } else {
                    AndroidUtil.showAlert(getActivity(), getString(R.string.alert_dialog_alert), getString(R.string.filter_not_use_message));
                }

                return false;
            }
        });
        if (mPref.getString(PrefKeys.FILTERS, null) != null) {
            filterPref.setSummary(getString(R.string.pref_filter_list_exist));
        }
        // 알림 설정
        commentPref = (CheckBoxPreference) findPreference(PrefKeys.NOTIFICATION_COMMENT);
        if (mPref.getString(PrefKeys.ACCOUNT_ID, null) == null) {
            commentPref.setEnabled(false);
        }

        // 이벤트 버스 등록
        EventBusProvider.getInstance().register(this, Account.class);

    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (mPref.getString(PrefKeys.FILTERS, null) == null) {
                    findPreference("filterList").setSummary(getString(R.string.pref_filter_list_empty));
                } else {
                    findPreference("filterList").setSummary(getString(R.string.pref_filter_list_exist));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DpApp.getRequestQueue().cancelAll(TAG);
        EventBusProvider.getInstance().unregister(this);
    }

    // Notify EventBus
    public void onEvent(final Account account) {
        if (Config.DEBUG) {
            LOGD(TAG, "login account: " + account);
        }

        // 로그인 성공
        if (account.getResultCode().equals(ResultCode.LOGIN_SUCCESS)) {
            // 인증 성공을 처리한다.
            mPref.setString(PrefKeys.ACCOUNT_ID, account.getId());
            mPref.setString(PrefKeys.ACCOUNT_PW, account.getPassword());
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    accountPref.setSummary(getString(R.string.pref_account_information_summary_saved, mPref.getString(PrefKeys.ACCOUNT_ID, "")));
                    commentPref.setEnabled(true);
                    AndroidUtil.showToast(getActivity(), getResources().getString(R.string.login_request_success_message));
                }
            });
        }
        // 로그인 정보 삭제
        else if (account.getResultCode().equals(ResultCode.LOGIN_REMOVE)) {
            ServerUtilities.unregister(getActivity(), mPref.getString(PrefKeys.ACCOUNT_ID, null), mPref.getString(PrefKeys.GCM_REG_ID, null));
            mPref.removePref(PrefKeys.ACCOUNT_ID);
            mPref.removePref(PrefKeys.ACCOUNT_PW);
            mPref.removePref(PrefKeys.FILTERS);
            mPref.removePref(PrefKeys.COOKIES);
            accountPref.setSummary(getString(R.string.pref_account_information_summary_empty));
            commentPref.setEnabled(false);
        }
        // 플리커 정보 삭제
        else if (account.getResultCode().equals(ResultCode.FLICKR_REMOVE)) {
            mPref.removePref(PrefKeys.FLICKR_API_USER_ID);
            mPref.removePref(PrefKeys.FLICKR_API_USER_NAME);
            mPref.removePref(PrefKeys.FLICKR_API_ACCESS_TOKEN);
            mPref.removePref(PrefKeys.FLICKR_API_TOKEN_SECRET);
            flickrPref.setSummary(getString(R.string.pref_flickr_information_summary_empty));
        }
        // 필터 정보 삭제
        else if (account.getResultCode().equals(ResultCode.FILTER_REMOVE)) {
            if (mPref.getString(PrefKeys.FILTERS, null) == null) {
                findPreference("filterList").setSummary(getString(R.string.pref_filter_list_empty));
            }
        }
    }

    /**
     * Creates a new instance of our dialog and displays it.
     */
    private void showLoginDialog() {
        if (mPref.getString(PrefKeys.ACCOUNT_ID, null) != null) {
            CustomConfirmDialog.newInstance(getString(R.string.alert_account_delete_message)).show(getFragmentManager(), TAG);
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    /**
     * Creates a new instance of our dialog and displays it.
     */
    private void showFlickrDialog() {
        if (mPref.getString(PrefKeys.FLICKR_API_USER_ID, null) != null) {
            CustomConfirmDialog.newInstance(getString(R.string.alert_flickr_delete_message)).show(getFragmentManager(), TAG);
        }
    }

    private void showFilterListDialog() {
        new CustomFilterListDialog(getActivity()).show();
    }

}
