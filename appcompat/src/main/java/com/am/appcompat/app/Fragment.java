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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.am.mvp.app.MVPFragment;

/**
 * Fragment
 * Created by Alex on 2020/2/28.
 */
public abstract class Fragment extends MVPFragment {

    private final ToolbarDelegate mToolbarDelegate = new InnerToolbarDelegate();
    private final BackPressedDelegate mBackPressedDelegate = new InnerBackPressedDelegate();
    private boolean mHasToolbarMenu;
    private boolean mDelegateBackPressed;

    public Fragment() {
    }

    @ContentView
    public Fragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    /**
     * 通过ID查找View
     *
     * @param id  View 的资源ID
     * @param <V> View类型
     * @return 对应资源ID的View
     */
    public final <V extends View> V findViewById(int id) {
        return requireView().findViewById(id);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            if (mHasToolbarMenu) {
                ((AppCompatActivity) activity).addToolbarDelegate(mToolbarDelegate);
                ((AppCompatActivity) activity).invalidateToolbarMenu();
            }
            if (mDelegateBackPressed) {
                ((AppCompatActivity) activity).addBackPressedDelegate(mBackPressedDelegate);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        final FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            if (mHasToolbarMenu) {
                ((AppCompatActivity) activity).removeToolbarDelegate(mToolbarDelegate);
                ((AppCompatActivity) activity).invalidateToolbarMenu();
            }
            if (mDelegateBackPressed) {
                ((AppCompatActivity) activity).removeBackPressedDelegate(mBackPressedDelegate);
            }
        }
    }

    /**
     * 设置是否有Toolbar菜单
     *
     * @param hasMenu 是否有Toolbar菜单
     */
    public void setHasToolbarMenu(boolean hasMenu) {
        if (mHasToolbarMenu == hasMenu) {
            return;
        }
        mHasToolbarMenu = hasMenu;
        if (getView() != null) {
            final FragmentActivity activity = getActivity();
            if (activity instanceof AppCompatActivity) {
                if (mHasToolbarMenu) {
                    ((AppCompatActivity) activity).addToolbarDelegate(mToolbarDelegate);
                } else {
                    ((AppCompatActivity) activity).removeToolbarDelegate(mToolbarDelegate);
                }
                invalidateToolbarMenu();
            }
        }
    }

    /**
     * 设置是否代理返回操作
     *
     * @param delegate 是否代理返回操作
     */
    public void setDelegateBackPressed(boolean delegate) {
        if (mDelegateBackPressed == delegate) {
            return;
        }
        mDelegateBackPressed = delegate;
        if (getView() != null) {
            final FragmentActivity activity = getActivity();
            if (activity instanceof AppCompatActivity) {
                if (mDelegateBackPressed) {
                    ((AppCompatActivity) activity).addBackPressedDelegate(mBackPressedDelegate);
                } else {
                    ((AppCompatActivity) activity).removeBackPressedDelegate(mBackPressedDelegate);
                }
            }
        }
    }

    /**
     * 点击了Toolbar的返回按钮
     *
     * @param v 返回按钮
     * @return 是否消耗掉这次点击事件
     */
    protected boolean onToolbarNavigationClick(View v) {
        requireActivity().onBackPressed();
        return true;
    }

    /**
     * 更新Toolbar菜单
     *
     * @param menu     菜单
     * @param inflater MenuInflater
     */
    protected void onToolbarMenuUpdate(@NonNull Menu menu, MenuInflater inflater) {
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
    protected void invalidateToolbarMenu() {
        final FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).invalidateToolbarMenu();
        }
    }

    /**
     * 返回事件
     *
     * @return 消耗这次返回事件时返回true
     */
    protected boolean onBackPressed() {
        return false;
    }

    /**
     * 获取Toolbar代理
     *
     * @return Toolbar代理
     */
    protected ToolbarDelegate getToolbarDelegate() {
        return mToolbarDelegate;
    }

    /**
     * 获取返回事件代理
     *
     * @return 返回事件代理
     */
    protected BackPressedDelegate getBackPressedDelegate() {
        return mBackPressedDelegate;
    }

    private class InnerToolbarDelegate implements ToolbarDelegate {

        @Override
        public boolean onToolbarNavigationClick(View v) {
            return Fragment.this.onToolbarNavigationClick(v);
        }

        @Override
        public boolean onToolbarMenuItemClick(@NonNull MenuItem item) {
            return Fragment.this.onToolbarMenuItemClick(item);
        }

        @Override
        public void onToolbarMenuUpdate(@NonNull Menu menu) {
            Fragment.this.onToolbarMenuUpdate(menu, requireActivity().getMenuInflater());
        }
    }

    private class InnerBackPressedDelegate implements BackPressedDelegate {

        @Override
        public boolean onBackPressed() {
            return Fragment.this.onBackPressed();
        }
    }
}