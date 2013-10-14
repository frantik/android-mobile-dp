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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.model.Document;
import com.dvdprime.mobile.android.util.StringUtil;

/**
 * Document List Adapter
 * 
 * @author 작은광명
 * 
 */
public class DocumentListAdapter extends ArrayAdapter<Document> {

    public DocumentListAdapter(Context context, ArrayList<Document> objects) {
        super(context, R.layout.document_list_row, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.document_list_row, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        Document doc = getItem(position);
        StringBuffer title = new StringBuffer().append(doc.getTitle());
        if (StringUtil.toNumber(doc.getCommentCount()) > 0) {
            title.append("(").append(doc.getCommentCount()).append(")");
        }
        holder.title.setText(title.toString());
        holder.no.setText(doc.getNo());

        if (StringUtil.equals(doc.getNo(), "HOT")) {
            holder.no.setTextAppearance(getContext(), R.style.RedBaseSmallText);
        } else if (StringUtil.equals(doc.getNo(), "COOL")) {
            holder.no.setTextAppearance(getContext(), R.style.BlueBaseSmallText);
        } else if (StringUtil.equals(doc.getNo(), getContext().getString(R.string.list_notice_no))) {
            holder.no.setTextAppearance(getContext(), R.style.OrangeBaseSmallText);
        } else {
            holder.no.setTextAppearance(getContext(), R.style.GrayBaseSmallText);
        }

        if (StringUtil.toNumber(doc.getRecommendCount()) > 0) {
            holder.rcmd.setText(StringUtil.formattedNumber(doc.getRecommendCount()));
            holder.rcmd.setVisibility(View.VISIBLE);
        } else {
            holder.rcmd.setVisibility(View.INVISIBLE);
        }

        holder.name.setText(doc.getUserName());
        holder.count.setText(StringUtil.formattedNumber(doc.getVisitCount()));
        holder.date.setText(doc.getDate());

        return v;
    }

    private class ViewHolder {
        TextView title;
        TextView no;
        TextView name;
        TextView rcmd;
        TextView count;
        TextView date;

        public ViewHolder(View v) {
            title = (TextView) v.findViewById(R.id.document_list_title);
            no = (TextView) v.findViewById(R.id.document_list_no);
            name = (TextView) v.findViewById(R.id.document_list_user_name);
            rcmd = (TextView) v.findViewById(R.id.document_list_recommend);
            count = (TextView) v.findViewById(R.id.document_list_count);
            date = (TextView) v.findViewById(R.id.document_list_write_date);

            v.setTag(this);
        }
    }
}
