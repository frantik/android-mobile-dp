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
package com.dvdprime.mobile.android.adapter;

import com.dvdprime.mobile.android.R;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Document List Actionbar Suggestion Adapter
 * 
 * @author 작은광명
 * 
 */
public class DocumentSuggestionsAdapter extends CursorAdapter {

    public DocumentSuggestionsAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv1 = (TextView) view.findViewById(android.R.id.text1);
        tv1.setText(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)));
        TextView tv2 = (TextView) view.findViewById(android.R.id.text2);
        tv2.setTextAppearance(context, R.style.DarkGrayBaseSmallText);
        tv2.setText(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
        v.setPadding(30, 5, 5, 5);
        return v;
    }

    public static MatrixCursor getCursor(String query) {
        // 검색 목록 자동완성 컬럼
        final String[] COLUMNS = { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2 };

        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(new Object[] { 1, query, "제목 검색" });
        cursor.addRow(new Object[] { 2, query, "ID로 검색" });
        cursor.addRow(new Object[] { 6, query, "닉네임 검색" });
        cursor.addRow(new Object[] { 4, query, "말머리 검색" });
        cursor.addRow(new Object[] { 7, query, "글 태그 검색" });

        return cursor;
    }

    public static MatrixCursor getCursor2(String query) {
        // 검색 목록 자동완성 컬럼
        final String[] COLUMNS = { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2 };

        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(new Object[] { 1, query, "제목 검색" });
        cursor.addRow(new Object[] { 2, query, "ID로 검색" });
        cursor.addRow(new Object[] { 4, query, "말머리 검색" });
        cursor.addRow(new Object[] { 7, query, "글 태그 검색" });

        return cursor;
    }
}
