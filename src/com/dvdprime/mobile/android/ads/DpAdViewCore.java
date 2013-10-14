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
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class DpAdViewCore extends FrameLayout {
    onFailedListener failListener;

    public DpAdViewCore(Context paramContext) {
        this(paramContext, null);
    }

    public DpAdViewCore(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public void clearAdView() {}

    public void failed() {
        this.failListener.fail();
    }

    public void onDestroy() {}

    public void onPause() {}

    public void onResume() {}

    public void query() {}

    public void setOnFailedListener(onFailedListener paramonFailedListener) {
        this.failListener = paramonFailedListener;
    }

    public static abstract interface onFailedListener {
        public abstract void fail();
    }
}