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
package com.am.appcompat.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

/**
 * A special version of {@link DialogFragment} which uses an {@link MaterialAlertDialog} in place of a
 * platform-styled dialog.
 *
 * @see DialogFragment
 */
@SuppressLint("RestrictedApi")
public class MaterialAlertDialogFragment extends DialogFragment {

    private final int mContentLayoutId;

    public MaterialAlertDialogFragment() {
        super();
        mContentLayoutId = 0;
    }

    public MaterialAlertDialogFragment(@LayoutRes int contentLayoutId) {
        super();
        mContentLayoutId = contentLayoutId;
    }

    @NonNull
    @Override
    public MaterialAlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Context context = requireContext();
        final MaterialAlertDialog dialog = onCreateDialog(savedInstanceState, context, getTheme());
        if (mContentLayoutId != 0) {
            dialog.setView(View.inflate(context, mContentLayoutId, null));
        }
        return dialog;
    }

    /**
     * 创建对话框
     *
     * @param context Context
     * @param theme   主题
     * @return 对话框
     */
    protected MaterialAlertDialog onCreateDialog(@Nullable Bundle savedInstanceState,
                                                 @NonNull Context context, @StyleRes int theme) {
        return new MaterialAlertDialog(context, theme);
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        if (dialog instanceof AppCompatDialog) {
            // If the dialog is an AppCompatDialog, we'll handle it
            AppCompatDialog acd = (AppCompatDialog) dialog;
            switch (style) {
                case STYLE_NO_INPUT:
                    dialog.getWindow().addFlags(
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    // fall through...
                case STYLE_NO_FRAME:
                case STYLE_NO_TITLE:
                    acd.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            }
        } else {
            // Else, just let super handle it
            super.setupDialog(dialog, style);
        }
    }

    /**
     * 通过ID查找View
     *
     * @param id  View 的资源ID
     * @param <V> View类型
     * @return 对应资源ID的View
     */
    public final <V extends View> V findViewById(int id) {
        return requireDialog().findViewById(id);
    }
}