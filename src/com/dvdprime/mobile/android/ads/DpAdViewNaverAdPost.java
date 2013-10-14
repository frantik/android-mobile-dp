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
import android.os.Handler;
import android.util.AttributeSet;

import com.dvdprime.mobile.android.constants.Config;
import com.nbpcorp.mobilead.sdk.MobileAdListener;
import com.nbpcorp.mobilead.sdk.MobileAdView;

public class DpAdViewNaverAdPost extends DpAdViewCore {
    protected static boolean bGotAd = false;
    protected MobileAdView ad;
    String naverAdPostKey;

    public DpAdViewNaverAdPost(Context context) {
        this(context, null);
        initAdpostView();
    }

    public DpAdViewNaverAdPost(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.naverAdPostKey = Config.ADPOST_KEY;
    }

    public void clearAdView() {
        if (ad != null) {
            removeView(ad);
            ad.stop();
            ad.destroy();
            ad = null;
        }
        super.clearAdView();
    }

    public void initAdpostView() {
        ad = new MobileAdView(getContext());
        ad.setChannelID(naverAdPostKey);
        ad.setTest(false);

        LayoutParams l = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ad.setLayoutParams(l);

        ad.setListener(new MobileAdListener() {
            public void onReceive(int arg0) {
                // 광고 수신 성공인 경우나, 검수중인 경우
                if ((arg0 == 0) || (arg0 == 104) || (arg0 == 101) || (arg0 == 102) || (arg0 == 106)) {
                    bGotAd = true;
                    return;
                }
                failed();
            }
        });

        addView(ad);
    }

    public void onDestroy() {
        if (this.ad != null) {
            this.ad.stop();
            this.ad.destroy();
            this.ad = null;
        }
        super.onDestroy();
    }

    public void onPause() {
        if (this.ad != null)
            this.ad.stop();
        super.onPause();
    }

    public void onResume() {
        if (this.ad != null) {
            removeView(this.ad);
            this.ad.stop();
            this.ad.destroy();
            this.ad = null;
            initAdpostView();
            this.ad.start();
        }
        super.onResume();
    }

    public void query() {
        if (ad == null)
            initAdpostView();

        ad.start();

        if (bGotAd)
            return;

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (bGotAd)
                    return;
                if (ad != null)
                    ad.stop();
                failed();
            }
        }, 3000L);
    }
}