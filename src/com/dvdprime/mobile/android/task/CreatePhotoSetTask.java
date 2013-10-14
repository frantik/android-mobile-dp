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

import static com.dvdprime.mobile.android.util.LogUtil.LOGW;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;
import android.os.AsyncTask;

import com.dvdprime.mobile.android.flickr.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

public class CreatePhotoSetTask extends AsyncTask<String, Integer, String> {
    
    private String TAG = makeLogTag(CreatePhotoSetTask.class);

    private String mToken, mTokenSecret;

    private IPhotoSetCreationListener mListener;

    public CreatePhotoSetTask(String token, String secret, IPhotoSetCreationListener listener) {
        this.mToken = token;
        this.mTokenSecret = secret;
        this.mListener = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(String... params) {
        if (params.length < 2) {
            throw new IllegalArgumentException("CreatePhotoSetTask, parameter should be [title, primary photo id, [description]."); //$NON-NLS-1$
        }
        String title = params[0];
        String primaryPhotoId = params[1];
        String description = title;
        if (params.length > 2) {
            description = params[2];
        }

        Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken, mTokenSecret);
        PhotosetsInterface pi = f.getPhotosetsInterface();
        try {
            Photoset ps = pi.create(title, description, primaryPhotoId);
            return ps.getId();
        } catch (Exception ee) {
            LOGW(TAG, ee.getMessage());
            return "error: " + ee.getMessage();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        boolean fail = result.startsWith("error");
        if (mListener != null) {
            mListener.onPhotoSetCreated(!fail, result);
        }
    }

    public interface IPhotoSetCreationListener {
        /**
         * 
         * @param success
         *            <code>true</code> says success, in this case, <code>msg</code> is the photo set id which is just created; <code>false</code> says failure, <code>msg</code> in this case, is the error message.
         * @param msg
         */
        void onPhotoSetCreated(boolean success, String msg);
    }

}
