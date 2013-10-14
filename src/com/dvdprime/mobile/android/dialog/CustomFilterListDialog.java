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
package com.dvdprime.mobile.android.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.PrefKeys;
import com.dvdprime.mobile.android.constants.ResultCode;
import com.dvdprime.mobile.android.model.Account;
import com.dvdprime.mobile.android.model.Filter;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.util.AndroidUtil;
import com.dvdprime.mobile.android.util.GsonUtil;
import com.dvdprime.mobile.android.util.PrefUtil;
import com.dvdprime.mobile.android.util.StringUtil;
import com.dvdprime.mobile.android.volley.ApiRequest;

/**
 * Filter List Dialog
 * 
 * @author 작은광명
 * 
 */
public class CustomFilterListDialog extends Dialog {
    /** 필터 목록 */
    List<Filter> filterList = null;

    public CustomFilterListDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.filter_list_dialog);
        setTitle(R.string.pref_filter_preferences);
        setListEvent();
    }

    private void setListEvent() {
        final ListView mFilterList = (ListView) findViewById(R.id.filter_listView);
        mFilterList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        String filters = PrefUtil.getInstance().getString(PrefKeys.FILTERS, null);

        if (filters == null) {
            AndroidUtil.showToast(getContext(), getContext().getResources().getString(R.string.pref_filter_list_empty));
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    dismiss();
                }
            }, 500);
        } else {
            filterList = GsonUtil.getArrayList(filters, Filter.class);
            final FilterListAdapter mAdapter = new FilterListAdapter(getContext(), filterList);
            mFilterList.setAdapter(mAdapter);
            mFilterList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                    Filter f = mAdapter.getItem(position);
                    f.toggleChecked();
                    ViewHolder viewHolder = (ViewHolder) item.getTag(R.id.id_holder);
                    viewHolder.checkBox.setChecked(f.isChecked());
                }
            });

            final Button mLeftButton = (Button) findViewById(R.id.negative_button);
            final Button mRightButton = (Button) findViewById(R.id.positive_button);

            mLeftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
            mRightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> checkedList = new ArrayList<String>();
                    for (int i = 0; i < mAdapter.getCount(); i++) {
                        if (mAdapter.getItem(i).isChecked()) {
                            checkedList.add(mAdapter.getItem(i).getTargetId());
                        }
                    }
                    if (!checkedList.isEmpty()) {
                        // 필터 삭제 요청한다.
                        String filterUrl = StringUtil.format(Config.MOBILE_DP_API_FILTER_DELETE, PrefUtil.getInstance().getString(PrefKeys.ACCOUNT_ID, ""), StringUtil.join(checkedList, ","));
                        DpApp.getRequestQueue().add(new ApiRequest(Method.DELETE, filterUrl, null, null));
                        List<Filter> tmpList = new ArrayList<Filter>();
                        // 필터 설정 변경사항 반영
                        for (Filter f : filterList) {
                            if (f.isChecked()) {
                                tmpList.add(f);
                            }
                        }
                        filterList.removeAll(tmpList);
                        if (filterList.isEmpty()) {
                            PrefUtil.getInstance().removePref(PrefKeys.FILTERS);
                        } else {
                            PrefUtil.getInstance().setString(PrefKeys.FILTERS, GsonUtil.toJson(filterList));
                        }
                        // 삭제 이벤트 전송
                        EventBusProvider.getInstance().post(new Account(ResultCode.FILTER_REMOVE));
                    }

                    dismiss();
                }
            });
        }
    }

    private class FilterListAdapter extends ArrayAdapter<Filter> {

        public FilterListAdapter(Context context, List<Filter> objects) {
            super(context, R.layout.filter_list_row, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.filter_list_row, null);
            }

            ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

            if (holder == null) {
                holder = new ViewHolder(v);
                v.setTag(R.id.id_holder, holder);
            }

            Filter filter = getItem(position);
            holder.textView.setText(filter.getTargetNick());
            holder.checkBox.setTag(filter.getTargetId());

            return v;
        }

    }

    private class ViewHolder {
        TextView textView;

        CheckBox checkBox;

        public ViewHolder(View v) {
            textView = (TextView) v.findViewById(R.id.filter_id_textView);
            checkBox = (CheckBox) v.findViewById(R.id.filter_checkBox);

            v.setTag(this);
        }
    }
}
