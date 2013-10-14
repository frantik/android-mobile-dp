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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.adapter.DocumentListAdapter;
import com.dvdprime.mobile.android.adapter.DocumentSuggestionsAdapter;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.extras.PullToRefreshAttacher;
import com.dvdprime.mobile.android.model.Document;
import com.dvdprime.mobile.android.model.Filter;
import com.dvdprime.mobile.android.model.Refresh;
import com.dvdprime.mobile.android.parser.DocumentListParser;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.task.ViewDeleteTask;
import com.dvdprime.mobile.android.util.GsonUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.dvdprime.mobile.android.volley.ApiRequest;
import com.dvdprime.mobile.android.volley.StringRequest;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Document List Fragment
 * 
 * @author 작은광명
 * 
 */
public class DocumentListFragment extends SherlockListFragment implements PullToRefreshAttacher.OnRefreshListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    /** TAG */
    private static final String TAG = makeLogTag(DocumentListFragment.class.getSimpleName());

    // 듀얼 보기 여부
    protected static boolean mDualPane;

    // 현재 목록 포커스 위치
    private static int mCurCheckPosition = 0;

    // 리스트를 보여줄 ArrayAdapter
    private static DocumentListAdapter mAdapter;

    // 게시판 고유번호
    private static String mBbsId = null;

    // 목록 URL
    private static String mUrl = null;

    // 새로고침 메뉴 버튼
    private MenuItem refreshMenu = null;

    // 검색 메뉴 버튼
    private MenuItem searchMenu = null;

    // 검색 자동완성 아답터
    private DocumentSuggestionsAdapter mSuggestionsAdapter;

    // 당겨서 새로고침 라이브러리 선언
    private PullToRefreshAttacher mPullToRefreshAttacher;

    // Allow Activity to pass us it's PullToRefreshAttacher
    void setPullToRefreshAttacher(PullToRefreshAttacher attacher) {
        mPullToRefreshAttacher = attacher;
    }

    // 닉네임으로 검색
    private final int CONTEXT_MENU_SEARCH_BY_NICK = 1;
    // 아이디로 검색
    private final int CONTEXT_MENU_SEARCH_BY_ID = 2;
    // 필터 추가
    private final int CONTEXT_MENU_ADD_FILTER = 3;
    // 필터 제거
    private final int CONTEXT_MENU_DEL_FILTER = 4;
    // 글 삭제
    private final int CONTEXT_MENU_DELETE = 5;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 기본 공백 문자 설정
        setEmptyText(getString(R.string.empty));

        // 액션바 메뉴 활성화
        setHasOptionsMenu(true);

        // 리스트 어답터 설정
        mAdapter = new DocumentListAdapter(getActivity(), DpApp.getDocumentList());
        setListAdapter(mAdapter);

        // 리스트뷰 리스너
        getListView().setOnScrollListener(new EndlessScrollListener());
        getListView().setOnCreateContextMenuListener(this);
        getListView().setBackgroundColor(getResources().getColor(R.color.white));

        // progress indicator 보이기
        if (DpApp.getDocumentList().isEmpty()) {
            setListShown(false);
        }

        // 당겨서 새로고침 클래스 초기화
        mPullToRefreshAttacher = PullToRefreshAttacher.get(getSherlockActivity());

        // 당겨서 새로고침을 처리할 뷰 세팅
        mPullToRefreshAttacher.addRefreshableView(getListView(), this);

        // 이벤트 버스 등록
        EventBusProvider.getInstance().register(this, ArrayList.class, Refresh.class);

        mBbsId = getActivity().getIntent().getExtras().getString("id");
        mUrl = Config.getAbsoluteUrl(getActivity().getIntent().getExtras().getString("url"));

        // // 현재 화면이 글 상세 보기 포함인지 확인
        // View detailsFrame = getActivity().findViewById(R.id.document_view);
        // mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        // if (savedInstanceState != null) {
        // // Restore last state for checked position.
        // mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        // }
        // if (mDualPane) {
        // // In dual-pane mode, the list view highlights the selected item.
        // getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // }
        // getListView().setSelector(R.drawable.fragment_list_selector);
        // // Make sure our UI is in the correct state. showDocumentView(mCurCheckPosition);
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
        EventBusProvider.getInstance().unregister(this);
        setListAdapter(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onRefreshStarted(View view) {
        onRefreshList();
    }

    private void onRefreshList() {
        DpApp.setDocumentClear();
        mAdapter.notifyDataSetChanged();
        getListView().setOnScrollListener(new EndlessScrollListener());
        setListShown(false);
    }

    public void onEvent(Refresh refresh) {
        if (refresh.getType() == Refresh.LIST) {
            onRefreshList();
        }
    }

    public void onEvent(ArrayList<Document> documentList) {
        if (documentList != null && !documentList.isEmpty()) {
            DpApp.setHasData(true);
            for (Document doc : documentList) {
                if (!DpApp.getDocDuplicateSet().contains(doc.getId())) {
                    DpApp.getDocumentList().add(doc);
                    DpApp.getDocDuplicateSet().add(doc.getId());
                }
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 아답터에 데이터 변경 알림
                mAdapter.notifyDataSetChanged();
                // progress indicator 정지
                setListShownNoAnimation(true);
                refreshMenu.setActionView(null);
                // Notify PullToRefreshAttacher that the refresh has finished
                mPullToRefreshAttacher.setRefreshComplete();

                // if (mDualPane) {
                // if (DpApp.getDocumentList().size() > 0 && mCurCheckPosition == 0) {
                // showDocumentViewFragment(mCurCheckPosition);
                // }
                // }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        if (info.position > -1) {
            mCurCheckPosition = info.position;
            Document doc = mAdapter.getItem(info.position);

            if (doc == null) {
                return;
            }

            menu.add(Menu.NONE, CONTEXT_MENU_SEARCH_BY_NICK, Menu.NONE + 0, R.string.list_context_menu_search_nick).setEnabled(true);
            menu.add(Menu.NONE, CONTEXT_MENU_SEARCH_BY_ID, Menu.NONE + 1, R.string.list_context_menu_search_id).setEnabled(true);

            if (StringUtil.equals(PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, null), doc.getUserId())) {
                menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE + 2, R.string.list_context_menu_document_delete).setEnabled(true);
            } else {
                boolean includedFilter = false;
                String filters = PrefUtil.getInstance().getString(PrefKeys.FILTERS, null);
                if (filters != null) {
                    List<Filter> filterList = GsonUtil.getArrayList(filters, Filter.class);
                    for (Filter filter : filterList) {
                        if (filter.getTargetId().equals(doc.getUserId())) {
                            includedFilter = true;
                            break;
                        }
                    }
                }
                if (includedFilter)
                    menu.add(Menu.NONE, CONTEXT_MENU_DEL_FILTER, Menu.NONE + 3, R.string.list_context_menu_filter_remove).setEnabled(true);
                else
                    menu.add(Menu.NONE, CONTEXT_MENU_ADD_FILTER, Menu.NONE + 3, R.string.list_context_menu_filter_add).setEnabled(true);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        final Document doc = mAdapter.getItem(mCurCheckPosition);
        if (doc == null) {
            return true;
        }

        switch (item.getItemId()) {
        case CONTEXT_MENU_SEARCH_BY_NICK:
            search(2, doc.getUserName());
            break;
        case CONTEXT_MENU_SEARCH_BY_ID:
            search(1, doc.getUserId());
            break;
        case CONTEXT_MENU_ADD_FILTER:
            if (PrefUtil.getInstance().getString(PrefKeys.FILTERS, null) == null) {
                List<Filter> filterList = new ArrayList<Filter>();
                filterList.add(new Filter(doc.getUserId(), doc.getUserName()));
                PrefUtil.getInstance().setString(PrefKeys.FILTERS, GsonUtil.toJson(filterList));
            } else {
                List<Filter> filterList = GsonUtil.getArrayList(PrefUtil.getInstance().getString(PrefKeys.FILTERS, null), Filter.class);
                filterList.add(new Filter(doc.getUserId(), doc.getUserName()));
                PrefUtil.getInstance().setString(PrefKeys.FILTERS, GsonUtil.toJson(filterList));
            }
            DpApp.getRequestQueue().add(new ApiRequest(Method.POST, Config.MOBILE_DP_API_FILTER_POST, null, null) {
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, ""));
                    params.put("targetId", doc.getUserId());
                    params.put("targetNick", doc.getUserName());
                    return params;
                };
            });
            Toast.makeText(getActivity(), getText(R.string.toast_filter_added_message), Toast.LENGTH_SHORT).show();
            break;
        case CONTEXT_MENU_DEL_FILTER:
            List<Filter> filterList = GsonUtil.getArrayList(PrefUtil.getInstance().getString(PrefKeys.FILTERS, null), Filter.class);
            for (Filter filter : filterList) {
                if (filter.getTargetId().equals(doc.getUserId())) {
                    filterList.remove(filter);
                    break;
                }
            }
            Log.i(TAG, "filterList size: " + filterList.size());
            PrefUtil.getInstance().setString(PrefKeys.FILTERS, filterList.isEmpty() ? null : GsonUtil.toJson(filterList));
            DpApp.getRequestQueue().add(new ApiRequest(Method.DELETE, StringUtil.format(Config.MOBILE_DP_API_FILTER_DELETE, PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, ""), doc.getUserId()), null, null));
            Toast.makeText(getActivity(), getText(R.string.toast_filter_removed_message), Toast.LENGTH_SHORT).show();
            break;
        case CONTEXT_MENU_DELETE:
            new ViewDeleteTask(getActivity()).execute(Config.getAbsoluteUrl("/bbs" + doc.getUrl()));
            break;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.document_list_menu, menu);

        // 검색 메뉴 버튼 생성
        searchMenu = (MenuItem) menu.findItem(R.id.menu_search);
        SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
        SearchView searchView = new SearchView(activity.getSupportActionBar().getThemedContext());
        searchView.setQueryHint(getResources().getString(R.string.action_bar_search_hint));
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
        if (mSuggestionsAdapter == null) {
            mSuggestionsAdapter = new DocumentSuggestionsAdapter(activity.getSupportActionBar().getThemedContext(), DocumentSuggestionsAdapter.getCursor(""));
        }
        searchView.setSuggestionsAdapter(mSuggestionsAdapter);
        searchMenu.setActionView(searchView);

        // 새로고침 메뉴 생성
        refreshMenu = (MenuItem) menu.findItem(R.id.menu_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            EasyTracker.getTracker().sendEvent("DocList Menu", "Click", "Home", 0L);
            getActivity().finish();
            return true;
        case R.id.menu_refresh:
            EasyTracker.getTracker().sendEvent("DocList Menu", "Click", "Refresh", 0L);
            item.setActionView(R.layout.indeterminate_progress_action);
            onRefreshList();
            return true;
        case R.id.menu_write:
            EasyTracker.getTracker().sendEvent("DocList Menu", "Click", "Write", 0L);
            Intent i = new Intent(getActivity(), WriteDocumentActivity.class);
            i.putExtra("id", mBbsId);
            startActivity(i);
            return true;
        case R.id.menu_setting:
            EasyTracker.getTracker().sendEvent("DocList Menu", "Click", "Setting", 0L);
            startActivity(new Intent(getSherlockActivity(), SettingActivity.class));
            return true;
        case R.id.menu_about:
            EasyTracker.getTracker().sendEvent("DocList Menu", "Click", "About", 0L);
            startActivity(new Intent(getSherlockActivity(), AboutActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDocumentView(position);
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return search(position, null);
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (mUrl.contains("master_id=127")) {
            mSuggestionsAdapter.changeCursor(DocumentSuggestionsAdapter.getCursor2(query));
        } else {
            mSuggestionsAdapter.changeCursor(DocumentSuggestionsAdapter.getCursor(query));
        }
        mSuggestionsAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return search(0, null);
    }

    private boolean search(int position, String keyword) {
        Cursor c = (Cursor) mSuggestionsAdapter.getItem(position);
        String query = keyword == null ? c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)) : keyword;

        if (query.length() < 2) {
            Toast.makeText(getSherlockActivity(), getResources().getString(R.string.toast_search_suggests_text_limit), Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                // 검색어를 액션바 부제목으로 설정
                getSherlockActivity().getSupportActionBar().setSubtitle(c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2)) + ": " + query);
                query = URLEncoder.encode(query, Config.EUC_KR);
                List<String> excepts = new ArrayList<String>();
                excepts.add("SearchCondition");
                excepts.add("SearchConditionTxt");
                excepts.add("Page");
                mUrl = new StringBuffer().append(StringUtil.removeParameter(mUrl, excepts)).append("&SearchCondition=").append(c.getInt(c.getColumnIndex(BaseColumns._ID))).append("&SearchConditionTxt=").append(query).toString();
                onRefreshList();
            } catch (UnsupportedEncodingException e) {
                Toast.makeText(getSherlockActivity(), getResources().getString(R.string.search_loading_fail), Toast.LENGTH_LONG).show();
                return false;
            }
        }

        searchMenu.collapseActionView();
        return true;
    }

    private void loadPage() {
        if (DpApp.getPage() > 1) {
            refreshMenu.setActionView(R.layout.indeterminate_progress_action);
        }
        mUrl = StringUtil.substringBefore(mUrl, "&Page=") + "&Page=" + DpApp.incPage();
        StringRequest req = new StringRequest(Method.GET, mUrl, createReqSuccessListener(), createReqErrorListener());
        req.setTag(TAG);
        DpApp.getRequestQueue().add(req);
    }

    private Response.Listener<String> createReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    new DocumentListParser(response).execute();
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

    /**
     * 듀얼보기 경우에는 새로운 화면으로 전환하며 글 상세보기를 가로보기일 경우에는 듀얼 화면으로 글 상세보기를 제공한다.
     */
    private void showDocumentView(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {
            if (DpApp.getDocumentList().size() > 0) {
                // showDocumentViewFragment(index);
            }
        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent(getActivity(), DocumentViewActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }

    // @Deprecated
    // private void showDocumentViewFragment(int index) {
    // // We can display everything in-place with fragments, so update
    // // the list to highlight the selected item and show the data.
    // getListView().setItemChecked(index, true);
    // // Check what fragment is currently shown, replace if needed.
    // DocumentViewFragment view = (DocumentViewFragment) getFragmentManager().findFragmentById(R.id.document_view);
    // if (view == null || view.getShownIndex() != index) {
    // // Make new fragment to show this selection.
    // view = DocumentViewFragment.newInstance(index);
    // // Execute a transaction, replacing any existing fragment
    // }
    // // with this one inside the frame.
    // FragmentTransaction ft = getFragmentManager().beginTransaction();
    // ft.replace(R.id.document_view, view);
    // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    // ft.commit();
    // }

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
