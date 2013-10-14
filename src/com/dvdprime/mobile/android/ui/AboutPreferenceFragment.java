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

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.SystemUtil;

/**
 * About Preference Fragment
 * 
 * @author 작은광명
 * 
 */
public class AboutPreferenceFragment extends PreferenceFragment {

    private ActionBar actionbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionbar = getActivity().getActionBar();
        if (actionbar != null) {
            actionbar.setTitle(getResources().getString(R.string.action_bar_about));
        }

        addPreferencesFromResource(R.xml.about_preference);

        // 홈버튼 생성
        actionbar.setDisplayHomeAsUpEnabled(true);

        // 버전 세팅
        Preference prefVersion = (Preference) findPreference("aboutVersion");
        prefVersion.setSummary(SystemUtil.getVersionName(getActivity()));

        // 이메일 클릭 리스너
        Preference prefEmail = (Preference) findPreference("aboutEmail");
        prefEmail.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                AndroidUtil.clickedLinkAction(getActivity(), "frantik@gmail.com");

                return false;
            }
        });
    }

}
