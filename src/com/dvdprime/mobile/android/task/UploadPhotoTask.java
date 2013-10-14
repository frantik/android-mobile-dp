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
package com.dvdprime.mobile.android.task;

import static com.dvdprime.mobile.android.util.LogUtil.LOGD;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.flickr.FlickrHelper;
import com.dvdprime.mobile.android.util.ImageUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;

public class UploadPhotoTask extends AsyncTask<OAuth, Void, Void> {
    
    /** TAG */
    private String TAG = makeLogTag(UploadPhotoTask.class);

    private final Activity mActivity;

    private String[] paths;

    private String progressMessage = "사진을 업로드 중... ({0}/{1})";

    public UploadPhotoTask(Activity activity, String path) {
        this.mActivity = activity;
        this.paths = StringUtil.split(path, ",");
    }

    /**
     * The progress dialog before going to the browser.
     */
    private ProgressDialog mProgressDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mActivity, "", StringUtil.format(progressMessage, 1, paths.length));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                UploadPhotoTask.this.cancel(true);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(OAuth... params) {
        OAuth oauth = params[0];
        final OAuthToken token = oauth.getToken();

        try {
            Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());

            for (int i = 0; i < paths.length; i++) {
                final int now = i + 1;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMessage(StringUtil.format(progressMessage, now, paths.length));
                    }
                });
                File file = new File(paths[i]);
                UploadMetaData uploadMetaData = new UploadMetaData();
                uploadMetaData.setAsync(false);
                uploadMetaData.setPublicFlag(true);
                uploadMetaData.setTitle("" + file.getName());
                String photoId = f.getUploader().upload(file.getName(), new FileInputStream(file), uploadMetaData);

                if (monUploadDone != null) {
                    Photo p = f.getPhotosInterface().getInfo(photoId, PrefUtil.getInstance().getString(PrefKeys.FLICKR_API_TOKEN_SECRET, ""));
                    int width = p.getRotation() % 180 == 90 ? ImageUtil.getBitmapOfHeight(file.getAbsolutePath()) : ImageUtil.getBitmapOfWidth(file.getAbsolutePath());
                    int height = p.getRotation() % 180 == 90 ? ImageUtil.getBitmapOfWidth(file.getAbsolutePath()) : ImageUtil.getBitmapOfHeight(file.getAbsolutePath());
                    LOGD(TAG, "Image size: width = " + width + ", height = " + height + ", rotation = " + p.getRotation());
                    if (p.getOriginalFormat().equals("gif") || height > (width * 3)) {
                        monUploadDone.onComplete(p.getOriginalUrl());
                    } else {
                        if (height > width) {
                            monUploadDone.onComplete(StringUtil.replace(p.getLargeUrl(), "_b", "_c"));
                        } else {
                            monUploadDone.onComplete(StringUtil.replace(p.getLargeUrl(), "_b", "_z"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("boom!!", "" + e.toString());
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Void v) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

    }

    onUploadDone monUploadDone;

    public void setOnUploadDone(onUploadDone monUploadDone) {
        this.monUploadDone = monUploadDone;
    }

    public interface onUploadDone {
        void onComplete(String url);
    }

}