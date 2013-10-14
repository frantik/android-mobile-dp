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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.dvdprime.mobile.android.database.DBConst;
import com.dvdprime.mobile.android.ui.BbsListFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary sections of the app.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    public static final String[] tabEngTitles = new String[] { "Favorite", "Community", "Hardware", "Software", "Blueray", "SmartPhone" };
    public static final String[] tabTitles = new String[] { "즐겨찾기", "커뮤니티", "하드웨어 포럼", "소프트 포럼", "블루레이 포럼", "스마트폰" };

    public AppSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        BbsListFragment bbsList = new BbsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DBConst.Bbs.TOP_ID, i);
        bbsList.setArguments(bundle);

        return bbsList;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    SparseArray<Fragment> frags = new SparseArray<Fragment>();

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment f = (Fragment) super.instantiateItem(container, position);
        frags.put(position, f);
        return f;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        frags.remove(position);
    }

    @Override
    public void notifyDataSetChanged() {
        if (frags.get(0) != null) {
            frags.get(0).onResume();
        }
        super.notifyDataSetChanged();
    }
}
