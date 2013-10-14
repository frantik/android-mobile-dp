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
package com.dvdprime.mobile.android.ads;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.dvdprime.mobile.android.util.LogUtil;

public class DpAdManager {
    private String TAG = LogUtil.makeLogTag(DpAdManager.class);
    private DpAdViewCore adView;
    private Context mContext;

    public void bindCoreView(DpAdViewCore paramDpAdViewCore) {
        this.adView = paramDpAdViewCore;
        if (paramDpAdViewCore == null)
            return;

        final DpAdViewAdmob dpAdViewAdmob = new DpAdViewAdmob(mContext);
        dpAdViewAdmob.setOnFailedListener(new DpAdViewCore.onFailedListener() {

            @Override
            public void fail() {
                Log.i(DpAdManager.this.TAG, "adMob failed");
                if (!adView.isShown()) {
                    return;
                }
                ((DpAdViewAdmob) adView.getChildAt(0)).onDestroy();
                adView.removeViewAt(0);
                adView.setVisibility(View.GONE);
            }
        });

        final DpAdViewAdam dpAdViewAdam = new DpAdViewAdam(mContext);
        dpAdViewAdam.setOnFailedListener(new DpAdViewCore.onFailedListener() {

            @Override
            public void fail() {
                Log.i(DpAdManager.this.TAG, "adAdam failed");
                if (!adView.isShown()) {
                    return;
                }
                ((DpAdViewAdam) adView.getChildAt(0)).onDestroy();
                adView.getChildAt(0).setVisibility(View.GONE);
                adView.removeViewAt(0);
                adView.addView(dpAdViewAdmob, 0);
                DpAdViewAdmob newDpAdViewAdmob = (DpAdViewAdmob) adView.getChildAt(0);
                newDpAdViewAdmob.bringToFront();
                newDpAdViewAdmob.query();
            }
        });

        DpAdViewNaverAdPost dpAdViewNaverAdPost = new DpAdViewNaverAdPost(mContext);
        dpAdViewNaverAdPost.setOnFailedListener(new DpAdViewCore.onFailedListener() {

            @Override
            public void fail() {
                Log.i(DpAdManager.this.TAG, "adPost failed");
                if (!adView.isShown()) {
                    return;
                }
                ((DpAdViewNaverAdPost) adView.getChildAt(0)).onDestroy();
                adView.getChildAt(0).setVisibility(View.GONE);
                adView.removeViewAt(0);
                adView.addView(dpAdViewAdam, 0);
                DpAdViewAdam newDpAdViewAdam = (DpAdViewAdam) adView.getChildAt(0);
                newDpAdViewAdam.bringToFront();
                newDpAdViewAdam.query();
            }
        });

        paramDpAdViewCore.addView(dpAdViewNaverAdPost, 0);
        ((DpAdViewNaverAdPost) paramDpAdViewCore.getChildAt(0)).query();
    }

    public void onCreate(Context paramContext) {
        this.mContext = paramContext;
    }

    public void onDestroy() {
        if (adView != null) {
            if (adView.getChildAt(0) instanceof DpAdViewNaverAdPost) {
                ((DpAdViewNaverAdPost) adView.getChildAt(0)).onDestroy();
            } else if (adView.getChildAt(0) instanceof DpAdViewAdam) {
                ((DpAdViewAdam) adView.getChildAt(0)).onDestroy();
            } else if (adView.getChildAt(0) instanceof DpAdViewAdmob) {
                ((DpAdViewAdmob) adView.getChildAt(0)).onDestroy();
            }
        }
    }

    public void onPause() {
        if (adView != null) {
            if (adView.getChildAt(0) instanceof DpAdViewNaverAdPost) {
                ((DpAdViewNaverAdPost) adView.getChildAt(0)).onPause();
            } else if (adView.getChildAt(0) instanceof DpAdViewAdam) {
                ((DpAdViewAdam) adView.getChildAt(0)).onPause();
            } else if (adView.getChildAt(0) instanceof DpAdViewAdmob) {
                ((DpAdViewAdmob) adView.getChildAt(0)).onPause();
            }
        }
    }

    public void onResume() {
        if (this.adView != null) {
            if (this.adView.getChildAt(0) instanceof DpAdViewNaverAdPost) {
                ((DpAdViewNaverAdPost) this.adView.getChildAt(0)).onResume();
            } else if (this.adView.getChildAt(0) instanceof DpAdViewAdam) {
                ((DpAdViewAdam) this.adView.getChildAt(0)).onResume();
            } else if (this.adView.getChildAt(0) instanceof DpAdViewAdmob) {
                ((DpAdViewAdmob) this.adView.getChildAt(0)).onResume();
            }
        }
    }
}