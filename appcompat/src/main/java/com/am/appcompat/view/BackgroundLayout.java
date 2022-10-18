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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.core.view.ViewCompat;

/**
 * 背景布局
 * Created by Alex on 2018/11/21.
 */
@SuppressLint("ViewConstructor")
final class BackgroundLayout extends ViewGroup {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private final BoundsAdapter mAdapter;
    private final int mShadowPadding;
    private final View mBackground;
    private final float mCornerRadius;
    private final Rect mBounds = new Rect();
    private final RectF mElevation = new RectF();

    public BackgroundLayout(Context context, BoundsAdapter adapter) {
        super(context);
        mAdapter = adapter;

        final Resources resources = context.getResources();
        int color = Color.WHITE;
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                resources.getDisplayMetrics());
        float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                resources.getDisplayMetrics());

        mCornerRadius = radius;

        final GradientDrawable background = new GradientDrawable();
        background.setColor(color);
        background.setCornerRadius(radius);

        mBackground = new View(context);
        ViewCompat.setBackground(mBackground, background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mShadowPadding = 0;
            mBackground.setElevation(elevation);
            mBackground.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        } else {
            setWillNotDraw(false);
            mShadowPadding = Math.round(elevation + 0.5f);
            setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
            mPaint.setColor(Color.WHITE);
            mPaint.setShadowLayer(elevation, 0, mShadowPadding * 0.3f, 0x20000000);
        }
        addView(mBackground);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBackground.measure(MeasureSpec.makeMeasureSpec(mBounds.width(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mBounds.height(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mBackground.layout(mBounds.left, mBounds.top, mBounds.right, mBounds.bottom);
        mPath.reset();
        mElevation.set(mBounds.left - mShadowPadding, mBounds.top - mShadowPadding,
                mBounds.right + mShadowPadding, mBounds.bottom + mShadowPadding);
        mPath.addRoundRect(mElevation, mCornerRadius, mCornerRadius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawPath(mPath, mPaint);
        }
    }

    float getCornerRadius() {
        return mCornerRadius;
    }

    void refresh() {
        getWindowVisibleDisplayFrame(mBounds);
        final int displayLeft = mBounds.left;
        final int displayTop = mBounds.top;
        final int displayRight = mBounds.right;
        final int displayBottom = mBounds.bottom;
        final int displayWidth = mBounds.width();
        final int displayHeight = mBounds.height();
        final float state = mAdapter.getState();
        if (state == 0) {
            mAdapter.getMainDisplayFrame(mBounds);
        } else if (state == 1) {
            mAdapter.getOverflowDisplayFrame(mBounds);
        } else {
            mAdapter.getMainDisplayFrame(mBounds);
            final int mainLeft = mBounds.left;
            final int mainTop = mBounds.top;
            final int mainRight = mBounds.right;
            final int mainBottom = mBounds.bottom;
            mAdapter.getOverflowDisplayFrame(mBounds);
            final int overflowLeft = mBounds.left;
            final int overflowTop = mBounds.top;
            final int overflowRight = mBounds.right;
            final int overflowBottom = mBounds.bottom;
            //noinspection ConstantConditions
            mBounds.set(Math.round(mainLeft + (overflowLeft - mainLeft) * state),
                    Math.round(mainTop + (overflowTop - mainTop) * state),
                    Math.round(mainRight + (overflowRight - mainRight) * state),
                    Math.round(mainBottom + (overflowBottom - mainBottom) * state));
        }
        final int left = mBounds.left;
        final int top = mBounds.top;
        final int width = mBounds.width();
        final int height = mBounds.height();
        if (width >= displayWidth && height >= displayHeight) {
            mBounds.set(displayLeft, displayTop, displayRight, displayBottom);
        } else if (width >= displayWidth) {
            //noinspection ConstantConditions
            int t = Math.max(displayTop, top);
            int b = t + height;
            if (b > displayBottom) {
                b = displayBottom;
                t = b - height;
            }
            mBounds.set(displayLeft, t, displayRight, b);
        } else if (height >= displayHeight) {
            //noinspection ConstantConditions
            int l = Math.max(displayLeft, left);
            int r = l + width;
            if (r > displayRight) {
                r = displayRight;
                l = r - width;
            }
            mBounds.set(l, displayTop, r, displayBottom);
        } else {
            //noinspection ConstantConditions
            int l = Math.max(displayLeft, left);
            int r = l + width;
            if (r > displayRight) {
                r = displayRight;
                l = r - width;
            }
            //noinspection ConstantConditions
            int t = Math.max(displayTop, top);
            int b = t + height;
            if (b > displayBottom) {
                b = displayBottom;
                t = b - height;
            }
            mBounds.set(l, t, r, b);
        }
        requestLayout();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            invalidate();
        }
    }
}
