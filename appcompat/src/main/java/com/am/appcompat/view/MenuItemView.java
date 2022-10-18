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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

/**
 * 面板子项视图
 * Created by Alex on 2018/11/21.
 */
final class MenuItemView extends LinearLayout {

    private final int mPadding;
    private final int mPaddingEdge;
    private final ImageView mIcon;
    private final TextView mText;
    private final int mPaddingText;
    private boolean mFirst = false;
    private boolean mLast = false;

    MenuItemView(Context context) {
        super(context);
        final Resources resources = context.getResources();
        final int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                resources.getDisplayMetrics());
        final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11,
                resources.getDisplayMetrics());
        final int paddingEdge = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16.5f,
                resources.getDisplayMetrics());
        final int paddingIcon = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                resources.getDisplayMetrics());
        final int widthIcon = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                resources.getDisplayMetrics());
        final int paddingText = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11,
                resources.getDisplayMetrics());
        final int textColor = Color.BLACK;
        final float textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                resources.getDisplayMetrics());

        final TypedArray a = context.obtainStyledAttributes(
                new int[]{android.R.attr.selectableItemBackground});
        setBackgroundResource(a.getResourceId(0, 0));
        a.recycle();

        setOrientation(HORIZONTAL);
        setMinimumWidth(size);
        setMinimumHeight(size);

        mPadding = padding;
        mPaddingEdge = paddingEdge;
        mPaddingText = paddingText;
        updatePadding();

        mIcon = new ImageView(context);
        ViewCompat.setBackground(mIcon, null);
        mIcon.setFocusable(false);
        mIcon.setFocusableInTouchMode(false);
        mIcon.setPadding(paddingIcon, 0, paddingIcon, 0);
        mIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (Build.VERSION.SDK_INT >= 16) {
            mIcon.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
        addView(mIcon, new LayoutParams(widthIcon, size));

        mText = new TextView(context);
        final TypedArray a1 = context.obtainStyledAttributes(
                new int[]{android.R.attr.textAppearanceListItemSmall});
        mText.setTextAppearance(context, a1.getResourceId(0, 0));
        a1.recycle();
        ViewCompat.setBackground(mText, null);
        mText.setEllipsize(TextUtils.TruncateAt.END);
        mText.setFocusable(false);
        mText.setFocusableInTouchMode(false);
        mText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        mText.setGravity(Gravity.CENTER);
        mText.setSingleLine();
        mText.setTextColor(textColor);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        if (Build.VERSION.SDK_INT >= 16) {
            mText.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
        addView(mText, new LayoutParams(LayoutParams.WRAP_CONTENT, size));

    }

    void setData(MenuItem item) {
        mText.setEllipsize(null);
        final Drawable icon = item.getIcon();
        final CharSequence title = item.getTitle();
        if (icon != null && title != null) {
            mIcon.setVisibility(VISIBLE);
            mIcon.setImageDrawable(icon);
            mText.setVisibility(VISIBLE);
            if (Build.VERSION.SDK_INT >= 16) {
                mText.setPaddingRelative(mPaddingText, 0, 0, 0);
            } else {
                mText.setPadding(mPaddingText, 0, 0, 0);
            }
            mText.setText(title);
        } else if (icon != null) {
            mIcon.setVisibility(VISIBLE);
            mIcon.setImageDrawable(icon);
            mText.setVisibility(GONE);
        } else {
            mIcon.setVisibility(GONE);
            mText.setVisibility(VISIBLE);
            mText.setPadding(0, 0, 0, 0);
            mText.setText(title);
        }
    }

    void setFirst(boolean first) {
        if (mFirst == first)
            return;
        mFirst = first;
        updatePadding();
    }

    void setLast(boolean last) {
        if (mLast == last)
            return;
        mLast = last;
        updatePadding();
    }

    private void updatePadding() {
        if (mFirst && mLast)
            setPaddingCompat(mPaddingEdge, 0, mPaddingEdge, 0);
        else if (mFirst)
            setPaddingCompat(mPaddingEdge, 0, mPadding, 0);
        else if (mLast)
            setPaddingCompat(mPadding, 0, mPaddingEdge, 0);
        else
            setPaddingCompat(mPadding, 0, mPadding, 0);
    }

    @SuppressWarnings("SameParameterValue")
    private void setPaddingCompat(int start, int top, int end, int bottom) {
        if (Build.VERSION.SDK_INT >= 17) {
            setPaddingRelative(start, top, end, bottom);
        } else {
            setPadding(start, top, end, bottom);
        }
    }
}
