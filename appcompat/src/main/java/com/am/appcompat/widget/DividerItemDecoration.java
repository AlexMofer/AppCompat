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
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 分割线子项装饰
 * Created by Alex on 2022/7/13.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int UNSPECIFIED = -1;
    public static final int HORIZONTAL = RecyclerView.HORIZONTAL;

    public static final int VERTICAL = RecyclerView.VERTICAL;
    private final DividerParams mBeginning;
    private final DividerParams mMiddle;
    private final DividerParams mEnd;
    private int mOrientation = UNSPECIFIED;

    public DividerItemDecoration(@NonNull DividerParams beginning,
                                 @NonNull DividerParams middle,
                                 @NonNull DividerParams end) {
        mBeginning = beginning;
        mMiddle = middle;
        mEnd = end;
    }

    public DividerItemDecoration(@NonNull DividerParams middle) {
        this(new DividerParams(0), middle, new DividerParams(0));
    }

    public DividerItemDecoration(int beginning, int middle, int end) {
        this(new DividerParams(beginning), new DividerParams(middle), new DividerParams(end));
    }

    public DividerItemDecoration(int middle) {
        this(0, middle, 0);
    }

    public DividerItemDecoration(@Nullable Drawable beginning, @Nullable Drawable middle,
                                 @Nullable Drawable end) {
        this(new DividerParams(beginning), new DividerParams(middle), new DividerParams(end));
    }

    public DividerItemDecoration(@Nullable Drawable middle) {
        this(null, middle, null);
    }

    /**
     * 设置方向
     *
     * @param orientation 方向
     */
    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    /**
     * 判断是否为垂直方向
     *
     * @param manager 布局管理器
     * @return 为垂直方向时返回true
     */
    protected boolean isVertical(RecyclerView.LayoutManager manager) {
        if (mOrientation == VERTICAL) {
            return true;
        }
        if (mOrientation == HORIZONTAL) {
            return true;
        }
        return manager instanceof LinearLayoutManager &&
                ((LinearLayoutManager) manager).getOrientation() == LinearLayoutManager.VERTICAL;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager == null) {
            return;
        }
        outRect.left = outRect.top = outRect.right = outRect.bottom = 0;
        if (isVertical(manager)) {
            // 垂直
            outRect.bottom = mMiddle.getHeight(parent);
            final int position = manager.getPosition(view);
            final int itemCount = manager.getItemCount();
            if (position == 0) {
                // 首项
                outRect.top = mBeginning.getHeight(parent);
            }
            if (position == itemCount - 1) {
                // 尾项
                outRect.bottom = mEnd.getHeight(parent);
            }
        } else {
            // 水平
            outRect.right = mMiddle.getWidth(parent);
            final int position = manager.getPosition(view);
            final int itemCount = manager.getItemCount();
            if (position == 0) {
                // 首项
                outRect.left = mBeginning.getWidth(parent);
            }
            if (position == itemCount - 1) {
                // 尾项
                outRect.right = mEnd.getWidth(parent);
            }
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager == null) {
            return;
        }
        if (isVertical(manager)) {
            // 垂直
            final int itemCount = manager.getItemCount();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final int position = manager.getPosition(child);
                if (position == 0) {
                    c.save();
                    final int height = manager.getTopDecorationHeight(child);
                    c.translate(child.getLeft() + child.getTranslationX(),
                            child.getTop() + child.getTranslationY() - height);
                    mBeginning.onDraw(c, child.getWidth(), height);
                    c.restore();
                }
                c.save();
                c.translate(child.getLeft() + child.getTranslationX(),
                        child.getBottom() + child.getTranslationY());
                if (position == itemCount - 1) {
                    // 尾项
                    mEnd.onDraw(c, child.getWidth(), manager.getBottomDecorationHeight(child));
                } else {
                    mMiddle.onDraw(c, child.getWidth(), manager.getBottomDecorationHeight(child));
                }
                c.restore();
            }
        } else {
            // 水平
            final int itemCount = manager.getItemCount();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final int position = manager.getPosition(child);
                if (position == 0) {
                    c.save();
                    final int width = manager.getLeftDecorationWidth(child);
                    c.translate(child.getLeft() + child.getTranslationX() - width,
                            child.getTop() + child.getTranslationY());
                    mBeginning.onDraw(c, width, child.getHeight());
                    c.restore();
                }
                c.save();
                c.translate(child.getRight() + child.getTranslationX(),
                        child.getTop() + child.getTranslationY());
                if (position == itemCount - 1) {
                    // 尾项
                    mEnd.onDraw(c, manager.getRightDecorationWidth(child), child.getHeight());
                } else {
                    mMiddle.onDraw(c, manager.getRightDecorationWidth(child), child.getHeight());
                }
                c.restore();
            }
        }
    }

    /**
     * 分割线参数
     */
    public static class DividerParams {
        public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
        public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
        public static final int SCALE_TYPE_CENTER = 0;
        public static final int SCALE_TYPE_FIT = 1;
        private int mWidth;
        private int mHeight;

        private int mLeftMargin;

        private int mTopMargin;

        private int mRightMargin;

        private int mBottomMargin;
        private Drawable mDrawable;
        private int mScaleType = SCALE_TYPE_CENTER;

        public DividerParams(int size) {
            this(size, size, null);
        }

        public DividerParams(@Nullable Drawable drawable) {
            mWidth = WRAP_CONTENT;
            mHeight = WRAP_CONTENT;
            mDrawable = drawable;
        }

        public DividerParams(int width, int height, @Nullable Drawable drawable) {
            mWidth = width;
            mHeight = height;
            mDrawable = drawable;
        }

        public DividerParams(int width, int height, @Nullable Drawable drawable, int scaleType) {
            mWidth = width;
            mHeight = height;
            mDrawable = drawable;
            mScaleType = scaleType;
        }

        public DividerParams(int width, int height, @Nullable Drawable drawable, int scaleType,
                             int margin) {
            this(width, height, drawable, scaleType);
            mLeftMargin = margin;
            mTopMargin = margin;
            mRightMargin = margin;
            mBottomMargin = margin;
        }

        public DividerParams(int width, int height, @Nullable Drawable drawable, int scaleType,
                             int horizontalMargin, int verticalMargin) {
            this(width, height, drawable, scaleType);
            mLeftMargin = horizontalMargin;
            mTopMargin = verticalMargin;
            mRightMargin = horizontalMargin;
            mBottomMargin = verticalMargin;
        }

        public DividerParams(int width, int height, @Nullable Drawable drawable, int scaleType,
                             int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
            this(width, height, drawable, scaleType);
            mLeftMargin = leftMargin;
            mTopMargin = topMargin;
            mRightMargin = rightMargin;
            mBottomMargin = bottomMargin;
        }

        /**
         * 设置宽度
         *
         * @param width 宽度
         */
        public void setWidth(int width) {
            mWidth = width;
        }

        /**
         * 设置高度
         *
         * @param height 高度
         */
        public void setHeight(int height) {
            mHeight = height;
        }

        /**
         * 设置图片
         *
         * @param drawable 图片
         */
        public void setDrawable(@Nullable Drawable drawable) {
            mDrawable = drawable;
        }

        /**
         * 设置图片缩放方式
         *
         * @param type 缩放方式
         */
        public void setScaleType(int type) {
            mScaleType = type;
        }

        /**
         * 设置间距
         *
         * @param left   左间距
         * @param top    上间距
         * @param right  右间距
         * @param bottom 下间距
         */
        public void setMargin(int left, int top, int right, int bottom) {
            mLeftMargin = left;
            mTopMargin = top;
            mRightMargin = right;
            mBottomMargin = bottom;
        }

        /**
         * 获取宽度
         *
         * @param parent RecyclerView
         * @return 宽度
         */
        protected int getWidth(RecyclerView parent) {
            if (mWidth == MATCH_PARENT) {
                return mLeftMargin + parent.getWidth() + mRightMargin;
            } else if (mWidth == WRAP_CONTENT) {
                return mLeftMargin + (mDrawable == null ? 0 : mDrawable.getIntrinsicWidth())
                        + mRightMargin;
            } else {
                return mLeftMargin + Math.max(0, mWidth) + mRightMargin;
            }
        }

        /**
         * 获取高度
         *
         * @param parent RecyclerView
         * @return 高度
         */
        protected int getHeight(RecyclerView parent) {
            if (mHeight == MATCH_PARENT) {
                return mTopMargin + parent.getHeight() + mBottomMargin;
            } else if (mHeight == WRAP_CONTENT) {
                return mTopMargin + (mDrawable == null ? 0 : mDrawable.getIntrinsicHeight())
                        + mBottomMargin;
            } else {
                return mTopMargin + Math.max(0, mHeight) + mBottomMargin;
            }
        }

        /**
         * 绘制
         *
         * @param canvas 画布
         * @param width  宽度
         * @param height 高度
         */
        protected void onDraw(Canvas canvas, int width, int height) {
            if (mDrawable == null) {
                return;
            }
            if (mScaleType == SCALE_TYPE_FIT) {
                mDrawable.setBounds(mLeftMargin, mTopMargin,
                        width - mLeftMargin - mRightMargin,
                        height - mTopMargin - mBottomMargin);
                mDrawable.draw(canvas);
            } else {
                final int drawableWidth = mDrawable.getIntrinsicWidth();
                final int drawableHeight = mDrawable.getIntrinsicHeight();
                mDrawable.setBounds(0, 0, drawableWidth, drawableHeight);
                canvas.save();
                canvas.translate((width - drawableWidth) * 0.5f,
                        (height - drawableHeight) * 0.5f);
                mDrawable.draw(canvas);
                canvas.restore();
            }
        }
    }
}