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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.database.DBAdapter;
import com.dvdprime.mobile.android.model.Bbs;
import com.dvdprime.mobile.android.ui.MainActivity;
import com.dvdprime.mobile.android.util.StringUtil;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;

/**
 * Bbs List Sticky Header Adapter
 * 
 * @author 작은광명
 * 
 */
public class StickyListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

    private int topId;

    private List<Bbs> bbsList;

    private int[] sectionIndices;

    private String[] sectionStrings;

    private LayoutInflater inflater;

    public StickyListAdapter(Context context, int topId) {
        this.topId = topId;
        inflater = LayoutInflater.from(context);
        bbsList = DBAdapter.getInstance().selectBbsList(topId);
        sectionIndices = getSectionIndices();
        sectionStrings = getTitleStrings();
    }

    private String[] getTitleStrings() {
        List<String> titles = new ArrayList<String>();
        for (int index : sectionIndices) {
            titles.add(bbsList.get(index).getTitle());
        }

        return titles.toArray(new String[titles.size()]);
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        String firstGroup = bbsList.get(0).getGroupTitle();
        sectionIndices.add(0);
        for (int i = 1; i < bbsList.size(); i++) {
            if (!StringUtil.equals(bbsList.get(i).getGroupTitle(), firstGroup)) {
                firstGroup = bbsList.get(i).getGroupTitle();
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    public List<Bbs> getBbsList() {
        return bbsList;
    }

    public void setBbsList(List<Bbs> bbsList) {
        this.bbsList = bbsList;
    }

    @Override
    public int getCount() {
        return bbsList.size();
    }

    @Override
    public Object getItem(int position) {
        return bbsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = inflater.inflate(R.layout.bbs_list_header, parent, false);
        }

        HeaderViewHolder holder = (HeaderViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new HeaderViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        holder.bbsTitleHeader.setText(bbsList.get(position).getGroupTitle());

        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = inflater.inflate(R.layout.bbs_list_row, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        holder.bbsTitleTextView.setText(bbsList.get(position).getTitle());
        holder.bbsFavoriteCheckBox.setChecked(bbsList.get(position).getIsFavorite() > 0);
        holder.bbsFavoriteCheckBox.setTag(R.id.id_position, position);
        holder.bbsFavoriteCheckBox.setTag(R.id.id_uniqid, bbsList.get(position).getUniqId());

        return v;
    }

    @Override
    public long getHeaderId(int position) {
        return bbsList.get(position).getCatId();
    }

    public class HeaderViewHolder {
        TextView bbsTitleHeader;

        public HeaderViewHolder(View v) {
            bbsTitleHeader = (TextView) v.findViewById(R.id.bbs_list_header_textView);
        }
    }

    public class ViewHolder {
        TextView bbsTitleTextView;
        CheckBox bbsFavoriteCheckBox;

        public ViewHolder(View v) {
            bbsTitleTextView = (TextView) v.findViewById(R.id.bbs_title_textView);
            bbsFavoriteCheckBox = (CheckBox) v.findViewById(R.id.bbs_favorite_checkBox);
            bbsFavoriteCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    cb.setChecked(cb.isChecked());
                    int position = (Integer) cb.getTag(R.id.id_position);
                    String uniqId = (String) cb.getTag(R.id.id_uniqid);
                    if (uniqId != null) {
                        if (cb.isChecked()) {
                            if (DBAdapter.getInstance().insertFavoite(uniqId) > 0L) {
                                bbsList.get(position).setIsFavorite(1);
                                MainActivity.mAppSectionsPagerAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (DBAdapter.getInstance().deleteFavorite(uniqId) > 0) {
                                bbsList.get(position).setIsFavorite(0);
                                MainActivity.mAppSectionsPagerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getPositionForSection(int section) {
        if (section >= sectionIndices.length) {
            section = sectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return sectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < sectionIndices.length; i++) {
            if (position < sectionIndices[i]) {
                return i - 1;
            }
        }
        return sectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return sectionStrings;
    }

    public void clear() {
        sectionIndices = new int[0];
        sectionStrings = new String[0];
        bbsList = new ArrayList<Bbs>();
        notifyDataSetChanged();
    }

    public void restore() {
        bbsList = DBAdapter.getInstance().selectBbsList(topId);
        sectionIndices = getSectionIndices();
        sectionStrings = getTitleStrings();
        notifyDataSetChanged();
    }

    public int getSectionStart(int itemPosition) {
        return getPositionForSection(getSectionForPosition(itemPosition));
    }

}
