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
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

/**
 * 基础悬浮ActionMode
 * Created by Alex on 2022/8/10.
 */
@SuppressLint("RestrictedApi")
class FloatingActionMode extends ActionMode {

    private final ActionModeCompat.Callback mCallback;
    private final FloatingPopupWindow mPopupWindow;
    private boolean mFinished = false;

    public FloatingActionMode(View view, ActionModeCompat.Callback callback) {
        mCallback = callback;
        mPopupWindow = new FloatingPopupWindow(view, item -> {
            if (callback.onActionItemClicked(this, item)) {
                return;
            }
            final Intent intent = item.getIntent();
            final Context context = view.getContext();
            if (intent != null) {
                try {
                    context.startActivity(intent);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    boolean start() {
        if (mCallback.onCreateActionMode(this, mPopupWindow.getMenu())) {
            mCallback.onPrepareActionMode(this, mPopupWindow.getMenu());
            mCallback.onGetContentRect(this, mPopupWindow.getView(),
                    mPopupWindow.getFocusBounds());
            mPopupWindow.invalidateView();
            mPopupWindow.autoUpdateLocation();
            return true;
        }
        return false;
    }

    @Override
    public void invalidate() {
        if (mCallback.onPrepareActionMode(this, mPopupWindow.getMenu())) {
            mCallback.onGetContentRect(this, mPopupWindow.getView(),
                    mPopupWindow.getFocusBounds());
            mPopupWindow.invalidateView();
        }
    }

    @Override
    public void invalidateContentRect() {
        mCallback.onGetContentRect(this, mPopupWindow.getView(), mPopupWindow.getFocusBounds());
        mPopupWindow.invalidateContentRect();
    }

    @Override
    public void hide(long duration) {
        mPopupWindow.hide(duration);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        mPopupWindow.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    public void finish() {
        if (mFinished) {
            return;
        }
        mFinished = true;
        mCallback.onDestroyActionMode(this);
        mPopupWindow.destroy();
    }

    @Override
    public Menu getMenu() {
        return mPopupWindow.getMenu();
    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    @Override
    public void setTitle(CharSequence title) {
    }

    @Override
    public void setTitle(int resId) {
    }

    @Override
    public CharSequence getSubtitle() {
        return null;
    }

    @Override
    public void setSubtitle(CharSequence subtitle) {
    }

    @Override
    public void setSubtitle(int resId) {
    }

    @Override
    public View getCustomView() {
        return null;
    }

    @Override
    public void setCustomView(View view) {
    }

    @Override
    public MenuInflater getMenuInflater() {
        return new MenuInflater(mPopupWindow.getMenu().getContext());
    }

    @Override
    public int getType() {
        return ActionModeCompat.TYPE_FLOATING;
    }
}
