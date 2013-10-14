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

import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.dvdprime.mobile.android.R;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Setting Activity
 * 
 * @author 작은광명
 * 
 */
public class SettingActivity extends SherlockPreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        try {
            if (getResources().getIdentifier("settings_header", "xml", getPackageName()) <= 0)
                return;

            loadHeadersFromResource(R.xml.settings_header, target);
        } catch (Exception e) {
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light);
        super.onCreate(savedInstanceState);

        if (!hasHeaders()) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingPreferenceFragment()).commit();
        }

        // Google Analytics
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().sendView("Setting");
        LOGD("Tracker", "Setting");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            EasyTracker.getTracker().sendEvent("Setting Menu", "Click", "Home", 0L);
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
