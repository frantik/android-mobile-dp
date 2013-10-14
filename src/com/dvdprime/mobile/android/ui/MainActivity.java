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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.adapter.AppSectionsPagerAdapter;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.dvdprime.mobile.android.util.SystemUtil;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Main Activity
 * 
 * @author 작은광명
 * 
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /** TAG */
    private static final String TAG = makeLogTag(MainActivity.class.getSimpleName());

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If this becomes too memory intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public static AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * Notification Badge Count
     */
    private Button badgeCount;

    /**
     * Saved Last Tab Position
     */
    private int lastPosition = 0;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a time.
     */
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the Tab.
                actionBar.setSelectedNavigationItem(position);
                EasyTracker.getTracker().sendView(AppSectionsPagerAdapter.tabEngTitles[position]);
                LOGD("Tracker", AppSectionsPagerAdapter.tabEngTitles[position]);
                // Save position value in preference
                PrefUtil.getInstance().setInt(PrefKeys.LAST_TAB_POSITION, position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the listener for when this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mAppSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

        // Google Analytics
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().sendView("Main");
        LOGD("Tracker", "Main");

        // Set Last Tab
        lastPosition = PrefUtil.getInstance().getInt(PrefKeys.LAST_TAB_POSITION, 0);
        actionBar.setSelectedNavigationItem(lastPosition);

        // 카운트를 조회한다.
        if (PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, null) != null) {
            JsonObjectRequest filterReq = new JsonObjectRequest(StringUtil.format(Config.MOBILE_DP_COUNT, PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, null)), null, createJsonReqSuccessListener(), createJsonReqErrorListener());
            filterReq.setTag(TAG);
            DpApp.getRequestQueue().add(filterReq);
        }

        // 버전 알림 다이얼로그
        if (SystemUtil.getVersionName(this).equals("1.0") && PrefUtil.getInstance().getBoolean(PrefKeys.VERSION_1_0, true)) {
            String msg = new StringBuffer().append("[1.0 버전]\n\n")
                    .append("-즐겨찾기 탭 추가 \n  게시판 목록의 별을 터치하시면 즐겨찾기 목록에 추가/삭제됩니다.\n\n")
                    .append("-푸시 알림 기능 추가\n  안드로이드 앱으로 댓글을 남기면 푸시 메시지가 전송됩니다. 이를 위해서 재로그인 부탁드립니다.")
                    .toString();
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_dialog_version))
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrefUtil.getInstance().setBoolean(PrefKeys.VERSION_1_0, false);
                        dialog.dismiss();
                    }
                })
                .create().show();
        }
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
        DpApp.getRequestQueue().cancelAll(TAG);
        super.onDestroy();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        View count = menu.findItem(R.id.menu_badge).getActionView();
        badgeCount = (Button) count.findViewById(R.id.badge_count);
        badgeCount.setText(String.valueOf(0));
        badgeCount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationListActivity.class));
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_setting) {
            EasyTracker.getTracker().sendEvent("Main Menu", "Click", "Setting", 0L);
            Intent i = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(i);
            return true;
        }

        if (itemId == R.id.menu_about) {
            EasyTracker.getTracker().sendEvent("Main Menu", "Click", "About", 0L);
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Response.Listener<JSONObject> createJsonReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 200) {
                        int unreadCnt = response.getJSONObject("data").getInt("notification");
                        if (unreadCnt > 99) {
                            badgeCount.setText("99+");
                        } else {
                            badgeCount.setText(String.valueOf(unreadCnt));
                        }
                    }
                } catch (JSONException e) {
                }
            }
        };
    }

    private Response.ErrorListener createJsonReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        };
    }
}
