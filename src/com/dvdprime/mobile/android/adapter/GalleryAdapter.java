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
package com.dvdprime.mobile.android.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.multichoiceadapter.MultiChoiceBaseAdapter;
import com.dvdprime.mobile.android.ui.WriteGalleryActivity;
import com.dvdprime.mobile.android.util.ImageUtil;
import com.dvdprime.mobile.android.util.StringUtil;

public class GalleryAdapter extends MultiChoiceBaseAdapter {
    private final String[] IMAGE_PROJECTION = { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.SIZE };
    // private final String TAG = makeLogTag(GalleryAdapter.class.getSimpleName());
    private WriteGalleryActivity mActivity;
    private List<String> thumbsDataList;
    private List<String> thumbsIDList;
    private List<Integer> thumbsOrientationList;
    private final Uri uriImages = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public GalleryAdapter(Bundle bundle, WriteGalleryActivity activity) {
        super(bundle);
        this.mActivity = activity;
        this.thumbsIDList = new ArrayList<String>();
        this.thumbsDataList = new ArrayList<String>();
        this.thumbsOrientationList = new ArrayList<Integer>();
        Cursor cursor = this.mActivity.getContentResolver().query(this.uriImages, IMAGE_PROJECTION, null, null, "date_added desc");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String thumbsId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                String thumbData = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                String thumbName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                int thumbOri = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));

                if (StringUtil.isNotBlank(thumbName)) {
                    this.thumbsIDList.add(thumbsId);
                    this.thumbsDataList.add(thumbData);
                    this.thumbsOrientationList.add(thumbOri);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void uploadSelectedItems() {
        ArrayList<String> arrayList = new ArrayList<String>();
        Iterator<Long> iter = getCheckedItems().iterator();
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                arrayList.add(getThumbsDataList().get((int) iter.next().longValue()));
            }
        }

        if (!arrayList.isEmpty()) {
            Intent i = new Intent();
            i.putExtra("list", StringUtil.join(arrayList, ","));
            mActivity.setResult(Activity.RESULT_OK, i);
        }
        mActivity.finish();
    }

    @Override
    public int getCount() {
        if (this.thumbsIDList != null)
            return this.thumbsIDList.size();
        return 0;
    }

    @Override
    public Object getItem(int paramInt) {
        return Integer.valueOf(paramInt);
    }

    @Override
    public long getItemId(int paramInt) {
        return paramInt;
    }

    public List<String> getThumbsDataList() {
        return this.thumbsDataList;
    }

    @Override
    protected View getViewImpl(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.gallery_item, viewGroup, false);
        }
        final ImageView imageView = (ImageView) view;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inSampleSize = 3;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                imageView.setImageBitmap(ImageUtil.rotate(MediaStore.Images.Thumbnails.getThumbnail(mActivity.getContentResolver(), Integer.parseInt((String) thumbsIDList.get(position)), 3, options), thumbsOrientationList.get(position)));
            }
        }, 100L);
        return imageView;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_upload) {
            uploadSelectedItems();
            finishActionMode();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu) {
        return false;
    }
}