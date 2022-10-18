/*
 * Copyright (C) 2022 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.am.appcompat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * 列表型面板
 * Created by Alex on 2018/11/21.
 */
class MenuListView extends ListView {

    final Adapter mDataAdapter = new Adapter();
    final MenuItemView mCalculator;
    private int mItemMinimumWidth = 0;

    MenuListView(Context context) {
        super(context);
        setDivider(null);
        setDividerHeight(0);
        final TypedArray a = context.obtainStyledAttributes(
                new int[]{android.R.attr.selectableItemBackground});
        setSelector(a.getResourceId(0, 0));
        a.recycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            setScrollBarDefaultDelayBeforeFade(ViewConfiguration.getScrollDefaultDelay() * 3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            setScrollIndicators(View.SCROLL_INDICATOR_TOP | View.SCROLL_INDICATOR_BOTTOM);
        setAdapter(mDataAdapter);
        mCalculator = new MenuItemView(context);
        mCalculator.setFirst(true);
        mCalculator.setLast(true);
    }

    void setItemMinimumWidth(int minWidth) {
        if (minWidth <= 0)
            return;
        mItemMinimumWidth = minWidth;
        mCalculator.setMinimumWidth(minWidth);
    }

    void awakenScrollBar() {
        awakenScrollBars(ViewConfiguration.getScrollDefaultDelay() * 3, true);
    }

    class Adapter extends BaseAdapter {

        private final ArrayList<MenuItem> mItems = new ArrayList<>();

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public MenuItem getItem(int position) {
            return position < 0 | position >= mItems.size() ? null : mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new MenuItemView(parent.getContext());
            }
            final MenuItemView button = (MenuItemView) convertView;
            button.setFirst(true);
            button.setLast(true);
            if (mItemMinimumWidth > 0)
                button.setMinimumWidth(mItemMinimumWidth);
            final MenuItem item = getItem(position);
            button.setData(item);
            convertView.setTag(item);
            return convertView;
        }

        void clear() {
            mItems.clear();
        }

        void add(MenuItem item) {
            mItems.add(item);
        }
    }
}
