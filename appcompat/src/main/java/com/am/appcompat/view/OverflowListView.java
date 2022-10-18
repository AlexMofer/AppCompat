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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.MenuItem;

import com.am.appcompat.graphics.CanvasCompat;

import java.util.List;

/**
 * 更多面板
 * 最多显示4.5个
 * 根据显示区域控制3.5个、2.5个，再小就往下展示，4.5个
 * Created by Alex on 2018/11/21.
 */
@SuppressLint("ViewConstructor")
final class OverflowListView extends MenuListView {

    private final BoundsAdapter mAdapter;
    private final Rect mDisplayBounds = new Rect();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private final RectF mBounds = new RectF();
    private int mItemMaxWidth;
    private int mItemHeight;
    private float mCornerRadius;

    OverflowListView(Context context, BoundsAdapter adapter, OnOverflowListener listener) {
        super(context);
        mAdapter = adapter;
        setWillNotDraw(false);
        final Resources resources = context.getResources();
        final int overflowMinimumWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 48, resources.getDisplayMetrics());

        setItemMinimumWidth(overflowMinimumWidth);

        setOnItemClickListener((parent, view, position, id) ->
                listener.onOverflowItemClick(mDataAdapter.getItem(position)));

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

    void setData(List<MenuItem> items) {
        mDataAdapter.clear();
        mItemMaxWidth = 0;
        for (MenuItem item : items) {
            if (item != null) {
                mDataAdapter.add(item);
                mCalculator.setData(item);
                mCalculator.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                if (mItemHeight <= 0) {
                    mItemHeight = mCalculator.getMeasuredHeight();
                }
                mItemMaxWidth = Math.max(mItemMaxWidth, mCalculator.getMeasuredWidth());
            }
        }
    }

    // 返回true时表明可放置在上半部分，false表明只能放置在下半部分
    boolean measure(int maxWidth, int topPartHeight, int bottomPartHeight) {
        if (mDataAdapter.isEmpty()) {
            setMeasuredDimension(0, 0);
            return true;
        }
        final int width = Math.min(maxWidth, mItemMaxWidth);
        final int count = mDataAdapter.getCount();
        int height;
        if (count > 4) {
            height = Math.round(mItemHeight * 4.5f);
            if (topPartHeight >= height) {
                setMeasuredDimension(width, height);
                return true;
            }
            height = Math.round(mItemHeight * 3.5f);
            if (topPartHeight >= height) {
                setMeasuredDimension(width, height);
                return true;
            }
            height = Math.round(mItemHeight * 2.5f);
            if (topPartHeight >= height) {
                setMeasuredDimension(width, height);
                return true;
            }
        } else if (count > 3) {
            height = mItemHeight * count;
            if (topPartHeight >= height) {
                setMeasuredDimension(width, height);
                return true;
            }
            height = Math.round(mItemHeight * 3.5f);
            if (topPartHeight >= height) {
                setMeasuredDimension(width, height);
                return true;
            }
            height = Math.round(mItemHeight * 2.5f);
            if (topPartHeight >= height) {
                setMeasuredDimension(width, height);
                return true;
            }
        } else if (count > 2) {
            height = mItemHeight * count;
            if (topPartHeight >= height) {
                setMeasuredDimension(width, height);
                return true;
            }
            height = Math.round(mItemHeight * 2.5f);
            if (topPartHeight >= height) {
                setMeasuredDimension(width, height);
                return true;
            }
        } else {
            height = mItemHeight * count;
            if (topPartHeight >= height) {
                setMeasuredDimension(width, height);
                return true;
            }
        }
        if (count > 4) {
            height = Math.round(mItemHeight * 4.5f);
            if (bottomPartHeight >= height) {
                setMeasuredDimension(width, height);
                return false;
            }
            height = Math.round(mItemHeight * 3.5f);
            if (bottomPartHeight >= height) {
                setMeasuredDimension(width, height);
                return false;
            }
            height = Math.round(mItemHeight * 2.5f);
            if (bottomPartHeight >= height) {
                setMeasuredDimension(width, height);
                return false;
            }
        } else if (count > 3) {
            height = mItemHeight * count;
            if (bottomPartHeight >= height) {
                setMeasuredDimension(width, height);
                return false;
            }
            height = Math.round(mItemHeight * 3.5f);
            if (bottomPartHeight >= height) {
                setMeasuredDimension(width, height);
                return false;
            }
            height = Math.round(mItemHeight * 2.5f);
            if (bottomPartHeight >= height) {
                setMeasuredDimension(width, height);
                return false;
            }
        } else if (count > 2) {
            height = mItemHeight * count;
            if (bottomPartHeight >= height) {
                setMeasuredDimension(width, height);
                return false;
            }
            height = Math.round(mItemHeight * 2.5f);
            if (bottomPartHeight >= height) {
                setMeasuredDimension(width, height);
                return false;
            }
        } else {
            height = mItemHeight * count;
            if (bottomPartHeight >= height) {
                setMeasuredDimension(width, height);
                return false;
            }
        }
        setMeasuredDimension(width, bottomPartHeight);
        return false;
    }

    boolean isEmpty() {
        return mDataAdapter.isEmpty();
    }

    void refresh() {
        final float state = mAdapter.getState();
        mAdapter.getViewDisplayFrame(this, mDisplayBounds);
        final int displayLeft = mDisplayBounds.left;
        final int displayTop = mDisplayBounds.top;
        if (state == 0) {
            mAdapter.getMainDisplayFrame(mDisplayBounds);
            setAlpha(0);
        } else if (state == 1) {
            mAdapter.getOverflowDisplayFrame(mDisplayBounds);
            setAlpha(1);
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
            setAlpha(state);
        }
        //noinspection ConstantConditions
        mDisplayBounds.set(mDisplayBounds.left - displayLeft,
                mDisplayBounds.top - displayTop,
                mDisplayBounds.right - displayLeft,
                mDisplayBounds.bottom - displayTop);
        mCornerRadius = mAdapter.getCornerRadius();
        invalidate();
    }

    public interface OnOverflowListener {
        void onOverflowItemClick(MenuItem item);
    }
}
