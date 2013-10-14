/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dvdprime.mobile.android.gcm.command;

import static com.dvdprime.mobile.android.util.LogUtil.LOGI;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.gcm.GCMCommand;
import com.dvdprime.mobile.android.model.Gcm;
import com.dvdprime.mobile.android.ui.DocumentViewActivity;
import com.dvdprime.mobile.android.util.GsonUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.SystemUtil;

public class CommentCommand extends GCMCommand {
    private static final String TAG = makeLogTag(CommentCommand.class);

    @Override
    public void execute(Context context, String type, String extraData) {
        LOGI(TAG, "Received GCM message: " + type);
        displayNotification(context, extraData);
    }

    private void displayNotification(Context context, String message) {
        LOGI(TAG, "Displaying notification: " + message);
        Gcm gcm = GsonUtil.fromJson(message, Gcm.class);
        SystemUtil.launcherBroadcast(DpApp.class, gcm.getCount());
        String targetUrl = null;
        try {
            targetUrl = URLDecoder.decode(gcm.getTargetUrl(), Config.UTF8);
        } catch (UnsupportedEncodingException e) {
        }
        if (PrefUtil.getInstance().getBoolean(PrefKeys.NOTIFICATION_COMMENT, true) && targetUrl != null) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
            .notify(0, new NotificationCompat.Builder(context)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker(gcm.getMessage())
                    .setContentTitle(gcm.getMessage())
                    .setContentText(gcm.getTitle())
                    .setContentIntent(
                            PendingIntent.getActivity(context, 0,
                                    new Intent(context, DocumentViewActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                            .setData(Uri.parse(targetUrl))
                                            .putExtra("targetKey", gcm.getTargetKey()),
                                    0))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .build());
        }
    }

}
