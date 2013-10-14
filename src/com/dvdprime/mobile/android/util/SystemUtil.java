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
package com.dvdprime.mobile.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.constants.Config;

/**
 * System Util
 * 
 * @author 작은광명
 * 
 */
public class SystemUtil {

    /**
     * 특정 파일을 미디어 스캐닝
     */
    public static class MediaScannerNotifier implements MediaScannerConnectionClient {
        @SuppressWarnings("unused")
        private Context mContext;

        private MediaScannerConnection mConnection;

        private String mPath;

        private String mMimeType;

        public MediaScannerNotifier(Context context, String path, String mimeType) {
            mContext = context;
            mPath = path;
            mMimeType = mimeType;
            mConnection = new MediaScannerConnection(context, this);
            mConnection.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mConnection.scanFile(mPath, mMimeType);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            // OPTIONAL: scan is complete, this will cause the viewer to render
            // it
            try {
                /*
                 * if (uri != null) { Intent intent = new Intent(Intent.ACTION_VIEW); intent.setData(uri); mContext.startActivity(intent); }
                 */
            } finally {
                mConnection.disconnect();
                mContext = null;
            }
        }
    }

    /**
     * 현재 네트워크 사용 가능 여부
     * 
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null)
            return false;
        else {
            final NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
    }

    /**
     * 외장 메모리 쓰기 가능 여부
     * 
     * @return
     */
    public static boolean isExternalWritable() {
        File sdDir = new File(Config.SDCARD_DIRECTORY);
        return (sdDir.exists() && sdDir.canWrite());
    }

    /**
     * 런처 아이콘에 배지 카운트 브로드캐시트
     * 
     * @param activity
     *            패키지명 추출
     * @param badgeCnt
     *            배지 카운트
     */
    public static void launcherBroadcast(Class<?> clazz, int badgeCnt) {
        String packageName = null;
        String className = null;

        if (clazz != null) {
            packageName = clazz.getPackage().getName();
            className = packageName + ".ui.MainActivity";
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", badgeCnt); // 실제 업데이트 하려는 카운트 개수
            // 메인메뉴에 나타나는 어플의패키지명
            intent.putExtra("badge_count_package_name", packageName);
            // 메인메뉴에 나타나는 어플의클래스명
            intent.putExtra("badge_count_class_name", className);
            DpApp.getContext().sendBroadcast(intent);
        }
    }

    /**
     * 캐쉬 경로를 반환한다.
     * 
     * @param mContext
     * @return
     */
    public static File getCacheDir(Context mContext) {
        File cacheDir;

        if (isExternalWritable()) {
            cacheDir = Config.CACHE_DIR;
            if (!cacheDir.exists())
                cacheDir.mkdirs();
        } else {
            cacheDir = mContext.getCacheDir();
        }

        return cacheDir;
    }

    /**
     * Clipboard에 선택한 텍스트 복사
     * 
     * @param mContext
     * @param s
     */
    public static void copyToClipboard(Context mContext, String s) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("DP", s);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mContext, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Clipboard에 저장된 텍스트 가져오기
     * 
     * @param mContext
     * @return
     */
    public static String pasteFromClipboard(Context mContext) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard.hasPrimaryClip()) {
            return clipboard.getPrimaryClip().getDescription().toString();
        } else {
            return "";
        }
    }

    /**
     * 현재 실행중인 프로세스 패키지 목록
     * 
     * @param mContext
     * @return
     */
    public static List<String> getRunningActivity(Context mContext) {
        List<String> activePackage = new ArrayList<String>();

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> proceses = am.getRunningAppProcesses();

        // 프로세서 전체를 반복
        for (RunningAppProcessInfo process : proceses) {
            if (process.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackage.add(process.processName); // package이름과 동일함.
            }
        }
        return activePackage;
    }

    /**
     * 현재 화면에 보여지는 최상위 액티비티 클래스명
     * 
     * @param mContext
     * @return
     */
    public static String getRunningTopActivity(Context mContext) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = am.getRunningTasks(1);
        ComponentName topActivity = task.get(0).topActivity;

        return topActivity.getClassName();
    }

    /**
     * .zip 압축 해제 (소스상 현재는 폰트 전용으로 구현되어 있음)
     * 
     * @param unZipFile
     * @throws IOException
     */
    public static void unZip(File unZipFile) throws IOException {
        ZipInputStream in = new ZipInputStream(new FileInputStream(unZipFile));
        ZipEntry ze;

        try {
            if (unZipFile.exists()) {
                while ((ze = in.getNextEntry()) != null) {
                    String path = unZipFile.getAbsolutePath();
                    path = StringUtil.replace(path, ".zip", ".ttf");

                    if (ze.getName().indexOf("/") != -1) {
                        File parent = new File(path).getParentFile();
                        if (!parent.exists()) {
                            if (!parent.mkdirs())
                                throw new IOException("Unable to create folder" + parent);
                        }
                    }

                    FileOutputStream out = new FileOutputStream(path);

                    byte[] buf = new byte[1024];

                    for (int nReadSize = in.read(buf); nReadSize != -1; nReadSize = in.read(buf)) {
                        out.write(buf, 0, nReadSize);
                    }
                    out.close();
                }
            }
        } catch (Exception e) {

        } finally {
            in.close();
        }

        // delete zip file
        unZipFile.delete();
    }

    /**
     * 단말의 폰번호 알아오기
     * 
     * @param mContext
     * @return
     */
    public static String getPhoneNumber(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        String phoneNumber = StringUtil.defaultIfBlank(tm.getLine1Number());
        phoneNumber = StringUtil.removeString(phoneNumber);
        if (StringUtil.startsWith(phoneNumber, "821")) { // 대한민국 국가번호 포함시
            phoneNumber = "01" + StringUtil.substringAfter(phoneNumber, "821");
        }

        return phoneNumber;
    }

    /**
     * 앱의 버전명 갖고 오기
     * 
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 단말의 고유 ID
     * 
     * @return
     */
    public static String getDeviceId() {
        // Returns the unique device ID, for example,
        // the IMEI for GSM and the MEID for CDMA phones.
        // Return null if device ID is not available.
        // Requires Permission: READ_PHONE_STATE

        // String id = TelephonyManager.getDeviceId();
        return null;
    }

    public static String getMacAddress(Context mContext) {
        // Requires Permission: ACCESS_WIFI_STATE
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wm.getConnectionInfo();

        return wifiInfo.getMacAddress();
    }
}
