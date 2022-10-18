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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.am.appcompat.graphics.CanvasCompat;

import java.util.List;


/**
 * 主面板
 * Created by Alex on 2018/11/21.
 */
@SuppressLint("ViewConstructor")
final class MainLayout extends LinearLayout {

    private final BoundsAdapter mAdapter;
    private final OnMainListener mListener;

    private final Rect mDisplayBounds = new Rect();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private final RectF mBounds = new RectF();
    private float mCornerRadius;

    MainLayout(Context context, BoundsAdapter adapter, OnMainListener listener) {
        super(context);
        mAdapter = adapter;
        mListener = listener;

        setWillNotDraw(false);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        mPath.setFillType(Path.FillType.EVEN_ODD);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public void draw(Canvas canvas) {
        final int width = getWidth();
        final int height = getHeight();
        final int layer = CanvasCompat.saveLayer(canvas, 0, 0, getWidth(), getHeight(),
                null);
        super.draw(canvas);
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.lineTo(width, 0);
        mPath.lineTo(width, height);
        mPath.lineTo(0, height);
        mPath.close();
        mBounds.set(mDisplayBounds);
        mPath.addRoundRect(mBounds, mCornerRadius, mCornerRadius, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);
        canvas.restoreToCount(layer);
    }

    void setData(List<MenuItem> items, int maxWidth, int overflowButtonWidth) {
        int index = 0;
        int width = 0;
        while (!items.isEmpty()) {
            final MenuItem item = items.remove(0);
            if (item == null) {
                continue;
            }
            final boolean more = !items.isEmpty();
            final MenuItemView button = getChildAt(index, item);
            button.setFirst(index == 0);
            button.setLast(!more);
            button.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            final int itemWidth = button.getMeasuredWidth();
            if (more) {
                // 存在更多菜单
                if (width + itemWidth + overflowButtonWidth > maxWidth) {
                    // 宽度放不下
                    items.add(0, item);
                    removeChildren(index);
                    break;
                }
                width += itemWidth;
                index++;
            } else {
                // 最后一个菜单
                if (width + itemWidth > maxWidth) {
                    // 宽度放不下
                    items.add(0, item);
                    removeChildren(index);
                    break;
                }
            }
        }
        removeChildren(index + 1);
        requestLayout();
        invalidate();
    }

    private MenuItemView getChildAt(int index, MenuItem item) {
        View child = getChildAt(index);
        if (child == null) {
            child = new MenuItemView(getContext());
            child.setOnClickListener(v -> mListener.onMainItemClick((MenuItem) v.getTag()));
            addView(child, index);
        }
        final MenuItemView view = (MenuItemView) child;
        view.setData(item);
        view.setTag(item);
        return (MenuItemView) child;
    }

    private void removeChildren(int index) {
        int count = getChildCount();
        while (count > index) {
            final View child = getChildAt(index);
            child.setTag(null);
            child.setOnClickListener(null);
            removeViewInLayout(child);
            count = getChildCount();
        }
    }

    void refresh() {
        final float state = mAdapter.getState();
        mAdapter.getViewDisplayFrame(this, mDisplayBounds);
        final int displayLeft = mDisplayBounds.left;
        final int displayTop = mDisplayBounds.top;
        if (state == 0) {
            mAdapter.getMainDisplayFrame(mDisplayBounds);
            setAlpha(1);
        } else if (state == 1) {
            mAdapter.getOverflowDisplayFrame(mDisplayBounds);
            setAlpha(0);
        } else {
            mAdapter.getMainDisplayFrame(mDisplayBounds);
            final int mainLeft = mDisplayBounds.left;
            final int mainTop = mDisplayBounds.top;
            final int mainRight = mDisplayBounds.right;
            final int mainBottom = mDisplayBounds.bottom;
            mAdapter.getOverflowDisplayFrame(mDisplayBounds);
            final int overflowLeft = mDisplayBounds.left;
            final int overflowTop = mDisplayBounds.top;
            final int overflowRight = mDisplayBounds.right;
            final int overflowBottom = mDisplayBounds.bottom;
            //noinspection ConstantConditions
            mDisplayBounds.set(Math.round(mainLeft + (overflowLeft - mainLeft) * state),
                    Math.round(mainTop + (overflowTop - mainTop) * state),
                    Math.round(mainRight + (overflowRight - mainRight) * state),
                    Math.round(mainBottom + (overflowBottom - mainBottom) * state));
            setAlpha(1 - state);
        }
        //noinspection ConstantConditions
        mDisplayBounds.set(mDisplayBounds.left - displayLeft,
                mDisplayBounds.top - displayTop,
                mDisplayBounds.right - displayLeft,
                mDisplayBounds.bottom - displayTop);
        mCornerRadius = mAdapter.getCornerRadius();
        invalidate();
    }

    public interface OnMainListener {
        void onMainItemClick(MenuItem item);
    }
}
