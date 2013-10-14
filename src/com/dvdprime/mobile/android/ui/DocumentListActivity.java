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
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Document List Activity
 * 
 * @author 작은광명
 * 
 */
public class DocumentListActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_document_list);
        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);

        Bundle extras = getIntent().getExtras();

        // 제목 설정
        getSupportActionBar().setTitle(extras.getString("title"));
        // 홈버튼 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Google Analytics
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().sendView("DocList");
        LOGD("Tracker", "DocList");
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
    protected void onDestroy() {
        if (isFinishing()) {
            DpApp.setDocumentClear();
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
    }

}
