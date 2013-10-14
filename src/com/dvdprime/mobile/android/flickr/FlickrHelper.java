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
package com.dvdprime.mobile.android.flickr;

import javax.xml.parsers.ParserConfigurationException;

import com.dvdprime.mobile.android.constants.Config;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.galleries.GalleriesInterface;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

public final class FlickrHelper {

    private static FlickrHelper instance = null;

    private FlickrHelper() {

    }

    public static FlickrHelper getInstance() {
        if (instance == null) {
            instance = new FlickrHelper();
        }

        return instance;
    }

    public Flickr getFlickr() {
        try {
            Flickr f = new Flickr(Config.FLICKR_API_KEY, Config.FLICKR_API_SEC, new REST());
            return f;
        } catch (ParserConfigurationException e) {
            return null;
        }
    }

    public Flickr getFlickrAuthed(String token, String secret) {
        Flickr f = getFlickr();
        RequestContext requestContext = RequestContext.getRequestContext();
        OAuth auth = new OAuth();
        auth.setToken(new OAuthToken(token, secret));
        requestContext.setOAuth(auth);
        return f;
    }

    public InterestingnessInterface getInterestingInterface() {
        Flickr f = getFlickr();
        if (f != null) {
            return f.getInterestingnessInterface();
        } else {
            return null;
        }
    }

    public PhotosInterface getPhotosInterface() {
        Flickr f = getFlickr();
        if (f != null) {
            return f.getPhotosInterface();
        } else {
            return null;
        }
    }

    public GalleriesInterface getGalleryInterface() {
        Flickr f = getFlickr();
        if (f != null) {
            return f.getGalleriesInterface();
        } else {
            return null;
        }
    }

}
