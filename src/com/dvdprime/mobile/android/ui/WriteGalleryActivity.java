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
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.adapter.GalleryAdapter;
import com.google.analytics.tracking.android.EasyTracker;

public class WriteGalleryActivity extends SherlockActivity implements AdapterView.OnItemClickListener {

    /** Gallery Adapter */
    private GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_gallery);
        // 홈 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 제목 설정
        getSupportActionBar().setTitle(getString(R.string.write_gallery_title));
        getSupportActionBar().setSubtitle(getString(R.string.write_gallery_subtitle));

        // items = Building.createList(this);
        rebuildList(savedInstanceState);

        // Google Analytics
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().sendView("WriteGallery");
        LOGD("Tracker", "WriteGallery");
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

    private GridView getGridView() {
        return (GridView) findViewById(android.R.id.list);
    }

    public void onItemClick(android.widget.AdapterView<?> adapterView, View view, int position, long id) {
        adapter.setItemChecked(position, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        }
        return false;
    }

    private void rebuildList(Bundle savedInstanceState) {
        adapter = new GalleryAdapter(savedInstanceState, this);
        adapter.setOnItemClickListener(this);
        adapter.setAdapterView(getGridView());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        adapter.save(outState);
    }
}
