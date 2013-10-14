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
package com.dvdprime.mobile.android.ui;

import static com.dvdprime.mobile.android.util.LogUtil.LOGD;
import static com.dvdprime.mobile.android.util.LogUtil.LOGI;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.ads.DpAdManager;
import com.dvdprime.mobile.android.ads.DpAdViewCore;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.constants.RequestCode;
import com.dvdprime.mobile.android.extras.PullToRefreshAttacher;
import com.dvdprime.mobile.android.model.Document;
import com.dvdprime.mobile.android.model.Refresh;
import com.dvdprime.mobile.android.parser.DocumentViewParser;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.task.CmtDeleteTask;
import com.dvdprime.mobile.android.task.CmtRecommendTask;
import com.dvdprime.mobile.android.task.ViewDeleteTask;
import com.dvdprime.mobile.android.task.ViewRecommendTask;
import com.dvdprime.mobile.android.task.ViewSaveTask;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.LogUtil;
import com.dvdprime.mobile.android.util.MultimediaUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.dvdprime.mobile.android.util.SystemUtil;
import com.dvdprime.mobile.android.volley.BitmapLruCache;
import com.dvdprime.mobile.android.volley.StringRequest;
import com.google.analytics.tracking.android.EasyTracker;

public class DocumentViewActivity extends SherlockActivity implements PullToRefreshAttacher.OnRefreshListener {

    /** TAG */
    private final String TAG = makeLogTag(DocumentViewActivity.class.getSimpleName());

    private String contextUrl = "";
    private int index;
    private boolean isRefresh = false;

    private DocumentViewActivity mActivity;
    private DpAdManager mAdManager;
    private DpAdViewCore mAdView;

    private RelativeLayout mLayout;
    private PullToRefreshAttacher mPullToRefreshAttacher;

    private WebView mWebView;
    private String mUrl;
    private String mContent;
    private String mTargetKey;

    private int wvHeight = 0;
    private int wvWidth = 0;

    /* Menus */
    private MenuItem deleteMenu = null;
    private MenuItem modifyMenu = null;
    private MenuItem mydpMenu = null;
    private MenuItem rcmdMenu = null;
    private MenuItem refreshMenu = null;

    /* Context Menus */
    private final int CONTEXT_MENU_COPY_URL = 1;
    private final int CONTEXT_MENU_BROWSER = 2;
    private final int CONTEXT_MENU_SHARE_LINK = 3;
    private final int CONTEXT_MENU_SAVE_IMAGE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);

        // Initialize WebView
        mWebView = new DpWebView(this);
        mWebView.setId(mWebView.hashCode());
        mWebView.setOnCreateContextMenuListener(this);

        // Initialize Ad.
        mAdManager = new DpAdManager();
        mAdManager.onCreate(this);

        mLayout = new RelativeLayout(this);
        mAdView = new DpAdViewCore(this);
        mAdView.setId(mAdView.hashCode());

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams1.addRule(RelativeLayout.ABOVE, mAdView.getId());
        mLayout.addView(mWebView, layoutParams1);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mLayout.addView(mAdView, layoutParams2);
        mAdManager.bindCoreView(mAdView);

        setContentView(mLayout);

        // Initialize pull to refresh
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
        mPullToRefreshAttacher.addRefreshableView(mWebView, this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayout.findViewById(mAdView.getId()).setVisibility(View.GONE);
        }
        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
        this.mActivity = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                index = bundle.getInt("index", 0);
                Document doc = (Document) DpApp.getDocumentList().get(this.index);
                getSupportActionBar().setTitle(doc.getUserName());
                getSupportActionBar().setSubtitle(doc.getTitle());
                mUrl = Config.getAbsoluteUrl("/bbs" + doc.getUrl());
            } catch (Exception e) {
            }
        }
        Uri uri = getIntent().getData();
        if (uri != null) {
            this.mUrl = uri.toString();
            if (getIntent().getExtras().getString("targetKey") != null) {
                mTargetKey = getIntent().getExtras().getString("targetKey");
            }
        }
        if (StringUtil.isBlank(this.mUrl)) {
            finish();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Initialize EventBus
        EventBusProvider.getInstance().register(this, String.class, new Class[] { Refresh.class });

        // Request Document View
        StringRequest req = new StringRequest(0, this.mUrl, createReqSuccessListener(), createReqErrorListener());
        req.setTag(this.TAG);
        DpApp.getRequestQueue().add(req);

        // Initialize Google Analytics
        EasyTracker.getInstance().setContext(this.mActivity);
        EasyTracker.getTracker().sendView("DocView");
        LogUtil.LOGD("Tracker", "DocView");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (rcmdMenu != null) {
            if (PrefUtil.getInstance().getString("account_id", null) != null) {
                rcmdMenu.setEnabled(true);
                mydpMenu.setEnabled(true);
            } else {
                rcmdMenu.setEnabled(false);
                mydpMenu.setEnabled(false);
                modifyMenu.setVisible(false);
                deleteMenu.setVisible(false);
            }
        }
        if (mContent != null) {
            callHiddenWebViewMethod("onResume");
            if (mAdManager != null)
                mAdManager.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        callHiddenWebViewMethod("onPause");
        if (mAdManager == null) {
            return;
        }
        mAdManager.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    public void onDestroy() {
        DpApp.getRequestQueue().cancelAll(TAG);
        EventBusProvider.getInstance().unregister(this);
        super.onDestroy();

        getSupportActionBar().setIcon(null);

        mLayout.removeAllViews();
        if (mAdManager != null)
            mAdManager.onDestroy();

        mPullToRefreshAttacher = null;

        mWebView.setFocusable(true);
        mWebView.removeAllViews();
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.destroy();
        mWebView = null;
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            onRefreshStarted(null);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayout.findViewById(mAdView.getId()).setVisibility(View.GONE);
            mWebView.loadUrl("javascript:configurationChanged(" + wvHeight + ")");
        } else {
            mLayout.findViewById(mAdView.getId()).setVisibility(View.VISIBLE);
            mWebView.loadUrl("javascript:configurationChanged(" + wvWidth + ")");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        HitTestResult hitTestResult = mWebView.getHitTestResult();
        if (hitTestResult.getType() == HitTestResult.IMAGE_TYPE || hitTestResult.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            contextUrl = hitTestResult.getExtra();
            menu.setHeaderTitle(hitTestResult.getExtra());
            menu.add(Menu.NONE, CONTEXT_MENU_COPY_URL, 0, getString(R.string.context_menu_copy_url));
            menu.add(Menu.NONE, CONTEXT_MENU_BROWSER, 1, getString(R.string.context_menu_open_browser));
            menu.add(Menu.NONE, CONTEXT_MENU_SAVE_IMAGE, 2, getString(R.string.context_menu_save_image));
        } else if (hitTestResult.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
            contextUrl = hitTestResult.getExtra();
            menu.setHeaderTitle(hitTestResult.getExtra());
            menu.add(Menu.NONE, CONTEXT_MENU_COPY_URL, 0, getString(R.string.context_menu_copy_url));
            menu.add(Menu.NONE, CONTEXT_MENU_BROWSER, 1, getString(R.string.context_menu_open_browser));
            menu.add(Menu.NONE, CONTEXT_MENU_SHARE_LINK, 1, getString(R.string.context_menu_share_url));
        }
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        default:
        case CONTEXT_MENU_COPY_URL:
            SystemUtil.copyToClipboard(mActivity, contextUrl);
            break;
        case CONTEXT_MENU_BROWSER:
            AndroidUtil.clickedLinkAction(mActivity, contextUrl);
            break;
        case CONTEXT_MENU_SHARE_LINK:
            Intent localIntent = new Intent("android.intent.action.SEND");
            localIntent.setType("text/plain");
            localIntent.putExtra("android.intent.extra.TEXT", contextUrl);
            startActivity(Intent.createChooser(localIntent, getString(R.string.action_bar_share_with)));
            break;
        case CONTEXT_MENU_SAVE_IMAGE:
            DownloadManager dm = (DownloadManager) getSystemService("download");
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(this.contextUrl));
            req.setTitle("DP Downloader");
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, StringUtil.substringAfterLast(this.contextUrl, "/"));
            req.setAllowedNetworkTypes(DownloadManager.PAUSED_QUEUED_FOR_WIFI);
            req.setMimeType("image/" + StringUtil.substringAfterLast(contextUrl, "."));
            File localFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!(localFile.exists()))
                localFile.mkdirs();
            dm.enqueue(req);
            break;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.document_view_menu, menu);
        this.refreshMenu = menu.findItem(R.id.menu_refresh);
        this.rcmdMenu = menu.findItem(R.id.menu_recommend);
        this.mydpMenu = menu.findItem(R.id.menu_save_mydp);
        this.modifyMenu = menu.findItem(R.id.menu_modify);
        this.deleteMenu = menu.findItem(R.id.menu_delete);
        if (PrefUtil.getInstance().getString("account_id", null) == null) {
            this.rcmdMenu.setEnabled(false);
            this.mydpMenu.setEnabled(false);
        }
        return true;
    }

    public void onEvent(Refresh refresh) {
        if (refresh.getType() == Refresh.DONE) {
            refreshMenu.setActionView(null);
            return;
        }
        refreshMenu.setActionView(R.layout.indeterminate_progress_action);
    }

    public void onEvent(String html) {
        if ((isRefresh || mContent == null) && html != null) {
            if (StringUtil.startsWith(html, "http")) {
                new ImageLoader(DpApp.getRequestQueue(), new BitmapLruCache()).get(html, new ImageLoader.ImageListener() {
                    public void onErrorResponse(VolleyError paramVolleyError) {}

                    public void onResponse(ImageContainer container, boolean paramBoolean) {
                        getSupportActionBar().setIcon(new BitmapDrawable(getResources(), container.getBitmap()));
                    }
                });
            } else if (StringUtil.startsWith(html, "<!DOCTYPE")) {
                isRefresh = false;
                mContent = html;
                if (StringUtil.isBlank(html)) {
                    AndroidUtil.showToast(DpApp.getContext(), getString(R.string.list_loading_fail));
                    finish();
                    return;
                }
                mWebView.loadDataWithBaseURL("http://dvdprime.donga.com", html, "text/html", "utf-8", null);
                EventBusProvider.getInstance().post(new Refresh(Refresh.VIEW));
                mPullToRefreshAttacher.setRefreshComplete();
                return;
            } else {
                String[] splitText = StringUtil.split(html, '|');
                getSupportActionBar().setTitle(splitText[1]);
                getSupportActionBar().setSubtitle(splitText[2]);
                if (StringUtil.equals(splitText[0], PrefUtil.getInstance().getString("account_id", null))) {
                    modifyMenu.setVisible(true);
                    modifyMenu.setEnabled(true);
                    deleteMenu.setVisible(true);
                    deleteMenu.setEnabled(true);
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        case android.R.id.home:
            EasyTracker.getTracker().sendEvent("DocView Menu", "Click", "Home", Long.valueOf(0L));
            finish();
            return true;
        case R.id.menu_refresh:
            EasyTracker.getTracker().sendEvent("DocView Menu", "Click", "Refresh", Long.valueOf(0L));
            menuItem.setActionView(R.layout.indeterminate_progress_action);
            onRefreshStarted(null);
            return true;
        case R.id.menu_share:
            try {
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Method.POST, Config.GOOGLE_SHORTENER_URL, new JSONObject().put("longUrl", this.mUrl), createShortenerReqSuccessListener(), createShortenerReqErrorListener());
                jsonRequest.setTag(TAG);
                DpApp.getRequestQueue().add(jsonRequest);
            } catch (JSONException e) {
            }
            return true;
        case R.id.menu_view_original:
            EasyTracker.getTracker().sendEvent("DocView Menu", "Click", "ViewOriginal", Long.valueOf(0L));
            AndroidUtil.clickedLinkAction(mActivity, mUrl);
            return true;
        case R.id.menu_copy_url:
            EasyTracker.getTracker().sendEvent("DocView Menu", "Click", "CopyURL", Long.valueOf(0L));
            SystemUtil.copyToClipboard(mActivity, mUrl);
            return true;
        case R.id.menu_recommend:
            if (this.rcmdMenu.isEnabled()) {
                EasyTracker.getTracker().sendEvent("DocView Menu", "Click", "Recommend", Long.valueOf(0L));
                new ViewRecommendTask(mActivity).execute(StringUtil.getParamValue(mUrl, "bbslist_id"));
            }
            return true;
        case R.id.menu_save_mydp:
            if (this.rcmdMenu.isEnabled()) {
                EasyTracker.getTracker().sendEvent("DocView Menu", "Click", "SaveMyDP", Long.valueOf(0L));
                new ViewSaveTask(mActivity).execute(StringUtil.getParamValue(mUrl, "bbslist_id"));
            }
            return true;
        case R.id.menu_modify:
            if (rcmdMenu.isEnabled()) {
                EasyTracker.getTracker().sendEvent("DocView Menu", "Click", "Modify", Long.valueOf(0L));
                String title = "";
                String content = "";
                String tag = "";
                String date = "";
                Source source = new Source(mContent);
                Element titleElement = source.getFirstElement("strong");
                if (titleElement != null) {
                    title = StringUtil.substringAfter(titleElement.getTextExtractor().toString(), "] ");
                }
                Element contentElement = source.getFirstElement("id", "viewContent", false);
                if (contentElement != null) {
                    content = contentElement.getContent().toString();
                }
                Element tagElement = source.getFirstElement("id", "viewTag", false);
                if (tagElement != null) {
                    tag = tagElement.getTextExtractor().toString();
                    if (StringUtil.equals(tag, getString(R.string.write_tag_empty))) {
                        tag = "";
                    }
                }
                Element dateElement = source.getFirstElement("id", "viewDate", false);
                if (dateElement != null) {
                    date = StringUtil.substringBefore(dateElement.getTextExtractor().toString(), " ");
                }
                Matcher matcher = Pattern.compile("<br?>", 2).matcher(content);
                Intent intent = new Intent(this, WriteDocumentActivity.class);
                intent.putExtra("id", StringUtil.getParamValue(mUrl, "major") + StringUtil.getParamValue(mUrl, "minor") + StringUtil.getParamValue(mUrl, "master_id"));
                intent.putExtra("title", title);
                intent.putExtra("content", matcher.replaceAll("\n"));
                intent.putExtra("tag", tag);
                intent.putExtra("date", date);
                intent.putExtra("bbsId", StringUtil.getParamValue(mUrl, "bbslist_id"));
                startActivityForResult(intent, RequestCode.MODIFY_DOCUMENT);
            }
            return true;
        case R.id.menu_delete:
            EasyTracker.getTracker().sendEvent("DocView Menu", "Click", "Delete", Long.valueOf(0L));
            AndroidUtil.showConfim(mActivity, getString(R.string.alert_dialog_ok), getString(R.string.view_delete_message), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int paramInt) {
                    dialogInterface.dismiss();
                    new ViewDeleteTask(mActivity).execute(mUrl);
                }
            });
            return true;
        case R.id.menu_setting:
            EasyTracker.getTracker().sendEvent("DocView Menu", "Click", "Setting", Long.valueOf(0L));
            startActivity(new Intent(this.mActivity, SettingActivity.class));
            return true;
        case R.id.menu_about:
            EasyTracker.getTracker().sendEvent("DocView Menu", "Click", "About", Long.valueOf(0L));
            startActivity(new Intent(this.mActivity, AboutActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        isRefresh = true;
        mWebView.loadUrl("file:///android_asset/html/loading.html");
        LOGD(TAG, "request url -> " + mUrl);
        StringRequest req = new StringRequest(mUrl, createReqSuccessListener(), createReqErrorListener());
        req.setTag(TAG);
        DpApp.getRequestQueue().add(req);
    }

    @SuppressLint({ "SetJavaScriptEnabled" })
    private class DpWebView extends WebView {
        float dx = 0.0F;
        float dy = 0.0F;
        float x1 = 0.0F;
        float x2 = 0.0F;
        float y1 = 0.0F;
        float y2 = 0.0F;

        public DpWebView(Context paramContext) {
            super(paramContext);
            getSettings().setAllowFileAccess(true);
            getSettings().setAppCacheEnabled(false);
            getSettings().setBuiltInZoomControls(false);
            getSettings().setCacheMode(2);
            getSettings().setDefaultTextEncodingName("utf-8");
            getSettings().setDisplayZoomControls(false);
            getSettings().setJavaScriptEnabled(true);
            getSettings().setLoadsImagesAutomatically(true);
            getSettings().setSupportMultipleWindows(false);
            getSettings().setUseWideViewPort(true);
            addJavascriptInterface(new DocumentViewActivity.WebAppInterface(DocumentViewActivity.this), "Android");
            setHorizontalScrollBarEnabled(false);
            setBackgroundColor(0);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            setWebViewClient(new WebViewClient() {
                private WebResourceResponse getCssWebResourceResponseFromAsset(String paramString) {
                    try {
                        InputStream localInputStream = DpApp.getContext().getAssets().open("css" + File.separator + paramString);
                        WebResourceResponse res = null;
                        if (localInputStream != null) {
                            res = getUtf8EncodedCssWebResourceResponse(localInputStream);
                        }
                        return res;
                    } catch (IOException e) {
                    }
                    return null;
                }

                private WebResourceResponse getFontsWebResourceResponseFromAsset(String paramString) {
                    try {
                        InputStream localInputStream = DpApp.getContext().getAssets().open("fonts" + File.separator + paramString);
                        WebResourceResponse res = null;
                        if (localInputStream != null) {
                            res = getUtf8EncodedJsWebResourceResponse(localInputStream);
                        }
                        return res;
                    } catch (IOException e) {
                    }
                    return null;
                }

                private WebResourceResponse getImgWebResourceResponseFromAsset(String paramString) {
                    try {
                        InputStream localInputStream = DpApp.getContext().getAssets().open("img" + File.separator + paramString);
                        WebResourceResponse res = null;
                        if (localInputStream != null) {
                            res = getUtf8EncodedImgWebResourceResponse(localInputStream, StringUtil.substringAfter(paramString, "."));
                        }
                        return res;
                    } catch (IOException e) {
                    }
                    return null;
                }

                private WebResourceResponse getJsWebResourceResponseFromAsset(String paramString) {
                    try {
                        InputStream localInputStream = DpApp.getContext().getAssets().open("js" + File.separator + paramString);
                        WebResourceResponse res = null;
                        if (localInputStream != null) {
                            res = getUtf8EncodedJsWebResourceResponse(localInputStream);
                        }
                        return res;
                    } catch (IOException e) {
                    }
                    return null;
                }

                private WebResourceResponse getUtf8EncodedCssWebResourceResponse(InputStream paramInputStream) {
                    return new WebResourceResponse("text/css", "UTF-8", paramInputStream);
                }

                private WebResourceResponse getUtf8EncodedImgWebResourceResponse(InputStream paramInputStream, String paramString) {
                    return new WebResourceResponse("image/" + paramString, "UTF-8", paramInputStream);
                }

                private WebResourceResponse getUtf8EncodedJsWebResourceResponse(InputStream paramInputStream) {
                    return new WebResourceResponse("application/javascript", "UTF-8", paramInputStream);
                }

                public WebResourceResponse shouldInterceptRequest(WebView paramWebView, String paramString) {
                    if ((paramString.startsWith("http://dvdprime.donga.com")) && (paramString.endsWith(".css")))
                        return getCssWebResourceResponseFromAsset(StringUtil.substringAfterLast(paramString, "/"));
                    if ((paramString.startsWith("http://dvdprime.donga.com")) && (paramString.endsWith(".js")))
                        return getJsWebResourceResponseFromAsset(StringUtil.substringAfterLast(paramString, "/"));
                    if ((paramString.startsWith("http://dvdprime.donga.com")) && (StringUtil.contains(paramString, "fonts/glyphicons")))
                        return getFontsWebResourceResponseFromAsset(StringUtil.substringAfterLast(paramString, "/"));
                    if ((paramString.startsWith("http://dvdprime.donga.com")) && (((paramString.endsWith("play-background.jpg")) || (paramString.endsWith("play-button.png")))))
                        return getImgWebResourceResponseFromAsset(StringUtil.substringAfterLast(paramString, "/"));
                    return super.shouldInterceptRequest(paramWebView, paramString);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String overrideUrl) {
                    if (overrideUrl.startsWith("http")) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(overrideUrl));
                        startActivity(i);
                        return true;
                    } else {
                        boolean override = false;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(overrideUrl));
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, TAG);
                        try {
                            startActivity(intent);
                            override = true;
                        } catch (ActivityNotFoundException ex) {
                        }
                        return override;
                    }
                }
            });
            setWebChromeClient(new WebChromeClient() {

                @Override
                public boolean onJsAlert(WebView webView, String title, String message, final JsResult result) {
                    new AlertDialog.Builder(webView.getContext()).setTitle("알림").setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).setCancelable(true).create().show();
                    return true;
                }

                @Override
                public boolean onJsConfirm(WebView webView, String title, String message, final JsResult result) {
                    new AlertDialog.Builder(webView.getContext()).setTitle("확인").setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    }).create().show();
                    return true;
                }

                @Override
                public void onProgressChanged(WebView view, int progress) {
                    setSupportProgressBarVisibility(true);
                    setSupportProgress(progress * 100);
                    if (progress == 100) {
                        // 로딩 프로그레스 종료
                        EventBusProvider.getInstance().post(new Refresh(Refresh.DONE));
                    }
                }
            });
            loadUrl("file:///android_asset/html/loading.html");
        }

        private void click(HitTestResult result) {
            if (result != null) {
                if (result.getType() == HitTestResult.SRC_ANCHOR_TYPE && MultimediaUtil.isMultimediaType(result.getExtra())) {
                    callHiddenWebViewMethod("onPause");
                    AndroidUtil.clickedLinkAction(mActivity, result.getExtra());
                }
                Log.w("HitResult", "type: " + result.getType() + ", extra: " + result.getExtra());
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                x1 = event.getX();
                y1 = event.getY();
                break;
            case (MotionEvent.ACTION_UP): {
                x2 = event.getX();
                y2 = event.getY();
                dx = x2 - x1;
                dy = y2 - y1;

                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx == 0)
                        click(mWebView.getHitTestResult()); // click
                } else {
                    if (dy == 0)
                        click(mWebView.getHitTestResult()); // click
                }
            }
            }
            return super.onTouchEvent(event);
        }
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context paramContext) {
            this.mContext = paramContext;
        }

        @JavascriptInterface
        public void clickWriteComment(String memberId) {
            Intent intent = new Intent(DocumentViewActivity.this, WriteCommentActivity.class);
            intent.putExtra("bbsId", StringUtil.getParamValue(mUrl, "bbslist_id"));
            intent.putExtra("memberId", memberId);
            intent.putExtra("title", getSupportActionBar().getSubtitle());
            intent.putExtra("url", mUrl);
            startActivityForResult(intent, RequestCode.WRITE_COMMENT);
        }

        @JavascriptInterface
        public void copyToClipboard(String paramString) {
            SystemUtil.copyToClipboard(DpApp.getContext(), paramString);
        }

        @JavascriptInterface
        public void setViewport(int width, int height) {
            LOGD(TAG, "webview -> width: " + width + ", height: " + height);
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            float ratio = 0f;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                ratio = (float) width / (float) metrics.widthPixels;
                wvWidth = width;
                wvHeight = (int) (ratio * metrics.heightPixels);
            } else {
                ratio = (float) width / (float) metrics.widthPixels;
                wvWidth = (int) (ratio * metrics.heightPixels);
                wvHeight = width;
            }
            LOGI(TAG, "setViewport -> wvWidth: " + wvWidth + ", wvHeight: " + wvHeight);
        }

        @JavascriptInterface
        public void showCommentMenu(final int depth, String cmtId, final String cmtNo, final String memberId) {
            // 회원 아이디 세팅
            Set<String> members = new HashSet<String>();
            if (memberId != null) {
                for (String id : StringUtil.split(memberId, ",")) {
                    if (!StringUtil.equals(PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, null), id)) {
                        members.add(id);
                    }
                }
            }
            final String ids = members.isEmpty() ? null : StringUtil.join(members, ",");   
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DocumentViewActivity.this);
            alertDialog.setIcon(R.drawable.ic_action_about);
            alertDialog.setTitle(getString(R.string.alert_dialog_comment));
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DocumentViewActivity.this, android.R.layout.simple_list_item_1);
            if (depth == 1) {
                arrayAdapter.add(getString(R.string.cmt_option_write));
                if (!(StringUtil.equals(PrefUtil.getInstance().getString("account_id", null), cmtId)))
                    arrayAdapter.add(getString(R.string.cmt_option_recommend));
            }
            if (StringUtil.equals(PrefUtil.getInstance().getString("account_id", null), cmtId)) {
                if (depth == 1) {
                    arrayAdapter.add(getString(R.string.cmt_option_parent_delete));
                } else {
                    arrayAdapter.add(getString(R.string.cmt_option_child_delete));
                }
            }
            alertDialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (PrefUtil.getInstance().getString("account_id", null) == null) {
                        AndroidUtil.showToast(mActivity, getString(R.string.toast_need_login_message));
                        return;
                    }
                    String str = (String) arrayAdapter.getItem(which);
                    if (StringUtil.equals(str, getString(R.string.cmt_option_write))) {
                        Intent intent = new Intent(DocumentViewActivity.this, WriteCommentActivity.class);
                        intent.putExtra("depth", depth);
                        intent.putExtra("bbsId", StringUtil.getParamValue(mUrl, "bbslist_id"));
                        intent.putExtra("cmtNo", cmtNo);
                        intent.putExtra("memberId", ids);
                        intent.putExtra("title", getSupportActionBar().getSubtitle());
                        intent.putExtra("url", mUrl);
                        startActivityForResult(intent, RequestCode.WRITE_COMMENT);
                        return;
                    }
                    if (StringUtil.equals(str, getString(R.string.cmt_option_recommend))) {
                        new CmtRecommendTask(mActivity).execute(cmtNo);
                        return;
                    }
                    AndroidUtil.showConfim(mActivity, getString(R.string.alert_dialog_ok), getString(R.string.cmt_delete_confirm_message), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new CmtDeleteTask(mActivity).execute(StringUtil.getParamValue(mUrl, "bbslist_id"), cmtNo);
                        }
                    });
                }
            });
            alertDialog.show();
        }

        @JavascriptInterface
        public void showToast(String paramString) {
            AndroidUtil.showToast(this.mContext, paramString);
        }
    }

    private void callHiddenWebViewMethod(String name) {
        if (mWebView != null) {
            try {
                WebView.class.getMethod(name).invoke(mWebView);
            } catch (NoSuchMethodException e) {
                // Log.e(TAG, "No such method: " + name, e);
            } catch (IllegalAccessException e) {
                // Log.e(TAG, "Illegal Access: " + name, e);
            } catch (InvocationTargetException e) {
                // Log.e(TAG, "Invocation Target Exception: " + name, e);
            }
        }
    }

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError paramVolleyError) {
                AndroidUtil.showAlert(mActivity, getString(R.string.alert_dialog_alert), getString(R.string.view_loading_fail));
                finish();
            }
        };
    }

    private Response.Listener<String> createReqSuccessListener() {
        return new Response.Listener<String>() {
            public void onResponse(String html) {
                if (html != null) {
                    EventBusProvider.getInstance().post(new DocumentViewParser(html, mTargetKey).parse());
                    return;
                }
                AndroidUtil.showAlert(mActivity, getString(R.string.alert_dialog_alert), getString(R.string.view_loading_fail));
                finish();
            }
        };
    }

    private Response.ErrorListener createShortenerReqErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError paramVolleyError) {
                AndroidUtil.showToast(mActivity, getString(R.string.toast_share_url_fail_message));
            }
        };
    }

    private Response.Listener<JSONObject> createShortenerReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject result) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, getSupportActionBar().getSubtitle());
                    Object[] data = new Object[2];
                    data[0] = getSupportActionBar().getSubtitle();
                    data[1] = result.getString("id");
                    intent.putExtra(Intent.EXTRA_TEXT, StringUtil.format("[DP]{0} - {1}", data));
                    startActivity(Intent.createChooser(intent, getString(R.string.action_bar_share_with)));
                    return;
                } catch (JSONException localJSONException) {
                    LOGD(TAG, "Parse error");
                }
            }
        };
    }

}