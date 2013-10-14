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
import com.dvdprime.mobile.android.model.Gcm;
import com.dvdprime.mobile.android.util.DateUtil;

/**
 * Notification List Adapter
 * 
 * @author 작은광명
 * 
 */
public class NotificationListAdapter extends ArrayAdapter<Gcm> {

    public NotificationListAdapter(Context context, ArrayList<Gcm> objects) {
        super(context, R.layout.notification_list_row, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.notification_list_row, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        Gcm gcm = getItem(position);

        if (position > 0 && DateUtil.getHeaderDay(getItem(position - 1).getCreationTime()).equals(DateUtil.getHeaderDay(gcm.getCreationTime()))) {
            holder.header.setVisibility(View.GONE);
        } else {
            holder.header.setVisibility(View.VISIBLE);
        }

        holder.header.setText(DateUtil.getHeaderDay(gcm.getCreationTime()));
        holder.message.setText(gcm.getMessage());
        holder.title.setText(gcm.getTitle());
        holder.date.setText(DateUtil.getTimeAgoByTimestamp(gcm.getCreationTime()));

        return v;
    }

    private class ViewHolder {
        TextView header;
        TextView message;
        TextView title;
        TextView date;

        public ViewHolder(View v) {
            header = (TextView) v.findViewById(R.id.notification_list_header);
            message = (TextView) v.findViewById(R.id.notification_list_message);
            title = (TextView) v.findViewById(R.id.notification_list_title);
            date = (TextView) v.findViewById(R.id.notification_list_date);

            v.setTag(this);
        }
    }

}
