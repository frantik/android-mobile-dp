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

import com.dvdprime.mobile.android.constants.Config;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.OnAdFailedListener;
import net.daum.adam.publisher.AdView.OnAdLoadedListener;
import net.daum.adam.publisher.impl.AdError;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

public class DpAdViewAdam extends DpAdViewCore {
    protected AdView ad;
    String adamID;
    protected boolean bGotAd;

    public DpAdViewAdam(Context context) {
        this(context, null);
    }

    public DpAdViewAdam(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        bGotAd = false;
        adamID = Config.ADAM_CLIENT_ID;
        initAdamView();
    }

    public void clearAdView() {
        if (ad != null) {
            removeView(ad);
            ad.destroy();
            ad = null;
        }
        super.clearAdView();
    }

    public void initAdamView() {
        ad = new AdView(getContext());

        // 할당 받은 clientId 설정
        ad.setClientId(adamID);
        // 광고 갱신 시간 : 기본 60초
        ad.setRequestInterval(20);

        int adHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 48.0, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, adHeight);
        ad.setLayoutParams(params);

        ad.setOnAdLoadedListener(new OnAdLoadedListener() {
            public void OnAdLoaded() {
                bGotAd = true;
            }
        });
        ad.setOnAdFailedListener(new OnAdFailedListener() {
            public void OnAdFailed(AdError arg0, String arg1) {
                bGotAd = true;
                failed();
            }
        });
        addView(ad);
    }

    public void onDestroy() {
        if (ad != null) {
            ad.destroy();
            ad = null;
        }
        super.onDestroy();
    }

    public void onPause() {
        if (ad != null)
            ad.pause();
        super.onPause();
    }

    public void onResume() {
        if (ad != null)
            ad.resume();
        super.onResume();
    }

    public void query() {
        bGotAd = false;
        if (ad == null)
            initAdamView();
        ad.resume();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (bGotAd)
                    return;
                if (ad != null)
                    ad.pause();
                failed();
            }
        }, 3000L);
    }
}