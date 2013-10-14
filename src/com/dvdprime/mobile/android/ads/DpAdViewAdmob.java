/** Copyright 2013 작은광명
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

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.dvdprime.mobile.android.constants.Config;
import com.google.ads.Ad;
import com.google.ads.AdRequest.ErrorCode;

public class DpAdViewAdmob extends DpAdViewCore {

    protected com.google.ads.AdView ad;

    protected boolean bGotAd = false;

    // 여기에 ADMOB ID 를 입력하세요.
    String admobID = Config.ADMOB_KEY;

    public DpAdViewAdmob(Context context) {
        this(context, null);
    }

    public DpAdViewAdmob(Context context, AttributeSet attrs) {

        super(context, attrs);

        initAdmobView();
    }

    public void initAdmobView() {
        ad = new com.google.ads.AdView((Activity) getContext(), com.google.ads.AdSize.SMART_BANNER, admobID);

        // 광고 뷰의 위치 속성을 제어할 수 있습니다.
        // this.setGravity(Gravity.CENTER);

        ad.setAdListener(new com.google.ads.AdListener() {

            public void onDismissScreen(Ad arg0) {}

            public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
                bGotAd = true;
                failed();
            }

            public void onLeaveApplication(Ad arg0) {}

            public void onPresentScreen(Ad arg0) {}

            public void onReceiveAd(Ad arg0) {
                bGotAd = true;
            }

        });

        this.addView(ad);
    }

    private com.google.ads.AdRequest request = new com.google.ads.AdRequest();

    // 스케줄러에의해 자동으로 호출됩니다.
    // 실제로 광고를 보여주기 위하여 요청합니다.
    public void query() {
        bGotAd = false;

        if (ad == null)
            initAdmobView();

        ad.loadAd(request);

        // 3초 이상 리스너 응답이 없으면 다음 플랫폼으로 넘어갑니다.
        Handler adHandler = new Handler();
        adHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (bGotAd)
                    return;
                else {
                    failed();
                }
            }

        }, 3000);
    }

    public void onDestroy() {
        if (ad != null) {
            ad.destroy();
            ad = null;
        }

        super.onDestroy();
    }

    public void clearAdView() {
        if (ad != null) {
            this.removeView(ad);
            ad.destroy();
            ad = null;
        }

        super.clearAdView();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

}