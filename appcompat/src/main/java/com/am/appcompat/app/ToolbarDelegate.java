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
package com.am.appcompat.app;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Toolbar代理
 * Created by Alex on 2022/6/17.
 */
public interface ToolbarDelegate {

    /**
     * 点击了Toolbar的返回按钮
     *
     * @param v 返回按钮
     * @return 是否消耗掉这次点击事件
     */
    boolean onToolbarNavigationClick(View v);

    /**
     * 点击Toolbar的菜单子项
     *
     * @param item 子项
     * @return 是否消耗掉这次点击事件
     */
    boolean onToolbarMenuItemClick(@NonNull MenuItem item);

    /**
     * 更新Toolbar菜单
     *
     * @param menu 菜单
     */
    void onToolbarMenuUpdate(@NonNull Menu menu);
}
