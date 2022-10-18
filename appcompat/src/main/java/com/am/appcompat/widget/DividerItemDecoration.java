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
package com.am.appcompat.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 分割子项装饰
 * Created by Alex on 2022/7/13.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Rect tBounds = new Rect();
    private int mGapBeginning;
    private int mGapMiddle;
    private int mGapEnd;
    private Drawable mDrawableBeginning;
    private Drawable mDrawableMiddle;
    private Drawable mDrawableEnd;

    public DividerItemDecoration(int beginning, int middle, int end) {
        mGapBeginning = Math.max(0, beginning);
        mGapMiddle = Math.max(0, middle);
        mGapEnd = Math.max(0, end);
    }

    public DividerItemDecoration(int middle) {
        this(0, middle, 0);
    }

    public DividerItemDecoration(@Nullable Drawable beginning, @Nullable Drawable middle,
                                 @Nullable Drawable end) {
        mDrawableBeginning = beginning;
        mDrawableMiddle = middle;
        mDrawableEnd = end;
    }

    public DividerItemDecoration(@Nullable Drawable middle) {
        this(null, middle, null);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (!(manager instanceof LinearLayoutManager)) {
            return;
        }
        outRect.left = outRect.top = outRect.right = outRect.bottom = 0;
        if (((LinearLayoutManager) manager).getOrientation() == LinearLayoutManager.VERTICAL) {
            // 垂直
            outRect.bottom = Math.max(mGapMiddle, mDrawableMiddle == null ? 0 :
                    mDrawableMiddle.getIntrinsicHeight());
            final int position = manager.getPosition(view);
            final int itemCount = manager.getItemCount();
            if (position == 0) {
                // 首项
                outRect.top = Math.max(mGapBeginning, mDrawableBeginning == null ? 0 :
                        mDrawableBeginning.getIntrinsicHeight());
            }
            if (position == itemCount - 1) {
                // 尾项
                outRect.bottom = Math.max(mGapEnd, mDrawableEnd == null ? 0 :
                        mDrawableEnd.getIntrinsicHeight());
            }
        } else {
            // 水平
            outRect.right = Math.max(mGapMiddle, mDrawableMiddle == null ? 0 :
                    mDrawableMiddle.getIntrinsicWidth());
            final int position = manager.getPosition(view);
            final int itemCount = manager.getItemCount();
            if (position == 0) {
                // 首项
                outRect.left = Math.max(mGapBeginning, mDrawableBeginning == null ? 0 :
                        mDrawableBeginning.getIntrinsicWidth());
            }
            if (position == itemCount - 1) {
                // 尾项
                outRect.right = Math.max(mGapEnd, mDrawableEnd == null ? 0 :
                        mDrawableEnd.getIntrinsicWidth());
            }
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (!(manager instanceof LinearLayoutManager)) {
            return;
        }
        if (mDrawableBeginning == null && mDrawableMiddle == null && mDrawableEnd == null) {
            return;
        }
        if (((LinearLayoutManager) manager).getOrientation() == LinearLayoutManager.VERTICAL) {
            // 垂直
            onDrawVertical(c, parent, (LinearLayoutManager) manager);
        } else {
            // 水平
            onDrawHorizontal(c, parent, (LinearLayoutManager) manager);
        }
    }

    private void onDrawVertical(@NonNull Canvas c, @NonNull RecyclerView parent,
                                @NonNull LinearLayoutManager manager) {
        final int paddingLeft = parent.getPaddingLeft();
        final float centerX = paddingLeft +
                (parent.getWidth() - paddingLeft - parent.getPaddingRight()) * 0.5f;
        final int itemCount = manager.getItemCount();
        final Rect rect = tBounds;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final int position = manager.getPosition(child);
            manager.getDecoratedBoundsWithMargins(child, rect);
            if (position == 0 && mDrawableBeginning != null) {
                final int drawableWidth = mDrawableBeginning.getIntrinsicWidth();
                final int drawableHeight = mDrawableBeginning.getIntrinsicHeight();
                c.save();
                c.translate(centerX - drawableWidth * 0.5f,
                        rect.top + child.getTranslationY());
                mDrawableBeginning.setBounds(0, 0, drawableWidth, drawableHeight);
                mDrawableBeginning.draw(c);
                c.restore();
            }
            if (position == itemCount - 1) {
                // 尾项
                if (mDrawableEnd != null) {
                    final int drawableWidth = mDrawableEnd.getIntrinsicWidth();
                    final int drawableHeight = mDrawableEnd.getIntrinsicHeight();
                    c.save();
                    c.translate(centerX - drawableWidth * 0.5f,
                            rect.bottom + child.getTranslationY() - drawableHeight);
                    mDrawableEnd.setBounds(0, 0, drawableWidth, drawableHeight);
                    mDrawableEnd.draw(c);
                    c.restore();
                }
            } else {
                if (mDrawableMiddle != null) {
                    final int drawableWidth = mDrawableMiddle.getIntrinsicWidth();
                    final int drawableHeight = mDrawableMiddle.getIntrinsicHeight();
                    c.save();
                    c.translate(centerX - drawableWidth * 0.5f,
                            rect.bottom + child.getTranslationY() - drawableHeight);
                    mDrawableMiddle.setBounds(0, 0, drawableWidth, drawableHeight);
                    mDrawableMiddle.draw(c);
                    c.restore();
                }
            }
        }
    }

    private void onDrawHorizontal(@NonNull Canvas c, @NonNull RecyclerView parent,
                                  @NonNull LinearLayoutManager manager) {
        final int paddingTop = parent.getPaddingTop();
        final float centerY = paddingTop +
                (parent.getHeight() - paddingTop - parent.getPaddingBottom()) * 0.5f;
        final int itemCount = manager.getItemCount();
        final Rect rect = tBounds;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final int position = manager.getPosition(child);
            manager.getDecoratedBoundsWithMargins(child, rect);
            if (position == 0 && mDrawableBeginning != null) {
                final int drawableWidth = mDrawableBeginning.getIntrinsicWidth();
                final int drawableHeight = mDrawableBeginning.getIntrinsicHeight();
                c.save();
                c.translate(rect.left + child.getTranslationX(),
                        centerY - drawableWidth * 0.5f);
                mDrawableBeginning.setBounds(0, 0, drawableWidth, drawableHeight);
                mDrawableBeginning.draw(c);
                c.restore();
            }
            if (position == itemCount - 1) {
                // 尾项
                if (mDrawableEnd != null) {
                    final int drawableWidth = mDrawableEnd.getIntrinsicWidth();
                    final int drawableHeight = mDrawableEnd.getIntrinsicHeight();
                    c.save();
                    c.translate(
                            rect.right + child.getTranslationX() - drawableWidth,
                            centerY - drawableWidth * 0.5f);
                    mDrawableEnd.setBounds(0, 0, drawableWidth, drawableHeight);
                    mDrawableEnd.draw(c);
                    c.restore();
                }
            } else {
                if (mDrawableMiddle != null) {
                    final int drawableWidth = mDrawableMiddle.getIntrinsicWidth();
                    final int drawableHeight = mDrawableMiddle.getIntrinsicHeight();
                    c.save();
                    c.translate(
                            rect.right + child.getTranslationX() - drawableWidth,
                            centerY - drawableWidth * 0.5f);
                    mDrawableMiddle.setBounds(0, 0, drawableWidth, drawableHeight);
                    mDrawableMiddle.draw(c);
                    c.restore();
                }
            }
        }
    }
}
