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
import android.util.TypedValue;
import android.view.ViewGroup;

import com.am.appcompat.graphics.CanvasCompat;


/**
 * 切换按钮
 * Created by Alex on 2018/11/21.
 */
@SuppressLint("ViewConstructor")
final class ToggleLayout extends ViewGroup {

    private final BoundsAdapter mAdapter;
    private final ToggleView mToggle;
    private final int mSize;
    private final Rect mDisplayBounds = new Rect();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private final RectF mBounds = new RectF();
    private float mState;
    private float mCornerRadius;

    ToggleLayout(Context context, BoundsAdapter adapter, OnClickListener listener) {
        super(context);
        mAdapter = adapter;

        setWillNotDraw(false);
        mToggle = new ToggleView(context);
        addView(mToggle);
        mSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                context.getResources().getDisplayMetrics());
        mToggle.setOnClickListener(listener);

        mPath.setFillType(Path.FillType.EVEN_ODD);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mToggle.measure(MeasureSpec.makeMeasureSpec(mSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mSize, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getWidth();
        final int top = Math.round(getHeight() * 0.5f - mSize * 0.5f);
        if (mState == 0) {
            mToggle.layout(width - mSize, top, width, top + mSize);
        } else if (mState == 1) {
            mToggle.layout(0, top, mSize, top + mSize);
        } else {
            final int left = Math.round((width - mSize) * (1 - mState));
            mToggle.layout(left, top, left + mSize, top + mSize);
        }
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

    int getSize() {
        return mSize;
    }

    void refresh() {
        mState = mAdapter.getState();
        mAdapter.getViewDisplayFrame(this, mDisplayBounds);
        final int displayLeft = mDisplayBounds.left;
        final int displayTop = mDisplayBounds.top;
        if (mState == 0) {
            mAdapter.getMainDisplayFrame(mDisplayBounds);
        } else if (mState == 1) {
            mAdapter.getOverflowDisplayFrame(mDisplayBounds);
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
            mDisplayBounds.set(Math.round(mainLeft + (overflowLeft - mainLeft) * mState),
                    Math.round(mainTop + (overflowTop - mainTop) * mState),
                    Math.round(mainRight + (overflowRight - mainRight) * mState),
                    Math.round(mainBottom + (overflowBottom - mainBottom) * mState));
        }
        //noinspection ConstantConditions
        mDisplayBounds.set(mDisplayBounds.left - displayLeft,
                mDisplayBounds.top - displayTop,
                mDisplayBounds.right - displayLeft,
                mDisplayBounds.bottom - displayTop);
        mCornerRadius = mAdapter.getCornerRadius();
        requestLayout();
        invalidate();
    }

    void setOverflow() {
        mToggle.setOverflow(true);
    }

    void setBack() {
        mToggle.setBack(true);
    }
}
