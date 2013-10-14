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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.adapter.FavoriteListAdapter;
import com.dvdprime.mobile.android.adapter.StickyListAdapter;
import com.dvdprime.mobile.android.database.DBAdapter;
import com.dvdprime.mobile.android.database.DBConst;
import com.dvdprime.mobile.android.model.Bbs;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView.OnHeaderClickListener;

/**
 * BBS List Fragment
 * 
 * @author 작은광명
 * 
 */
public class BbsListFragment extends Fragment implements AdapterView.OnItemClickListener, OnHeaderClickListener {

    /** BBS Top Id */
    private int topId = 0;

    /** 즐겨찾기 게시판 아답터 */
    private FavoriteListAdapter mFavoriteAdapter;

    /** 게시판 목록 아답터 */
    private StickyListAdapter mStickyAdapter;

    /** 게시판 목록 헤더 리스트 */
    private StickyListHeadersListView mStickyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        topId = getArguments().getInt(DBConst.Bbs.TOP_ID);
        if (topId == 0) {
            mFavoriteAdapter = new FavoriteListAdapter(getActivity(), DBAdapter.getInstance().selectBbsList(topId));
            v = inflater.inflate(R.layout.fragment_favorite_list, container, false);
            ListView mFavoriteList = (ListView) v.findViewById(R.id.bbs_favorite_list);
            mFavoriteList.setOnItemClickListener(this);
            mFavoriteList.setEmptyView(v.findViewById(R.id.empty_favorite));
            mFavoriteList.setAdapter(mFavoriteAdapter);
        } else {
            mStickyAdapter = new StickyListAdapter(getActivity(), topId);
            v = inflater.inflate(R.layout.fragment_bbs_list, container, false);
            mStickyList = (StickyListHeadersListView) v.findViewById(R.id.bbs_list);
            mStickyList.setOnItemClickListener(this);
            mStickyList.setOnHeaderClickListener(this);
            mStickyList.setFastScrollEnabled(false);
            mStickyList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            mStickyList.setAdapter(mStickyAdapter);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (topId == 0) {
            mFavoriteAdapter.setItems(DBAdapter.getInstance().selectBbsList(topId));
            mFavoriteAdapter.notifyDataSetChanged();
        }
        if (mStickyAdapter != null) {
            mStickyAdapter.setBbsList(DBAdapter.getInstance().selectBbsList(topId));
            mStickyAdapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 게시판 목록 호출
        Bbs bbs = null;
        if (topId == 0) {
            bbs = mFavoriteAdapter.getItem(position);
        } else {
            bbs = mStickyAdapter.getBbsList().get(position);
        }
        Intent i = new Intent(this.getActivity(), DocumentListActivity.class);
        i.putExtra("id", bbs.getUniqId());
        i.putExtra("url", bbs.getTargetUrl());
        i.putExtra("title", bbs.getTitle());
        startActivity(i);
        // 게시판의 로그인 필요여부 설정
        DpApp.setNeedLogin(bbs.getLoginCheck() > 0);
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
        mStickyList.smoothScrollToPositionFromTop(mStickyAdapter.getSectionStart(itemPosition) + mStickyList.getHeaderViewsCount(), -mStickyList.getPaddingTop());
    }

}
