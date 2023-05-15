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
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.ContentView;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * 基础警报对话框
 * Created by Alex on 2020/6/12.
 */
@Deprecated
public class AppCompatAlertDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_ICON = "icon";
    private static final String KEY_POSITIVE = "positive";
    private static final String KEY_NEGATIVE = "negative";
    private static final String KEY_NEUTRAL = "neutral";
    private static final String KEY_CANCELABLE = "cancelable";
    private static final String KEY_LAYOUT = "layout";
    @LayoutRes
    private int mContentLayoutId;

    public AppCompatAlertDialogFragment() {
    }

    @ContentView
    public AppCompatAlertDialogFragment(int contentLayoutId) {
        this();
        mContentLayoutId = contentLayoutId;
    }

    @Nullable
    @Override
    public AlertDialog getDialog() {
        return (AlertDialog) super.getDialog();
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), getTheme());
        if (mContentLayoutId != 0) {
            builder.setView(mContentLayoutId);
        }
        onCreateAlertDialog(savedInstanceState, builder);
        return builder.create();
    }

    /**
     * 创建警告对话框
     *
     * @param savedInstanceState 保存的实例状态
     * @param builder            构造器
     */
    protected void onCreateAlertDialog(@Nullable Bundle savedInstanceState,
                                       AlertDialog.Builder builder) {
        builder.setOnCancelListener(new InnerOnCancelListener());
        builder.setOnDismissListener(new InnerOnDismissListener());
        builder.setOnKeyListener(new InnerOnKeyListener());
        final Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }
        if (arguments.containsKey(KEY_TITLE)) {
            builder.setTitle(arguments.getCharSequence(KEY_TITLE));
        }
        if (arguments.containsKey(KEY_MESSAGE)) {
            builder.setMessage(arguments.getCharSequence(KEY_MESSAGE));
        }
        if (arguments.containsKey(KEY_ICON)) {
            builder.setIcon(arguments.getInt(KEY_ICON));
        }
        final DialogInterface.OnClickListener listener = new InnerOnClickListener();
        if (arguments.containsKey(KEY_POSITIVE)) {
            builder.setPositiveButton(arguments.getCharSequence(KEY_POSITIVE), listener);
        }
        if (arguments.containsKey(KEY_NEGATIVE)) {
            builder.setNegativeButton(arguments.getCharSequence(KEY_NEGATIVE), listener);
        }
        if (arguments.containsKey(KEY_NEUTRAL)) {
            builder.setNeutralButton(arguments.getCharSequence(KEY_NEUTRAL), listener);
        }
        builder.setCancelable(arguments.getBoolean(KEY_CANCELABLE, true));
        if (arguments.containsKey(KEY_LAYOUT)) {
            builder.setView(arguments.getInt(KEY_LAYOUT));
        }
    }

    public interface CancelCallback {
        /**
         * This method will be invoked when the dialog is canceled.
         *
         * @param fragment the dialog that was canceled will be passed into the
         *                 method
         */
        void onCancel(DialogFragment fragment);
    }

    public interface DismissCallback {
        /**
         * This method will be invoked when the dialog is dismissed.
         *
         * @param fragment the dialog that was dismissed will be passed into the
         *                 method
         */
        void onDismiss(DialogFragment fragment);
    }

    public interface KeyCallback {
        /**
         * Called when a key is dispatched to a dialog. This allows listeners to
         * get a chance to respond before the dialog.
         *
         * @param fragment the dialog the key has been dispatched to
         * @param keyCode  the code for the physical key that was pressed
         * @param event    the KeyEvent object containing full information about
         *                 the event
         * @return {@code true} if the listener has consumed the event,
         * {@code false} otherwise
         */
        boolean onKey(DialogFragment fragment, int keyCode, KeyEvent event);
    }

    public interface ClickCallback {
        /**
         * This method will be invoked when a button in the dialog is clicked.
         *
         * @param fragment the dialog that received the click
         * @param which    the button that was clicked (ex.
         *                 {@link DialogInterface#BUTTON_POSITIVE}) or the position
         *                 of the item clicked
         */
        void onClick(DialogFragment fragment, int which);
    }

    /**
     * 构建器
     */
    public static class Builder {

        private final Context mContext;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private int mIcon;
        private CharSequence mPositive;
        private CharSequence mNegative;
        private CharSequence mNeutral;
        private boolean mCancelable;
        private int mLayoutResId;

        public Builder(@NonNull Context context) {
            mContext = context;
        }

        /**
         * Set the title using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@StringRes int titleId) {
            mTitle = mContext.getText(titleId);
            return this;
        }

        /**
         * Set the title displayed in the {@link Dialog}.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@Nullable CharSequence title) {
            mTitle = title;
            return this;
        }

        /**
         * Set the message to display using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(@StringRes int messageId) {
            mMessage = mContext.getText(messageId);
            return this;
        }

        /**
         * Set the message to display.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(@Nullable CharSequence message) {
            mMessage = message;
            return this;
        }

        /**
         * Set the resource id of the {@link Drawable} to be used in the title.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setIcon(@DrawableRes int iconId) {
            mIcon = iconId;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the positive button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(@StringRes int textId) {
            mPositive = mContext.getText(textId);
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param text The text to display in the positive button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(CharSequence text) {
            mPositive = text;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the negative button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(@StringRes int textId) {
            mNegative = mContext.getText(textId);
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param text The text to display in the negative button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(CharSequence text) {
            mNegative = text;
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the neutral button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(@StringRes int textId) {
            mNeutral = mContext.getText(textId);
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         *
         * @param text The text to display in the neutral button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(CharSequence text) {
            mNeutral = text;
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        /**
         * Set a custom view resource to be the contents of the Dialog. The
         * resource will be inflated, adding all top-level views to the screen.
         *
         * @param layoutResId Resource ID to be inflated.
         * @return this Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setView(int layoutResId) {
            mLayoutResId = layoutResId;
            return this;
        }

        /**
         * Creates an {@link AppCompatAlertDialogFragment} with the arguments supplied to this
         * builder.
         * <p>
         * Calling this method does not display the dialog. If no additional
         * processing is needed, {@link #show(FragmentManager, String)} may be called instead to both
         * create and display the dialog.
         */
        public AppCompatAlertDialogFragment create() {
            final Bundle arguments = new Bundle();
            if (mTitle != null) {
                arguments.putCharSequence(KEY_TITLE, mTitle);
            }
            if (mMessage != null) {
                arguments.putCharSequence(KEY_MESSAGE, mMessage);
            }
            if (mIcon != 0) {
                arguments.putInt(KEY_ICON, mIcon);
            }
            if (mPositive != null) {
                arguments.putCharSequence(KEY_POSITIVE, mPositive);
            }
            if (mNegative != null) {
                arguments.putCharSequence(KEY_NEGATIVE, mNegative);
            }
            if (mNeutral != null) {
                arguments.putCharSequence(KEY_NEUTRAL, mNeutral);
            }
            arguments.putBoolean(KEY_CANCELABLE, mCancelable);
            if (mLayoutResId != 0) {
                arguments.putInt(KEY_LAYOUT, mLayoutResId);
            }
            final AppCompatAlertDialogFragment fragment = new AppCompatAlertDialogFragment();
            fragment.setArguments(arguments);
            return fragment;
        }

        /**
         * Creates an {@link AppCompatAlertDialogFragment} with the arguments supplied to this
         * builder and immediately displays the dialog.
         */
        public AppCompatAlertDialogFragment show(@NonNull FragmentManager manager, @Nullable String tag) {
            final AppCompatAlertDialogFragment fragment = create();
            fragment.show(manager, tag);
            return fragment;
        }
    }

    private class InnerOnCancelListener implements DialogInterface.OnCancelListener {

        @Override
        public void onCancel(DialogInterface dialog) {
            final androidx.fragment.app.Fragment parent = getParentFragment();
            if (parent instanceof CancelCallback) {
                ((CancelCallback) parent).onCancel(AppCompatAlertDialogFragment.this);
                return;
            }
            final FragmentActivity activity = getActivity();
            if (activity instanceof CancelCallback) {
                ((CancelCallback) activity).onCancel(AppCompatAlertDialogFragment.this);
            }
        }
    }

    private class InnerOnDismissListener implements DialogInterface.OnDismissListener {

        @Override
        public void onDismiss(DialogInterface dialog) {
            final androidx.fragment.app.Fragment parent = getParentFragment();
            if (parent instanceof DismissCallback) {
                ((DismissCallback) parent).onDismiss(AppCompatAlertDialogFragment.this);
                return;
            }
            final FragmentActivity activity = getActivity();
            if (activity instanceof DismissCallback) {
                ((DismissCallback) activity).onDismiss(AppCompatAlertDialogFragment.this);
            }
        }
    }

    private class InnerOnKeyListener implements DialogInterface.OnKeyListener {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            final androidx.fragment.app.Fragment parent = getParentFragment();
            if (parent instanceof KeyCallback) {
                return ((KeyCallback) parent).onKey(AppCompatAlertDialogFragment.this, keyCode, event);
            }
            final FragmentActivity activity = getActivity();
            if (activity instanceof KeyCallback) {
                return ((KeyCallback) activity).onKey(AppCompatAlertDialogFragment.this, keyCode, event);
            }
            return false;
        }
    }

    private class InnerOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            final androidx.fragment.app.Fragment parent = getParentFragment();
            if (parent instanceof ClickCallback) {
                ((ClickCallback) parent).onClick(AppCompatAlertDialogFragment.this, which);
                return;
            }
            final FragmentActivity activity = getActivity();
            if (activity instanceof ClickCallback) {
                ((ClickCallback) activity).onClick(AppCompatAlertDialogFragment.this, which);
            }
        }
    }
}
