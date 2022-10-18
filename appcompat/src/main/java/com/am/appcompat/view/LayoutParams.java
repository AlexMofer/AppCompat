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

import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;

/**
 * 布局参数
 * Created by Alex on 2022/8/16.
 */
class LayoutParams extends WindowManager.LayoutParams {

    public LayoutParams() {
        format = PixelFormat.TRANSPARENT;
        type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        gravity = Gravity.START | Gravity.TOP;
        setTouchable(false);
    }

    public LayoutParams(int width, int height) {
        this();
        this.width = width;
        this.height = height;
    }

    public LayoutParams setTouchable(boolean touchable) {
        flags = computeFlags(flags, touchable);
        return this;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        x = left;
        y = top;
        width = right - left;
        height = bottom - top;
    }

    public LayoutParams setLayoutInScreen() {
        flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        return this;
    }

    private int computeFlags(int curFlags, boolean touchable) {
        curFlags &= ~(
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (!touchable)
            curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_ATTACHED_IN_DECOR;
        curFlags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        return curFlags;
    }
}
