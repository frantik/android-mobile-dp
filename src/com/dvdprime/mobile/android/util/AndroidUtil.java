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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.widget.ViewAnimator;
import android.widget.ViewFlipper;

import com.dvdprime.mobile.android.R;

/**
 * Android Util
 * 
 * @author 작은광명
 * 
 */
public class AndroidUtil {
    public static String TAG = AndroidUtil.class.getSimpleName();

    public static void showToast(Context context, String title) {
        Toast toast = Toast.makeText(context, title, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToast(Context context, View view) {
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static NotificationManager showNotification(Context context, String title, String message, String alertmessage, int icon, Class<?> forwordActivity) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // The PendingIntent to launch our activity if the user selects this
        // notification
        // PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
        // new Intent(context, forwordActivity), 0);
        // construct the Notification object.
        Notification noti = new Notification();
        noti.icon = icon;
        noti.tickerText = alertmessage;
        noti.when = System.currentTimeMillis();

        // Set the info for the views that show in the notification panel.
        // noti.setLatestEventInfo(context, title, message, contentIntent);

        // after a 100ms delay, vibrate for 250ms, pause for 100 ms and
        // then vibrate for 500ms.
        noti.vibrate = new long[] { 100, 250, 100, 500 };

        // Note that we use R.layout.incoming_message_panel as the ID for
        // the notification. It could be any integer you want, but we use
        // the convention of using a resource id for a string related to
        // the notification. It will always be a unique number within your
        // application.
        nm.notify(1, noti);
        return nm;
    }

    public static void clearNotification(NotificationManager nm) {
        nm.cancelAll();
    }

    public static void showAlert(Context context, String title, String message) {
        showConfim(context, title, message, null);
    }

    public static AlertDialog.Builder creativeAlertDialog(Context context, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        return alert;
    }

    public static void showConfim(Context context, String title, String message, OnClickListener listener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setIcon(R.drawable.ic_alerts_and_states_warning);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(context.getString(R.string.alert_dialog_ok), listener);
        if (listener != null) {
            alert.setNegativeButton(context.getString(R.string.alert_dialog_cancel), null);
        }
        alert.show();
    }

    public static final void clickedLinkAction(Activity mActivity, String url) {
        Intent intent;

        if (url != null) {
            // 미디어파일 플레이 하기
            if (url.endsWith(".mp3")) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setDataAndType(Uri.parse(url), "audio/mp3");
                mActivity.startActivity(intent);
            }
            // 동영상 플레이 하기
            else if (MultimediaUtil.isMultimediaType(url)) {
                intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                intent.setDataAndType(Uri.parse(url), "video/*");
                mActivity.startActivity(intent);
            } else if (url.startsWith("http://") // 웹페이지 보기
                    || url.startsWith("geo:") // 구글맵 보기
                    || url.startsWith("http://maps.google.com/maps?") // 구글 길찾기
            // http://maps.google.com/maps?f=d&saddr=출발지주소&daddr=도착지주소&hl=ko
            ) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mActivity.startActivity(intent);
            } else if (url.startsWith("tel:")) { // 전화걸기
                intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                mActivity.startActivity(intent);
            } else if (url.startsWith("tel:")) { // 전화걸기로 넘김
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                mActivity.startActivity(intent);
            } else if (url.startsWith("smsto:")) { // SMS 발송
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                intent.putExtra("sms_body", ""); // SMS Text
                mActivity.startActivity(intent);
            } else if (url.startsWith("content:")) { // MMS 발송 (content://media/external/images/media/23)
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra("sms_body", ""); // MMS Text
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
                intent.setType("image/png");
                mActivity.startActivity(intent);
            }
            // else if (url.startsWith("mailto:")) { // 이메일 발송
            else if (StringUtil.isValidEmail(url)) {
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/email");
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { url });
                mActivity.startActivity(intent);
            } else if (url.startsWith("market://search?")) { // 마켓 검색
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mActivity.startActivity(intent);
            } else if (url.startsWith("market://details?id=")) { // 마켓 상세 화면
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mActivity.startActivity(intent);
            }

            /*
             * 그외 여러가지 인텐트 호출
             */
            // 설치 어플 제거
            // intent = new Intent(Intent.ACTION_DELETE,
            // Uri.fromParts("package", "strPackageName", null));
            // mActivity.startActivity(intent);

            // 구글 검색
            // intent = new Intent();
            // intent.setAction(Intent.ACTION_WEB_SEARCH);
            // intent.putExtra(SearchManager.QUERY, "searchString");
            // mActivity.startActivity(intent);
        }
    }

    public static Intent addRegisterReceiver(Context context, BroadcastReceiver broadcastreceiver, IntentFilter filter) {
        return context.registerReceiver(broadcastreceiver, filter);
    }

    public static void setKeyboardVisible(Context context, View view, boolean wantShowKeyboard) {
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // only will trigger it if no physical keyboard is open
        if (wantShowKeyboard) {
            mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            // 숨기기
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Location getLocation(Address addr) {
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(addr.getLatitude());
        location.setLongitude(addr.getLongitude());
        return location;
    }

    public static ViewFlipper creativeFlipper(Context context, ArrayList<View> addView, int in_ani, int out_ani) {
        ViewFlipper flipper = creativeFlipper(context);
        for (int i = 0; i < addView.size(); i++)
            flipper.addView(addView.get(i));
        setAnimation(context, flipper, in_ani, out_ani);
        return flipper;
    }

    public static ViewFlipper creativeFlipper(Context context) {
        return new ViewFlipper(context);
    }

    public static InputStream getRawResources(Context context, int ref) {
        return context.getResources().openRawResource(ref);
    }

    public static Resources getResources(Context context) {
        return context.getResources();
    }

    public static Drawable getDrawable(Context context, int res) {
        Resources resource = getResources(context);// .getResources();
        Drawable d = resource.getDrawable(res);
        return setClearFilter(d);
    }

    public static XmlResourceParser getXml(Context context, int res) {
        Resources resource = getResources(context);// .getResources();
        return resource.getXml(res);
    }

    public static Bitmap getBitmap(Context context, int imgres) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgres);
        return bitmap;
    }

    public static String getString(Context context, int res) {
        Resources resource = context.getResources();
        return resource.getString(res).toString();
    }

    public static String[] getStringArray(Context context, int res) {
        Resources resource = context.getResources();
        return resource.getStringArray(res);
    }

    public static String getString(Context context, int res, Object... arg) {
        Resources resource = context.getResources();
        return resource.getString(res, arg).toString();
    }

    public static int getColor(Context context, int res) {
        Resources resource = context.getResources();
        return resource.getColor(res);
    }

    public static int getInteger(Context context, int res) {
        Resources resource = context.getResources();
        return resource.getInteger(res);
    }

    public static Drawable setAlpha(Drawable draw, int alpha) {
        draw.setAlpha(alpha);
        draw.invalidateSelf();
        return draw;
    }

    public static void setFilter(Drawable draw, ColorFilter filter) {
        draw.setColorFilter(filter);
    }

    public static Drawable setClearFilter(Drawable draw) {
        draw.clearColorFilter();
        draw.invalidateSelf();
        return draw;
    }

    public static void setGrayFilter(Drawable draw) {
        ColorMatrix cm = new ColorMatrix(new float[] { 0.299f, 0.587f, 0.114f, 0, 0, 0.299f, 0.587f, 0.114f, 0, 0, 0.299f, 0.587f, 0.114f, 0, 0, 0, 0, 0, 1, 0 });
        setMatrixColorFilter(cm, draw);
    }

    public static void setReverseFilter(Drawable draw) {
        ColorMatrix cm = new ColorMatrix(new float[] { -1, 0, 0, 0, 255, 0, -1, 0, 0, 255, 0, 0, -1, 0, 255, 0, 0, 0, 1, 0 });
        setMatrixColorFilter(cm, draw);
    }

    public static Drawable setMatrixColorFilter(ColorMatrix matrix, Drawable draw) {
        draw.setColorFilter(new ColorMatrixColorFilter(matrix));
        draw.invalidateSelf();
        return draw;
    }

    public static Drawable setColorFilter(Drawable draw, int color) {
        // Mode t = PorterDuff.Mode.MULTIPLY;
        // PorterDuff.Mode.SRC_ATOP,
        // PorterDuff.Mode.MULTIPLY,
        return setColorFilter(draw, color, PorterDuff.Mode.MULTIPLY);
    }

    public static Drawable setColorFilter(Drawable draw, int color, PorterDuff.Mode mod) {
        draw.setColorFilter(new PorterDuffColorFilter(color, mod));
        draw.invalidateSelf();
        return draw;
    }

    public static void setVibrator(Context context, long mms) {
        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(mms);
    }

    public static void setRingMode(Context context, int mode) {
        AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mgr.setRingerMode(mode);
        // AudioManager.RINGER_MODE_NORMAL;//벨소리모드
        // AudioManager.RINGER_MODE_VIBRATE;//진동
        // AudioManager.RINGER_MODE_SILENT;//무음
    }

    public static void setBrightnessMode(Context context, int mode) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
        // Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL //수동
        // Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;//오토
    }

    public static void setBrightness(Context context, int value) {
        // 0~255
        // 밝기 값에 value 값을 적용한다. ( value : 0~ 255 값 )
        setBrightnessMode(context, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL); // 수동모드
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
        // WindowManager.LayoutParams lp=w.getAttributes();
        // lp.screenBrightness = (float)temp;
        // w.setAttributes(lp);

        // Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL //수동
        // Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;//오토
        // <uses-permission
        // android:name=”android.permission.HARDWARE_TEST”></uses-permission>
    }

    public static List<ApplicationInfo> getInstalledApplications(Context context) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> list = pm.getInstalledApplications(0);
        return list;
        // for (ApplicationInfo applicationInfo : list) {
        // String name = String.valueOf(applicationInfo.loadLabel(pm)); // 앱 이름
        // String pName = applicationInfo.packageName; // 앱 패키지
        // Drawable iconDrawable = applicationInfo.loadIcon(pm); // 앱 아이콘
        // }
    }

    public static String getSystemInfo() {
        String a = "BOARD" + Build.BOARD;
        a += "BRAND" + Build.BRAND;
        a += "CPU_ABI" + Build.CPU_ABI;
        a += "DEVICE" + Build.DEVICE;
        a += "DISPLAY" + Build.DISPLAY;
        a += "FINGERPRINT" + Build.FINGERPRINT;
        a += "HOST" + Build.HOST;
        a += "ID" + Build.ID;
        a += "MANUFACTURER" + Build.MANUFACTURER;
        a += "MODEL" + Build.MODEL;
        a += "PRODUCT" + Build.PRODUCT;
        a += "TAGS" + Build.TAGS;
        a += "TYPE" + Build.TYPE;
        a += "USER" + Build.USER;
        return a;

    }

    public static int getDipToPixel(Context context, float dip) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }

    public static Dialog creativeDialog(Context context, int layout) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(layout);
        return dialog;
    }

    public static void setTitle(Activity context, String titlemsg) {
        context.setTitle(titlemsg);
    }

    public static void setTitle(Activity context, int reglayout) {
        context.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, reglayout);
    }

    public static void setWindowNoTitle(Activity context) {
        setWindowFeature(context, Window.FEATURE_NO_TITLE);
    }

    public static void setWindowFeature(Activity context, int windowFeature) {
        // context.requestWindowFeature(Window.FEATURE_NO_TITLE);
        context.requestWindowFeature(windowFeature);
    }

    public static ProgressDialog creativeProgressBar(Context context) {
        return creativeProgressBar(context, null);
    }

    public static ProgressDialog creativeProgressBar(Context context, String comment) {
        ProgressDialog dialog = new ProgressDialog(context);
        if (comment == null)
            dialog.setMessage("Please wait while loading...");
        else
            dialog.setMessage(comment);

        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        return dialog;
    }

    // 핸폰 사용중인 통신
    public static int availableCommunication(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiAvail = ni.isAvailable();
        boolean isWifiConn = ni.isConnected();
        ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileAvail = ni.isAvailable();
        boolean isMobileConn = ni.isConnected();
        String status = "WiFi\nAvail = " + isWifiAvail + "\nConn = " + isWifiConn + "\nMobile\nAvail = " + isMobileAvail + "\nConn = " + isMobileConn + "\n";
        Log.d("Communication State! ", status);
        if (isWifiAvail == true && isWifiConn == true) {
            return ConnectivityManager.TYPE_WIFI;
        } else if (isMobileAvail == true && isMobileConn == true) {
            return ConnectivityManager.TYPE_MOBILE;
        } else {
            return -1;
        }
    }

    public static void setWifiEnable(Context context, boolean sw) {
        // <uses-permission
        // android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // boolean enabled = wifiManager.isWifiEnabled();
        wifiManager.setWifiEnabled(sw);
    }

    public static boolean getWifiEnableState(Context context, boolean sw) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean enabled = wifiManager.isWifiEnabled();
        return enabled;
    }

    public static void setBluetoothEnable(boolean sw) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        // 현재 Bluetooth가 켜져 있는지, 혹은 켜는 중인지 확인 한다.
        if (sw) {
            adapter.enable(); // Bluetooth On
        } else {
            adapter.disable(); // Bluetooth Off
        }
    }

    public static int getBluetoothStatus() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.getState();
        // //현재 Bluetooth가 켜져 있는지, 혹은 켜는 중인지 확인 한다.
        // if(adapter.getState() == BluetoothAdapter.STATE_TURNING_ON ||
        // adapter.getState() == BluetoothAdapter.STATE_ON)
        // {
        // adapter.disable(); // Bluetooth Off
        // }
        // else
        // {
        // adapter.enable(); // Bluetooth On
        // }
    }

    public static String getPhoneNumber(Context context) {
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phone = "";
        try {
            if (tMgr.getLine1Number() != null) {
                phone = tMgr.getLine1Number();
            }
            if (phone.length() == 10)
                return phone;

            phone = phone.substring(phone.length() - 10, phone.length());
            phone = "0" + phone;

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Log.d("Phone Number ",phone);
        // phone="01050950425";
        // phone="01100110011";
        return phone;
    }

    public static boolean isGPSEnable(Context context) {
        LocationManager locationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isNatworkEnable(Context context) {
        LocationManager locationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static void goLocationSettingPage(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        context.startActivity(intent);
        // context.startActivity(new
        // Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS));
    }

    public static void goLanguageSettingPage(Context context) {
        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS));
    }

    public static void goProgram(Context context, String pkg, String fullclasspath) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(pkg, fullclasspath));
        context.startActivity(intent);
    }

    @SuppressWarnings("deprecation")
    public static void killProgram(Context context, String packageName) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            // 2.1 version
            mActivityManager.restartPackage(packageName);
        } catch (Exception e) {
            // 2.2 version
            mActivityManager.killBackgroundProcesses(packageName);
        }
    }

    public static void goPage(Context context, Class<?> gopage) {
        Intent intent = new Intent(context, gopage);
        context.startActivity(intent);
    }

    public static void goPage(Context context, Class<?> gopage, String extraName, String extraValue) {
        Intent intent = new Intent(context, gopage);
        intent.putExtra(extraName, extraValue);
        context.startActivity(intent);
    }

    public static void goPage(Context context, Class<?> gopage, String extraName, int extraValue) {
        Intent intent = new Intent(context, gopage);
        intent.putExtra(extraName, extraValue);
        context.startActivity(intent);
    }

    public static void goPage(Context context, Class<?> gopage, String extraName, Parcelable extraValue) {
        Intent intent = new Intent(context, gopage);
        intent.putExtra(extraName, extraValue);
        context.startActivity(intent);
    }

    public static void resultSetParameter(Activity context, int resultCode) {
        context.setResult(resultCode);
        // Intent intent = new Intent(context, gopage);
        // context.startActivity(intent);
    }

    public static void resultSetParameter(Activity context, int resultCode, String extraName, String extraValue) {
        Intent intent = new Intent();
        intent.putExtra(extraName, extraValue);
        context.setResult(resultCode, intent);
    }

    public static void resultSetParameter(Activity context, int resultCode, String extraName, int extraValue) {
        Intent intent = new Intent();
        intent.putExtra(extraName, extraValue);
        context.setResult(resultCode, intent);
    }

    public static void resultSetParameter(Activity context, int resultCode, String extraName, int[] extraValue) {
        Intent intent = new Intent();
        intent.putExtra(extraName, extraValue);
        context.setResult(resultCode, intent);
    }

    public static void resultSetParameter(Activity context, int resultCode, String extraName, double extraValue) {
        Intent intent = new Intent();
        intent.putExtra(extraName, extraValue);
        context.setResult(resultCode, intent);
    }

    public static void resultSetParameter(Activity context, int resultCode, String extraName, double[] extraValue) {
        Intent intent = new Intent();
        intent.putExtra(extraName, extraValue);
        context.setResult(resultCode, intent);
    }

    public static void resultSetParameter(Activity context, int resultCode, String extraName, float extraValue) {
        Intent intent = new Intent();
        intent.putExtra(extraName, extraValue);
        context.setResult(resultCode, intent);
    }

    public static void resultSetParameter(Activity context, int resultCode, String extraName, float[] extraValue) {
        Intent intent = new Intent();
        intent.putExtra(extraName, extraValue);
        context.setResult(resultCode, intent);
    }

    public static void resultSetParameter(Activity context, int resultCode, String extraName, Parcelable extraValue) {
        Intent intent = new Intent();
        intent.putExtra(extraName, extraValue);
        context.setResult(resultCode, intent);
    }

    public static void goPageForResult(Activity context, int requstcode, Class<?> gopage) {
        Intent intent = new Intent(context, gopage);
        context.startActivityForResult(intent, requstcode);
    }

    public static void goPageForResult(Activity context, int requstcode, Class<?> gopage, String extraName, String extraValue) {
        Intent intent = new Intent(context, gopage);
        intent.putExtra(extraName, extraValue);
        context.startActivityForResult(intent, requstcode);
    }

    public static void goPageForResult(Activity context, int requstcode, Class<?> gopage, String extraName, Parcelable extraValue) {
        Intent intent = new Intent(context, gopage);
        intent.putExtra(extraName, extraValue);
        context.startActivityForResult(intent, requstcode);
    }

    public static void goPageForResult(Activity context, int requstcode, Class<?> gopage, String extraName, int extraValue) {
        Intent intent = new Intent(context, gopage);
        intent.putExtra(extraName, extraValue);
        context.startActivityForResult(intent, requstcode);
    }

    public static void goDial(Context context, String dial_number) {
        Intent phonepassIntent = new Intent();
        phonepassIntent.setAction(Intent.ACTION_DIAL);
        phonepassIntent.setData(Uri.parse("tel:" + dial_number));
        context.startActivity(phonepassIntent);
    }

    public static Intent getIntent(Context context, Class<?> activitys) {
        Intent intent = new Intent(context, activitys);
        return intent;
    }

    public static Context getContext(Activity activity) {
        return activity.getApplicationContext();
    }

    public static Dialog backgroundProcess(Context context, final Runnable run) {
        return backgroundProcess(context, run, null);
    }

    public static Dialog backgroundProcess(Context context, final Runnable run, String loadingComment) {
        return backgroundProcess(context, run, true, loadingComment);
    }

    public static Dialog backgroundProcess(Context context, final Runnable run, final boolean showDialog, String loadingComment) {
        final ProgressDialog progressdialog = creativeProgressBar(context, loadingComment);
        if (showDialog)
            progressdialog.show();
        Runnable wrapper = new Runnable() {
            public void run() {
                run.run();
                if (showDialog)
                    progressdialog.dismiss();
            }
        };
        Thread thread = new Thread(wrapper);
        thread.setDaemon(true);
        thread.start();
        return progressdialog;
    }

    public static void setAnimation(Context context, ViewAnimator victim, int in_animRrs, int out_animRrs) {
        // victim.setInAnimation(AnimationUtils.loadAnimation(context,
        // animRrs));
        victim.setInAnimation(context, in_animRrs);
        victim.setOutAnimation(context, out_animRrs);
    }

    public static Animation getAnimation(Context context, int anim_res) {
        return AnimationUtils.loadAnimation(context, anim_res);
    }

    public static void goMarket(Context context, String pkgname) {
        // Intent marketIntent = new Intent(Intent.ACTION_VIEW,
        // Uri.parse("market://details?id=com.google.zxing.client.android"));
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkgname));
        context.startActivity(marketIntent);
    }

    public static int getViewWidth(View context) {
        return context.getMeasuredWidth();
    }

    public static int getViewHeight(View context) {
        return context.getMeasuredHeight();
    }

    @SuppressWarnings("deprecation")
    public static int getWindowWidth(Context context) {
        Display display = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    @SuppressWarnings("deprecation")
    public static int getWindowHeight(Context context) {
        Display display = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getHeight();
    }

    public static InputStream openAssetsFile(Context context, String assetfilePath) {
        InputStream is = null;
        try {
            is = context.getAssets().open(assetfilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;

    }

    public static List<WifiConfiguration> getWifiConfigrations(Context context) {
        WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = mainWifi.getConfiguredNetworks();
        return list;
    }

    public static WifiInfo getWifiConnectionInfo(Context context) {
        WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ;
        WifiInfo wifiInfo = mainWifi.getConnectionInfo();
        return wifiInfo;
    }

    public static WifiConfiguration creativeWifiConfiguration(String ssid, String mod, String passwd) {
        // 공통사항
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        // wifiConfig.BSSID = result.BSSID;
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.priority = 40;

        if (mod == null || mod == "" || mod.equals("")) {
            // Capabilites -> Open
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedAuthAlgorithms.clear();
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }

        return wifiConfig;
    }

    public static WifiConfiguration creativeWifiConfiguration(ScanResult result, String passwd) {
        return creativeWifiConfiguration(result.SSID, result.capabilities, passwd);
    }

    public static int addNetwork(Context context, WifiConfiguration wifiConfig) {
        setWifiEnable(context, true);
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int netId = wifi.addNetwork(wifiConfig);
        wifi.saveConfiguration();
        return netId;
    }

    public static void removeNetwork(Context context, int networkid) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifi.removeNetwork(networkid);
        wifi.saveConfiguration();
    }

    public static void removeNetwork(Context context, String ssid) {
        removeNetwork(context, ssid, null);
    }

    public static void removeNetwork(Context context, String ssid, String bssid) {
        if (ssid != null)
            ssid = ssid.replaceAll("\"", "");
        if (bssid != null)
            bssid = bssid.replaceAll("\"", "");
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configrations = getWifiConfigrations(context);
        for (int i = 0; i < configrations.size(); i++) {
            WifiConfiguration config = configrations.get(i);
            if ((config.SSID != null && config.SSID.replaceAll("\"", "").equals(ssid)) || (config.BSSID != null && config.BSSID.replaceAll("\"", "").equals(bssid))) {
                AndroidUtil.removeNetwork(context, config.networkId);
            }
        }
        wifi.saveConfiguration();
    }

    public static void connectionWifi(Context context, WifiConfiguration wifiConfig, int ntind, boolean wantConnection) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // wifi.saveConfiguration();
        wifi.updateNetwork(wifiConfig);
        wifi.enableNetwork(ntind, wantConnection);
    }

    public static void enableNetwork(Context context, int networkid) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // wifi.updateNetwork(wifiConfig);
        wifi.enableNetwork(networkid, true);
        wifi.saveConfiguration();
    }

    public static float[] orientationSensorFromRatationMode(Activity context, float x_heading, float y_pitch, float z_roll) {
        int rotation = AndroidUtil.getRotation(context);

        // total accept
        // 방위각

        // 위아래 경사
        float temp = 0;
        if (y_pitch <= 0) {
            temp = y_pitch * -1;
        } else {
            temp = 360 - (y_pitch);
        }
        y_pitch = temp;

        // 좌우경사
        if (y_pitch > 100) {
            if (z_roll < 0) {
                z_roll = -90 - (z_roll + 90);
            } else {
                z_roll = 90 + (90 - z_roll);
            }
        }
        if (z_roll <= 0) {
            temp = z_roll * -1;
        } else {
            temp = 360 - (z_roll);
        }
        z_roll = temp;

        if (rotation == Surface.ROTATION_0) {

            // 방위각
            // 위아래 경사
            // 좌우경사

        } else if (rotation == Surface.ROTATION_90) {

            // 방위각
            if (x_heading > 270) {
                x_heading = x_heading - 270;
            } else {
                x_heading = 360 - 270 + x_heading;
            }

            temp = z_roll;
            z_roll = y_pitch;
            y_pitch = temp;
            y_pitch = 360 - y_pitch;

        } else if (rotation == Surface.ROTATION_180) {

            // if(x_heading>180){
            // x_heading = x_heading-180;
            // }else{
            // x_heading =360-180+x_heading;
            // }

        } else if (rotation == Surface.ROTATION_270) {

            if (x_heading > 90) {
                x_heading = x_heading - 90;
            } else {
                x_heading = 360 - 90 + x_heading;
            }

            temp = z_roll;
            z_roll = y_pitch;
            y_pitch = temp;
            z_roll = 360 - z_roll;

        }

        return new float[] { x_heading, y_pitch, z_roll };
    }

    public static void setOrientation(Activity context, int mode) {
        // ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        context.setRequestedOrientation(mode);
    }

    public static int getRotation(Activity context) {
        return context.getWindowManager().getDefaultDisplay().getRotation();
    }

    public static String getVersion(Context context) {
        String version = null;
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = i.versionName;
        } catch (Exception e) {
        }
        return version;
    }

    public static Locale getLocale(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale;

    }

}