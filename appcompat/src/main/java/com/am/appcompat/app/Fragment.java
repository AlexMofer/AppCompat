/*
 * Copyright (C) 2020 AlexMofer
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
package com.am.appcompat.app;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.am.mvp.app.MVPFragment;

/**
 * Fragment
 * Created by Alex on 2020/2/28.
 */
public abstract class Fragment extends MVPFragment {

    private View mToolbar;

    /**
     * 获取Toolbar
     *
     * @param <T> androidx.appcompat.widget.Toolbar 或 android.widget.Toolbar
     * @return Toolbar
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends View> T getToolbar() {
        return (T) mToolbar;
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbar Toolbar
     */
    public final void setToolbar(View toolbar) {
        if (toolbar instanceof androidx.appcompat.widget.Toolbar) {
            final androidx.appcompat.widget.Toolbar tb =
                    (androidx.appcompat.widget.Toolbar) toolbar;
            tb.setNavigationOnClickListener(this::onToolbarNavigationClick);
            tb.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
            mToolbar = toolbar;
            invalidateToolbarMenu();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (toolbar instanceof android.widget.Toolbar) {
                final android.widget.Toolbar tb =
                        (android.widget.Toolbar) toolbar;
                tb.setNavigationOnClickListener(this::onToolbarNavigationClick);
                tb.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
                mToolbar = toolbar;
                invalidateToolbarMenu();
            }
        }
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    public final void setToolbar(@IdRes int toolbarId) {
        setToolbar(requireView().findViewById(toolbarId));
    }

    /**
     * 点击了Toolbar的返回按钮
     *
     * @param v 返回按钮
     */
    protected void onToolbarNavigationClick(View v) {
        if (hideOverflowMenu()) {
            return;
        }
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    /**
     * 更新Toolbar菜单
     *
     * @param menu 菜单
     */
    protected void onToolbarMenuUpdate(@NonNull Menu menu) {
    }

    /**
     * 点击Toolbar的菜单子项
     *
     * @param item 子项
     * @return 是否消耗掉这次点击事件
     */
    protected boolean onToolbarMenuItemClick(@NonNull MenuItem item) {
        return false;
    }

    /**
     * 刷新Toolbar菜单
     */
    public void invalidateToolbarMenu() {
        if (mToolbar instanceof androidx.appcompat.widget.Toolbar) {
            final androidx.appcompat.widget.Toolbar toolbar =
                    (androidx.appcompat.widget.Toolbar) mToolbar;
            onToolbarMenuUpdate(toolbar.getMenu());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mToolbar instanceof android.widget.Toolbar) {
                final android.widget.Toolbar toolbar =
                        (android.widget.Toolbar) mToolbar;
                onToolbarMenuUpdate(toolbar.getMenu());
            }
        }
    }

    /**
     * 隐藏溢出菜单
     *
     * @return 完成隐藏时返回true
     */
    @SuppressLint("RestrictedApi")
    protected boolean hideOverflowMenu() {
        if (mToolbar instanceof androidx.appcompat.widget.Toolbar) {
            return ((androidx.appcompat.widget.Toolbar) mToolbar).hideOverflowMenu();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mToolbar instanceof android.widget.Toolbar) {
                return ((android.widget.Toolbar) mToolbar).hideOverflowMenu();
            }
        }
        return false;
    }
}