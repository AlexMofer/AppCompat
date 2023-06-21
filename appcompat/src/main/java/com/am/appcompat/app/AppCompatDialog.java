package com.am.appcompat.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 对话框
 * Created by Alex on 2023/6/21.
 */
public class AppCompatDialog extends androidx.appcompat.app.AppCompatDialog {

    private final DialogHolder mHolder;

    public AppCompatDialog(@NonNull Context context, boolean add) {
        super(context);
        if (add) {
            if (context instanceof DialogHolder) {
                mHolder = (DialogHolder) context;
                mHolder.addDialog(this);
            } else {
                mHolder = null;
            }
        } else {
            mHolder = null;
        }
    }

    public AppCompatDialog(@NonNull Context context, boolean add, int theme) {
        super(context, theme);
        if (add) {
            if (context instanceof DialogHolder) {
                mHolder = (DialogHolder) context;
                mHolder.addDialog(this);
            } else {
                mHolder = null;
            }
        } else {
            mHolder = null;
        }
    }

    public AppCompatDialog(@NonNull Context context, boolean add, boolean cancelable,
                           @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        if (add) {
            if (context instanceof DialogHolder) {
                mHolder = (DialogHolder) context;
                mHolder.addDialog(this);
            } else {
                mHolder = null;
            }
        } else {
            mHolder = null;
        }
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
        onSetContentView();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onSetContentView();
    }

    @Override
    public void setContentView(@NonNull View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        onSetContentView();
    }

    /**
     * 设置内容View
     */
    protected void onSetContentView() {

    }

    /**
     * 因Activity停止而关闭（此时对话框尚未dismiss）
     */
    protected void onDismissByActivityStop() {
    }

    @Override
    public void dismiss() {
        if (mHolder != null) {
            mHolder.removeDialog(this);
        }
        super.dismiss();
    }
}
