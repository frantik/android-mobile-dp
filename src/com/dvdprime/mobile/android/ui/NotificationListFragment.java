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

import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.adapter.NotificationListAdapter;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.extras.PullToRefreshAttacher;
import com.dvdprime.mobile.android.model.Gcm;
import com.dvdprime.mobile.android.model.NotificationResult;
import com.dvdprime.mobile.android.response.ResultListResponse;
import com.dvdprime.mobile.android.util.GsonUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.dvdprime.mobile.android.util.SystemUtil;
import com.dvdprime.mobile.android.volley.ApiRequest;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Notification List Fragment
 * 
 * @author 작은광명
 * 
 */
public class NotificationListFragment extends SherlockListFragment implements PullToRefreshAttacher.OnRefreshListener {

    /** TAG */
    private static final String TAG = makeLogTag(NotificationListFragment.class);

    /**
     * 페이지 번호
     */
    private int mPage = 1;

    /**
     * 페이지 요청 시간
     */
    private long mStartTime = 0L;

    // 알림 목록
    private ArrayList<Gcm> mNotificationList = new ArrayList<Gcm>();

    // 리스트를 보여줄 ArrayAdapter
    private NotificationListAdapter mAdapter;

    // 당겨서 새로고침 라이브러리 선언
    private PullToRefreshAttacher mPullToRefreshAttacher;

    // Allow Activity to pass us it's PullToRefreshAttacher
    void setPullToRefreshAttacher(PullToRefreshAttacher attacher) {
        mPullToRefreshAttacher = attacher;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 기본 공백 문자 설정
        setEmptyText(getString(R.string.empty));

        // 액션바 메뉴 활성화
        setHasOptionsMenu(true);

        // 리스트 어답터 설정
        mAdapter = new NotificationListAdapter(getActivity(), mNotificationList);
        setListAdapter(mAdapter);

        // 리스트뷰 리스너
        getListView().setOnScrollListener(new EndlessScrollListener());
        getListView().setBackgroundColor(getResources().getColor(R.color.white));

        // progress indicator 보이기
        setListShown(false);

        // 당겨서 새로고침 클래스 초기화
        mPullToRefreshAttacher = PullToRefreshAttacher.get(getSherlockActivity());

        // 당겨서 새로고침을 처리할 뷰 세팅
        mPullToRefreshAttacher.addRefreshableView(getListView(), this);

        // 알림 목록 요청
        ApiRequest filterReq = new ApiRequest(StringUtil.format(Config.MOBILE_DP_API_NOTIFICATION_LIST, PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, "")), createReqSuccessListener(), createReqErrorListener());
        filterReq.setTag(TAG);
        DpApp.getRequestQueue().add(filterReq);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!DpApp.hasData()) {
            loadPage();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DpApp.getRequestQueue().cancelAll(TAG);
        setListAdapter(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefreshStarted(View view) {
        onRefreshList();
    }

    private void onRefreshList() {
        mPage = 0;
        mAdapter.notifyDataSetChanged();
        getListView().setOnScrollListener(new EndlessScrollListener());
        setListShown(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            EasyTracker.getTracker().sendEvent("NotificationList Menu", "Click", "Home", 0L);
            getActivity().finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDocumentView(position);
    }

    private void loadPage() {
        String reqURL = StringUtil.format(Config.MOBILE_DP_API_NOTIFICATION_LIST, PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, ""));
        StringBuffer sb = new StringBuffer().append(reqURL).append("&page=").append(++mPage).append("&startTime=").append(mStartTime);

        ApiRequest filterReq = new ApiRequest(sb.toString(), createReqSuccessListener(), createReqErrorListener());
        filterReq.setTag(TAG);
        DpApp.getRequestQueue().add(filterReq);
    }

    private Response.Listener<String> createReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    ResultListResponse listRes = GsonUtil.fromJson(response, ResultListResponse.class);
                    if (listRes.getStatus() == 200) {
                        NotificationResult nr = GsonUtil.fromJson(GsonUtil.toJson(listRes.getResult()), NotificationResult.class);
                        mPage = nr.getPage();
                        mStartTime = nr.getStartTime();
                        if (mPage == 1) {
                            mNotificationList.clear();
                        }
                        if (listRes.getList() != null) {
                            SystemUtil.launcherBroadcast(DpApp.class, 0);
                            mNotificationList.addAll(GsonUtil.getArrayList(GsonUtil.toJson(listRes.getList()), Gcm.class));
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (mPage == 1) {
                                setEmptyText(getString(R.string.not_exist_notification_msg));
                            }
                        }
                        // progress indicator 정지
                        setListShownNoAnimation(true);
                        // Notify PullToRefreshAttacher that the refresh has finished
                        mPullToRefreshAttacher.setRefreshComplete();
                    }
                }
            }
        };
    }

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Give some text to display if there is no data. In a real
                // application this would come from a resource.
                setEmptyText(getResources().getString(R.string.list_loading_fail));
                // progress indicator 정지
                setListShownNoAnimation(true);
            }
        };
    }

    private void showDocumentView(int index) {
        Gcm gcm = mAdapter.getItem(index);
        // Otherwise we need to launch a new activity to display
        // the dialog fragment with selected text.
        Intent intent = new Intent(getActivity(), DocumentViewActivity.class);
        intent.setData(Uri.parse(gcm.getTargetUrl()));
        intent.putExtra("targetKey", gcm.getTargetKey());
        startActivity(intent);
    }

    /**
     * Detects when user is close to the end of the current page and starts loading the next page so the user will not have to wait (that much) for the next entries.
     * 
     * @author Ognyan Bankov (ognyan.bankov@bulpros.com)
     */
    public class EndlessScrollListener implements OnScrollListener {
        // how many entries earlier to start loading next page
        private int visibleThreshold = 5;

        private int currentPage = 0;

        private int previousTotal = 0;

        private boolean loading = true;

        public EndlessScrollListener() {}

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                loadPage();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        public int getCurrentPage() {
            return currentPage;
        }
    }

}
