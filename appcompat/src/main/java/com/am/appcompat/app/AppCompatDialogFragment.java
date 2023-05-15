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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import com.am.mvp.app.MVPDialogFragment;

/**
 * MVP对话框
 * Created by Alex on 2020/3/6.
 */
public class AppCompatDialogFragment extends MVPDialogFragment {

    public AppCompatDialogFragment() {
    }

    @ContentView
    public AppCompatDialogFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new InnerDialog(requireContext(), getTheme());
    }

    /**
     * 设置内容View
     *
     * @param dialog Dialog
     */
    protected void onSetContentView(Dialog dialog) {

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

    private class InnerDialog extends AppCompatDialog {

        public InnerDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        public void setContentView(@NonNull View view) {
            super.setContentView(view);
            onSetContentView(this);
        }

        @Override
        public void setContentView(int layoutResID) {
            super.setContentView(layoutResID);
            onSetContentView(this);
        }

        @Override
        public void setContentView(@NonNull View view, ViewGroup.LayoutParams params) {
            super.setContentView(view, params);
            onSetContentView(this);
        }
    }
}