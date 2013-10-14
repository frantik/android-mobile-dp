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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.model.Bbs;

/**
 * 즐겨찾기 목록 아답터
 * 
 * @author 작은광명
 *
 */
public class FavoriteListAdapter extends ArrayAdapter<Bbs> {

    private List<Bbs> items;

    public FavoriteListAdapter(Activity activity, List<Bbs> items) {
        super(activity, R.layout.bbs_list_row, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.bbs_list_row, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        holder.bbsTitleTextView.setText(items.get(position).getTitle());

        return v;
    }

    /**
     * The number of items in the list is determined by the number of speeches in our array.
     * 
     * @see android.widget.ListAdapter#getCount()
     */
    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    /**
     * Since the data comes from an array, just returning the index is sufficent to get at the data. If we were using a more complex data structure, we would return whatever object represents one row in the list.
     * 
     * @see android.widget.ListAdapter#getItem(int)
     */
    @Override
    public Bbs getItem(int position) {
        return items.get(position);
    }

    /**
     * Change Items
     * 
     * @param items
     */
    public void setItems(List<Bbs> items) {
        this.items = items;
    }

    /**
     * Use the array index as a unique id.
     * 
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        TextView bbsTitleTextView;
        CheckBox bbsFavoriteCheckBox;

        public ViewHolder(View v) {
            bbsTitleTextView = (TextView) v.findViewById(R.id.bbs_title_textView);
            v.findViewById(R.id.bbs_favorite_checkBox).setVisibility(View.GONE);
        }
    }
}
