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
import android.app.Dialog;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.ContentView;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;

import com.am.appcompat.view.MenuUtils;
import com.am.mvp.app.MVPActivity;

import java.util.ArrayList;

/**
 * 基础Activity
 * Created by Alex on 2020/6/1.
 */
public abstract class AppCompatActivity extends MVPActivity implements DialogHolder {

    private final ArrayList<ToolbarDelegate> mToolbarDelegates = new ArrayList<>();
    private final ArrayList<AppCompatDialog> mDialogs = new ArrayList<>();
    private View mToolbar;

    public AppCompatActivity() {
    }

    @ContentView
    public AppCompatActivity(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        final ArrayList<AppCompatDialog> dialogs = new ArrayList<>(mDialogs);
        for (AppCompatDialog dialog : dialogs) {
            try {
                if (dialog.isShowing()) {
                    dialog.onDismissByActivityStop();
                    final Window window = dialog.getWindow();
                    try {
                        dialog.getWindow().getWindowManager()
                                .removeViewImmediate(window.getDecorView());
                    } finally {
                        window.closeAllPanels();
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mToolbar instanceof androidx.appcompat.widget.Toolbar) {
            final androidx.appcompat.widget.Toolbar toolbar =
                    (androidx.appcompat.widget.Toolbar) mToolbar;
            toolbar.setTitle(title);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mToolbar instanceof android.widget.Toolbar) {
                final android.widget.Toolbar toolbar =
                        (android.widget.Toolbar) mToolbar;
                toolbar.setTitle(title);
            }
        }
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    public final void setSupportActionBar(@IdRes int toolbarId) {
        final View view = findViewById(toolbarId);
        if (view instanceof androidx.appcompat.widget.Toolbar) {
            final androidx.appcompat.widget.Toolbar toolbar =
                    (androidx.appcompat.widget.Toolbar) view;
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(this::onToolbarNavigationClick);
        }
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    public final void setSupportActionBar(@IdRes int toolbarId, boolean showTitle) {
        setSupportActionBar(toolbarId);
        final androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(showTitle);
        }
    }

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
        setToolbar(findViewById(toolbarId));
    }

    /**
     * 点击了Toolbar的返回按钮
     *
     * @param v 返回按钮
     */
    protected void onToolbarNavigationClick(View v) {
        for (ToolbarDelegate delegate : mToolbarDelegates) {
            if (delegate.onToolbarNavigationClick(v)) {
                return;
            }
        }
        if (hideOverflowMenu()) {
            return;
        }
        getOnBackPressedDispatcher().onBackPressed();
    }

    /**
     * 更新Toolbar菜单
     *
     * @param menu 菜单
     */
    protected void onToolbarMenuUpdate(@NonNull Menu menu) {
        for (ToolbarDelegate delegate : mToolbarDelegates) {
            delegate.onToolbarMenuUpdate(menu);
        }
    }

    /**
     * 点击Toolbar的菜单子项
     *
     * @param item 子项
     * @return 是否消耗掉这次点击事件
     */
    protected boolean onToolbarMenuItemClick(@NonNull MenuItem item) {
        for (ToolbarDelegate delegate : mToolbarDelegates) {
            if (delegate.onToolbarMenuItemClick(item)) {
                return true;
            }
        }
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
        final androidx.appcompat.app.ActionBar sab = getSupportActionBar();
        if (sab != null) {
            return sab.collapseActionView();
        }
        final android.app.ActionBar ab = getActionBar();
        if (ab != null) {
            try {
                //noinspection ConstantConditions
                return (boolean) ab.getClass().getMethod("collapseActionView").invoke(ab);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 添加Toolbar代理
     *
     * @param delegate Toolbar代理
     */
    @Deprecated
    public void addToolbarDelegate(@NonNull ToolbarDelegate delegate) {
        mToolbarDelegates.add(delegate);
    }

    /**
     * 移除Toolbar代理
     *
     * @param delegate Toolbar代理
     */
    @Deprecated
    public void removeToolbarDelegate(@NonNull ToolbarDelegate delegate) {
        mToolbarDelegates.remove(delegate);
    }

    /**
     * 清空Toolbar代理
     */
    @Deprecated
    public void clearToolbarDelegate() {
        mToolbarDelegates.clear();
    }

    /**
     * 是否显示可选图标
     *
     * @param menu    菜单
     * @param visible 是否显示
     */
    @Deprecated
    public boolean setOptionalIconsVisible(Menu menu, boolean visible) {
        return MenuUtils.setOptionalIconsVisible(menu, visible);
    }

    @Override
    public void addDialog(AppCompatDialog dialog) {
        if (dialog == null) {
            return;
        }
        mDialogs.add(dialog);
    }

    @Override
    public void removeDialog(AppCompatDialog dialog) {
        if (dialog == null) {
            return;
        }
        mDialogs.remove(dialog);
    }
}