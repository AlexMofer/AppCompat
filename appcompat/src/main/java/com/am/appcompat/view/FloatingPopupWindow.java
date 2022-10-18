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

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 悬浮弹窗
 * Created by Alex on 2022/8/15.
 */
@SuppressLint("RestrictedApi")
class FloatingPopupWindow {

    private static final int MAX_HIDE_DURATION = 3000;
    private static final long ACTION_MODE_HIDE_DURATION_DEFAULT = 2000;
    private final View mOriginatingView;
    private final MenuBuilder mMenu;
    private final WindowManager mManager;
    private final Rect mFocusBounds = new Rect();
    private final int mMargin;
    private final int mOffset;

    private final BackgroundLayout mBackground;
    private final ToggleLayout mToggle;
    private final Rect mToggleBounds = new Rect();
    private final MainLayout mMain;
    private final Rect mMainBounds = new Rect();
    private final OverflowListView mOverflow;
    private final Rect mOverflowBounds = new Rect();
    private final Rect tRect = new Rect();
    private final ObjectAnimator mToggleAnimator = new ObjectAnimator().setDuration(250);
    private final View.OnAttachStateChangeListener mOnAttachStateChangeListener =
            new InnerOnAttachStateChangeListener();
    private float mState = 0;// 0为主面板，1为更多菜单面板，中间值为动画过程
    private boolean mHide = false;
    private final Runnable mHideOff = this::show;
    private boolean mHasWindowFocus = true;

    public FloatingPopupWindow(View view, OnClickListener listener) {
        mOriginatingView = view;
        final Context context = view.getContext();
        mMenu = new MenuBuilder(context);
        mManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mMargin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22,
                context.getResources().getDisplayMetrics()));
        mOffset = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                context.getResources().getDisplayMetrics()));

        final BoundsAdapter adapter = new InnerBoundsAdapter();
        mBackground = new BackgroundLayout(context, adapter);

        mBackground.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT).setTouchable(false).setLayoutInScreen());

        mToggle = new ToggleLayout(context, adapter, v -> toggle());
        mToggle.setLayoutParams(new LayoutParams().setTouchable(true));

        mMain = new MainLayout(context, adapter, listener::onClick);
        mMain.setLayoutParams(new LayoutParams());

        mOverflow = new OverflowListView(context, adapter, listener::onClick);
        mOverflow.setLayoutParams(new LayoutParams());

        mToggleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mToggleAnimator.addUpdateListener(animation -> {
            final float state = (float) animation.getAnimatedValue();
            if (mState == state) {
                return;
            }
            if (state == 0 || state == 1) {
                mState = state;
                mMain.refresh();
                mOverflow.refresh();
                mToggle.refresh();
                mBackground.refresh();
                requestLayout();
            } else {
                if (mState == 0 || mState == 1) {
                    mState = state;
                    mMain.refresh();
                    mOverflow.refresh();
                    mToggle.refresh();
                    mBackground.refresh();
                    requestLayout();
                } else {
                    mState = state;
                    mMain.refresh();
                    mOverflow.refresh();
                    mToggle.refresh();
                    mBackground.refresh();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mOriginatingView.getRootView().getViewTreeObserver()
                    .addOnWindowFocusChangeListener(mOnWindowFocusChangeListener);
        }
        mOriginatingView.addOnAttachStateChangeListener(mOnAttachStateChangeListener);
    }

    private final ViewTreeObserver.OnWindowFocusChangeListener mOnWindowFocusChangeListener =
            this::onWindowFocusChanged;

    void invalidateView() {
        removeAllView();
        mState = 0;
        final List<MenuItem> items = getMenuItems();
        if (items.isEmpty()) {
            return;
        }
        final Rect rect = tRect;
        mOriginatingView.getWindowVisibleDisplayFrame(rect);
        mMain.setData(items, rect.width() - mMargin - mMargin, mToggle.getSize());
        mOverflow.setData(items);

        invalidateContentRect();
    }

    MenuBuilder getMenu() {
        return mMenu;
    }

    View getView() {
        return mOriginatingView;
    }

    Rect getFocusBounds() {
        return mFocusBounds;
    }

    private void updateLocation() {
        if (!ViewCompat.isAttachedToWindow(mOriginatingView)) {
            removeAllView();
            ViewCompat.postOnAnimation(mOriginatingView, mUpdate);
            return;
        }
        if (!mHasWindowFocus) {
            removeAllView();
            ViewCompat.postOnAnimation(mOriginatingView, mUpdate);
            return;
        }
        final Rect rect = tRect;
        getMainBounds(rect);
        if (rect.equals(mMainBounds)) {
            if (mMain.getParent() != null) {
                ViewCompat.postOnAnimation(mOriginatingView, mUpdate);
                return;
            }
        }
        if (rect.isEmpty()) {
            removeAllView();
        }
        mMainBounds.set(rect);
        mOverflowBounds.setEmpty();
        getOverflowBounds(mOverflowBounds);
        mToggleBounds.setEmpty();
        getToggleBounds(mToggleBounds);

        mMain.refresh();
        mOverflow.refresh();
        mToggle.refresh();
        mBackground.refresh();

        requestLayout();
        ViewCompat.postOnAnimation(mOriginatingView, mUpdate);
    }

    void invalidateContentRect() {
        if (!ViewCompat.isAttachedToWindow(mOriginatingView)) {
            removeAllView();
            return;
        }
        if (!mHasWindowFocus) {
            removeAllView();
            return;
        }
        mMainBounds.setEmpty();
        getMainBounds(mMainBounds);
        if (mMainBounds.isEmpty()) {
            removeAllView();
            return;
        }
        mOverflowBounds.setEmpty();
        getOverflowBounds(mOverflowBounds);
        mToggleBounds.setEmpty();
        getToggleBounds(mToggleBounds);

        mMain.refresh();
        mOverflow.refresh();
        mToggle.refresh();
        mBackground.refresh();

        requestLayout();
    }

    private final Runnable mUpdate = this::updateLocation;

    private void removeAllView() {
        if (mBackground.getParent() != null) {
            mManager.removeView(mBackground);
        }
        if (mToggle.getParent() != null) {
            mManager.removeView(mToggle);
        }
        if (mMain.getParent() != null) {
            mManager.removeView(mMain);
        }
        if (mOverflow.getParent() != null) {
            mManager.removeView(mOverflow);
        }
    }

    private List<MenuItem> getMenuItems() {
        final ArrayList<MenuItem> items = new ArrayList<>();
        final int count = mMenu.size();
        for (int i = 0; i < count; i++) {
            items.add(mMenu.getItem(i));
        }
        return items;
    }

    void autoUpdateLocation() {
        ViewCompat.postOnAnimation(mOriginatingView, mUpdate);
    }

    void onWindowFocusChanged(boolean hasWindowFocus) {
        mHasWindowFocus = hasWindowFocus;
        updateLocation();
    }

    private void getFocusBoundsOnRoot(Rect rect) {
        final int viewLeft = mOriginatingView.getLeft();
        final int viewTop = mOriginatingView.getTop();
        rect.set(viewLeft + mFocusBounds.left,
                viewTop + mFocusBounds.top,
                viewLeft + mFocusBounds.right,
                viewTop + mFocusBounds.bottom);
        ViewParent parent = mOriginatingView.getParent();
        while (parent != null) {
            if (parent instanceof View) {
                final int parentWidth = ((View) parent).getWidth();
                final int parentHeight = ((View) parent).getHeight();
                if (rect.right <= 0 || rect.bottom <= 0 ||
                        rect.left >= parentWidth || rect.top >= parentHeight) {
                    // 其在父视图中已超出显示范围
                    rect.setEmpty();
                    return;
                }
                final int parentLeft = ((View) parent).getLeft();
                final int parentTop = ((View) parent).getTop();

                rect.set(parentLeft + rect.left,
                        parentTop + rect.top,
                        parentLeft + rect.right,
                        parentTop + rect.bottom);
            }
            parent = parent.getParent();
        }
    }

    private void getMainBounds(Rect rect) {
        mOriginatingView.getWindowVisibleDisplayFrame(rect);
        final int displayWidth = rect.width();
        final int displayHeight = rect.height();
        rect.setEmpty();

        mMain.measure(View.MeasureSpec.makeMeasureSpec(displayWidth - mMargin - mMargin,
                        View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(displayHeight - mMargin - mMargin,
                        View.MeasureSpec.AT_MOST));
        final int mainWidth = mMain.getMeasuredWidth();
        final int mainHeight = mMain.getMeasuredHeight();
        getFocusBoundsOnRoot(rect);
        if (rect.isEmpty()) {
            return;
        }
        final int itemWidth = mOverflow.isEmpty() ? mainWidth : mainWidth + mToggle.getSize();
        final int left = Math.max(mMargin, Math.min(displayWidth - mMargin - itemWidth,
                Math.round(rect.exactCenterX() - itemWidth * 0.5f)));
        final int top = Math.max(mMargin, Math.min(displayHeight - mMargin - mainHeight,
                rect.top - mOffset - mainHeight));
        rect.set(left, top, left + mainWidth, top + mainHeight);
    }

    private void getOverflowBounds(Rect rect) {
        if (mOverflow.isEmpty()) {
            return;
        }
        final int topPartHeight = mMainBounds.top - mMargin;
        mOriginatingView.getWindowVisibleDisplayFrame(rect);
        final int displayHeight = rect.height();
        rect.setEmpty();
        final int bottomPartHeight = displayHeight - mMargin - mMargin
                - topPartHeight - mMainBounds.height();
        final int right = mMainBounds.right + mToggle.getSize();
        if (mOverflow.measure(mMainBounds.width(), topPartHeight, bottomPartHeight)) {
            // 放置于上半部分
            final int bottom = mMainBounds.top;
            rect.set(right - mOverflow.getMeasuredWidth(),
                    bottom - mOverflow.getMeasuredHeight(), right, bottom);
        } else {
            // 放置于下半部分
            final int top = mMainBounds.bottom;
            rect.set(right - mOverflow.getMeasuredWidth(), top, right,
                    top + mOverflow.getMeasuredHeight());
        }
    }

    private void getToggleBounds(Rect rect) {
        if (mOverflow.isEmpty()) {
            return;
        }
        rect.set(mOverflowBounds.left, mMainBounds.top, mOverflowBounds.right, mMainBounds.bottom);
    }

    void destroy() {
        mOriginatingView.removeCallbacks(mUpdate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mOriginatingView.getRootView().getViewTreeObserver()
                    .removeOnWindowFocusChangeListener(mOnWindowFocusChangeListener);
        }
        mOriginatingView.removeOnAttachStateChangeListener(mOnAttachStateChangeListener);
        removeAllView();
    }

    private void requestLayout() {
        if (mBackground.getParent() != null) {
            mManager.updateViewLayout(mBackground, mBackground.getLayoutParams());
        } else {
            mManager.addView(mBackground, mBackground.getLayoutParams());
        }
        final LayoutParams lpt = (LayoutParams) mToggle.getLayoutParams();
        lpt.setBounds(mToggleBounds.left, mToggleBounds.top,
                mToggleBounds.right, mToggleBounds.bottom);
        lpt.setTouchable(!mHide);
        if (mToggle.getParent() != null) {
            mManager.updateViewLayout(mToggle, lpt);
        } else {
            if (lpt.width != 0 || lpt.height != 0) {
                mManager.addView(mToggle, lpt);
            }
        }
        final LayoutParams lpm = (LayoutParams) mMain.getLayoutParams();
        lpm.setBounds(mMainBounds.left, mMainBounds.top,
                mMainBounds.right, mMainBounds.bottom);
        lpm.setTouchable(!mHide && mState == 0);
        if (mMain.getParent() != null) {
            mManager.updateViewLayout(mMain, lpm);
        } else {
            if (lpm.width != 0 || lpm.height != 0) {
                mManager.addView(mMain, lpm);
            }
        }
        final LayoutParams lpo = (LayoutParams) mOverflow.getLayoutParams();
        lpo.setBounds(mOverflowBounds.left, mOverflowBounds.top,
                mOverflowBounds.right, mOverflowBounds.bottom);
        lpo.setTouchable(!mHide && mState == 1);
        if (mOverflow.getParent() != null) {
            mManager.updateViewLayout(mOverflow, lpo);
        } else {
            if (lpo.width != 0 || lpo.height != 0) {
                mManager.addView(mOverflow, lpo);
            }
        }
    }

    void hide(long duration) {
        if (duration == ActionMode.DEFAULT_HIDE_DURATION) {
            duration = ACTION_MODE_HIDE_DURATION_DEFAULT;
        }
        duration = Math.min(MAX_HIDE_DURATION, duration);
        mOriginatingView.removeCallbacks(mHideOff);
        if (duration <= 0) {
            mHideOff.run();
        } else {
            hide();
            mOriginatingView.postDelayed(mHideOff, duration);
        }
    }

    private class InnerOnAttachStateChangeListener implements View.OnAttachStateChangeListener {

        @Override
        public void onViewAttachedToWindow(View v) {
            updateLocation();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            updateLocation();
        }
    }


    private void toggle() {
        if (mToggleAnimator.isRunning()) {
            mToggleAnimator.end();
        }
        if (mState == 0) {
            mToggle.setBack();
            mToggleAnimator.setFloatValues(0f, 1f);
            mOverflow.awakenScrollBar();
        } else {
            mToggle.setOverflow();
            mToggleAnimator.setFloatValues(1f, 0f);
        }
        mToggleAnimator.start();
    }

    private void hide() {
        mMain.setVisibility(View.GONE);
        mOverflow.setVisibility(View.GONE);
        mToggle.setVisibility(View.GONE);
        mBackground.setVisibility(View.GONE);
        mHide = true;
        requestLayout();
    }

    private void show() {
        mMain.setVisibility(View.VISIBLE);
        mOverflow.setVisibility(View.VISIBLE);
        mToggle.setVisibility(View.VISIBLE);
        mBackground.setVisibility(View.VISIBLE);
        mHide = false;
        requestLayout();
    }

    public interface OnClickListener {

        void onClick(MenuItem item);
    }

    private class InnerBoundsAdapter implements BoundsAdapter {

        @Override
        public void getMainDisplayFrame(Rect rect) {
            rect.set(mMainBounds);
            if (!mOverflow.isEmpty()) {
                rect.union(mToggleBounds);
            }
        }

        @Override
        public void getOverflowDisplayFrame(Rect rect) {
            rect.set(mToggleBounds);
            rect.union(mOverflowBounds);
        }

        @Override
        public void getViewDisplayFrame(View view, Rect rect) {
            if (view == mMain) {
                rect.set(mMainBounds);
            } else if (view == mOverflow) {
                rect.set(mOverflowBounds);
            } else if (view == mToggle) {
                rect.set(mToggleBounds);
            }
        }

        @Override
        public float getState() {
            return mState;
        }

        @Override
        public float getCornerRadius() {
            return mBackground.getCornerRadius();
        }
    }
}
